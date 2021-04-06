package com.unidata.mdm.backend.service.cleanse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.context.DQContext;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataQualityStatus;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractArrayAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DateSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimeSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.data.DataQualityStatusType;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.ConstantValueDef;
import com.unidata.mdm.meta.DQRRaiseDef;
import com.unidata.mdm.meta.DQRuleClass;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DQRuleType;
import com.unidata.mdm.meta.Port;
import com.unidata.mdm.meta.SeverityType;

/**
 * The Class DataQualityServiceImpl.
 */
@Component
public class DataQualityServiceImpl implements DataQualityServiceExt {

	/** The cfs. */
	@Autowired
	private CleanseFunctionServiceExt cfs;
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DataQualityServiceImpl.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.unidata.mdm.backend.service.cleanse.DataQualityService#applyRules(com
	 * .unidata.mdm.backend.service.cleanse.DQContext)
	 */
	@Override
	public void applyRules(DQContext<DataRecord> ctx) {
		Map<String, Attribute> input = extract(ctx.getRules(), ctx.getRecord(), "");
		ctx.withErrors(apply(ctx, input));

		if (ctx.getRules().stream().anyMatch(r -> r.getType().contains(DQRuleType.ENRICH))) {
			replace(input, ctx);
		}
	}

	/**
	 *
	 *
	 * @param ctx
	 *            the ctx
	 * @param input
	 *            the result
	 * @return the list
	 */
	public List<DataQualityError> apply(DQContext<DataRecord> ctx, Map<String, Attribute> input) {
		List<DataQualityError> errors = new ArrayList<>();
		// iterate over ordered stream with DQ rules
		ctx.getRules().stream().sorted(Comparator.comparing(DQRuleDef::getOrder)).forEachOrdered(r -> {
			// Map contains cleanse function input
			Map<String, Object> cfInput = new HashMap<>();
			cfInput.put(DataQualityServiceExt.$ETALON_RECORD_ID, ctx.getRecordId() == null ? "" : ctx.getRecordId());
			cfInput.put(DataQualityServiceExt.$ENTITY, ctx.getEntityName());
			cfInput.put(DataQualityServiceExt.$FROM, ctx.getRecordValidFrom());
			cfInput.put(DataQualityServiceExt.$TO, ctx.getRecordValidTo());
			cfInput.put(DataQualityServiceExt.$USER_STORAGE, ctx.getUserStorage());
			// Mapping between cleanse function output ports and attributes
			// names
			Map<String, String> outputPorts = new HashMap<>();
			// Iterate over DQ rule mappings
			r.getDqrMapping().forEach(rm -> {
				// Fill input map for cleanse function
				if (!StringUtils.isEmpty(rm.getInputPort())) {
					cfInput.put(rm.getInputPort(),
							rm.getAttributeConstantValue() == null ? input.get(rm.getAttributeName())
									: extractConstantValue(rm.getAttributeConstantValue(), rm.getAttributeName()));
				}
				// Fill set with output ports
				if (!StringUtils.isEmpty(rm.getOutputPort())) {
					outputPorts.put(rm.getOutputPort(), rm.getAttributeName());
				}
			});
			try {
				// Cleanse function input validation errors
				List<DataQualityError> cfIVE = validateInput(cfInput, r);
				// If all required ports set execute cleanse function
				if (CollectionUtils.isEmpty(cfIVE)) {
					exec(input, errors, r, cfInput, outputPorts);
				} else {
					errors.addAll(cfIVE);
				}

				// If any exception occurs while cleanse function execution skip
				// rule and create DQ error
			} catch (Exception e) {
				final String log = "DQ error: category [{}], status [{}], message [{}], severity [{}], rule name [{}].";
				LOGGER.warn(log, DQUtils.CATEGORY_SYSTEM,
						DataQualityStatusType.NEW.name(), "Execution error in cleanse function ("
								+ r.getCleanseFunctionName() + ") '" + e.getMessage() + "'",
						SeverityType.CRITICAL.name(), r.getName());
				errors.add(DataQualityError.builder().category(DQUtils.CATEGORY_SYSTEM)
						.status(DataQualityStatus.NEW).message("Execution error in cleanse function "
								+ r.getCleanseFunctionName() + "\n Stacktrace: \n" +String.join("\n", ExceptionUtils.getRootCauseStackTrace(e)))
						.severity(SeverityType.CRITICAL.name()).ruleName(r.getName()).build());

			}
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DQ errors summary for this context:");
			for (DataQualityError error : errors) {
				final String log = "DQ error: category [{}], status [{}], message [{}], severity [{}], rule name [{}].";
				LOGGER.warn(log, error.getCategory(), error.getStatus().name(), error.getMessage(), error.getSeverity(),
						error.getRuleName());
			}
		}

		return errors;
	}

	/**
     * Execute dq rule.
     *
     * @param input
     *            cf input.
     * @param errors
     *            list with errors.
     * @param r
     *            rule to execute.
     * @param cfInput
     *            cleanse function input.
     * @param outputPorts
     *            map with output ports.
     * @throws CleanseFunctionExecutionException
     *             if exception occurs during cleanse function execution.
     */
    private void exec(Map<String, Attribute> input, List<DataQualityError> errors, DQRuleDef r,
            Map<String, Object> cfInput, Map<String, String> outputPorts) throws CleanseFunctionExecutionException {
    	//UN-4809
		CleanseFunctionExtendedDef cfDef = cfs.getByID(r.getCleanseFunctionName());
		final Map<String, Object> cleaned = new HashMap<>();
		cfInput.forEach((k,v)->{
			if(v instanceof DQAttributeWrapper){
				cleaned.put(k, ((DQAttributeWrapper)v).getPure());
			}else{
				cleaned.put(k, v);
			}
		});
		cfInput.putAll(cleaned);
		List<Port> ips = cfDef.getInputPorts();
		for (Port ip : ips) {
			if (ip.isRequired() && (!cfInput.containsKey(ip.getName()) || cfInput.get(ip.getName()) == null
					|| getValueByPort(ip.getName(), ips, cfInput) == null)) {
				return;
			}
		}
        Map<String, Object> interimResult = cfs.executeSingle(cfInput, r.getCleanseFunctionName());
        interimResult.keySet().forEach(ik -> {

            if (outputPorts.isEmpty() || !outputPorts.containsKey(ik)) {
                return;
            }

            String elemPath = outputPorts.get(ik);
            Attribute result = (Attribute) interimResult.get(ik);

            if (result == null || StringUtils.isBlank(elemPath)) {
                return;
            }

            if (result.getAttributeType() == AttributeType.SIMPLE) {
                SimpleAttribute<?> simple = (SimpleAttribute<?>) result;
                SimpleAttribute<?> origin = AbstractSimpleAttribute.of(simple.getDataType(),
                        ModelUtils.subAttributePath(ModelUtils.getAttributeLevel(elemPath), elemPath),
                        simple.getValue());

                input.put(elemPath, new DQAttributeWrapperImpl(origin, true));
            } else if (result.getAttributeType() == AttributeType.ARRAY) {
                ArrayAttribute<?> array = (ArrayAttribute<?>) result;
                ArrayAttribute<?> origin = AbstractArrayAttribute.of(array.getDataType(),
                        ModelUtils.subAttributePath(ModelUtils.getAttributeLevel(elemPath), elemPath), array.toArray());

                input.put(elemPath, new DQAttributeWrapperImpl(origin, true));
            }
        });

        // Otherwise create error
        errors.addAll(validateOutput(interimResult, r));
    }

	/**
	 * Validate cleanse function input. If required port(s) not set, data
	 * quality error will be created.
	 *
	 * @param input
	 *            Input data.
	 * @param ruleDef
	 *            data quality rule.
	 * @return List with data quality error(s).
	 */
	private List<DataQualityError> validateInput(Map<String, Object> input, DQRuleDef ruleDef) {

		List<DataQualityError> errors = new ArrayList<>();
		CleanseFunctionExtendedDef cfDef = cfs.getByID(ruleDef.getCleanseFunctionName());

		if (cfDef == null) {
			errors.add(DataQualityError.builder().category(DQUtils.CATEGORY_SYSTEM).status(DataQualityStatus.NEW)
					.message("Cleanse function " + ruleDef.getCleanseFunctionName() + " not found!")
					.severity(SeverityType.CRITICAL.name()).ruleName(ruleDef.getName()).build());
		}
		// UN-4809
		// else {
		// cfDef.getInputPorts().forEach(ip -> {
		// if (ip.isRequired() &&
		// (!input.containsKey(ip.getName())||input.get(ip.getName())==null)) {
		// errors.add(
		// DataQualityError.builder()
		// .category("SYSTEM")
		// .status(DataQualityStatus.NEW)
		// .message("Не заполнены поля, которые являются обязательными для
		// правила качества.")
		// .severity(SeverityType.CRITICAL.name())
		// .ruleName(ruleDef.getName())
		// .build());
		// }
		// });
		// }
		return errors;
	}

	/**
	 * Validate cleanse function output. If no re
	 *
	 * @param output
	 *            the output
	 * @param ruleDef
	 *            the rule def
	 * @return the list
	 */
	private List<DataQualityError> validateOutput(Map<String, Object> output, DQRuleDef ruleDef) {

		List<DataQualityError> errors = new ArrayList<>();
		if (!ruleDef.getType().contains(DQRuleType.VALIDATE)) {
			return errors;
		}

		DQRRaiseDef raiseDef = ruleDef.getRaise();
		if(((SimpleAttribute<?>) output.get(raiseDef.getFunctionRaiseErrorPort()))==null||((SimpleAttribute<?>) output.get(raiseDef.getFunctionRaiseErrorPort())).getValue()==null) {
			String message = "Cleanse function "+ruleDef.getCleanseFunctionName()+" didn't return required value for port "+raiseDef.getFunctionRaiseErrorPort()+"!";
			String ruleName = ruleDef.getName();
			String severity = SeverityType.CRITICAL.name();
			String category = "SYSTEM";

			errors.add(DataQualityError.builder().category(category).status(DataQualityStatus.NEW).message(message)
					.severity(severity).ruleName(ruleName).build());
			return errors;
		}
		boolean isValid = ((SimpleAttribute<?>) output.get(raiseDef.getFunctionRaiseErrorPort())).castValue();

		if (!isValid) {
			String message = StringUtils.isEmpty(raiseDef.getMessageText())
					? ((SimpleAttribute<?>) output.get(raiseDef.getMessagePort())).castValue()
					: ruleDef.getRClass() == DQRuleClass.SYSTEM
					    ? DQUtils.extractSystemDQRaiseMessageText(raiseDef.getMessageText())
					    : raiseDef.getMessageText();
			String ruleName = ruleDef.getName();
			String severity = !StringUtils.isEmpty(raiseDef.getSeverityPort())
					? ((SimpleAttribute<?>) output.get(raiseDef.getSeverityPort())).castValue()
					: raiseDef.getSeverityValue().name();
			String category = !StringUtils.isEmpty(raiseDef.getCategoryPort())
					? ((SimpleAttribute<?>) output.get(raiseDef.getCategoryPort())).castValue()
					: raiseDef.getCategoryText();

			errors.add(DataQualityError.builder().category(category).status(DataQualityStatus.NEW).message(message)
					.severity(severity).ruleName(ruleName).build());
		}

		return errors;
	}

	private SimpleAttribute<?> extractConstantValue(ConstantValueDef cvd, String attrName) {

		if (cvd.getType() == null) {
			return null;
		}

		switch (cvd.getType()) {
		case BOOLEAN:
			return new BooleanSimpleAttributeImpl(attrName, cvd.isBoolValue());
		case DATE:
			return new DateSimpleAttributeImpl(attrName, cvd.getDateValue());
		case INTEGER:
			return new IntegerSimpleAttributeImpl(attrName, cvd.getIntValue());
		case NUMBER:
			return new NumberSimpleAttributeImpl(attrName, cvd.getNumberValue());
		case STRING:
			return new StringSimpleAttributeImpl(attrName, cvd.getStringValue());
		case TIME:
			return new TimeSimpleAttributeImpl(attrName, cvd.getTimeValue());
		case TIMESTAMP:
			return new TimestampSimpleAttributeImpl(attrName, cvd.getTimestampValue());
		default:
			break;
		}

		return null;
	}
}

// REIMPLEMENT
interface DQAttributeWrapper extends Attribute {
	boolean isOutputPort();

	Attribute getPure();
}

class DQAttributeWrapperImpl implements DQAttributeWrapper {
	private Attribute attr;
	boolean isOutputPort = false;

	DQAttributeWrapperImpl(Attribute attr, boolean isOutputPort) {

		this.isOutputPort = isOutputPort;
		this.attr = attr;
	}

	@Override
	public boolean isOutputPort() {
		return isOutputPort;
	}

	@Override
	public Attribute getPure() {
		return attr;
	}

	@Override
	public String getName() {
		return attr.getName();
	}

	@Override
	public DataRecord getRecord() {
		return attr.getRecord();
	}

	@Override
	public void setRecord(DataRecord record) {
		attr.setRecord(record);
	}

	@Override
	public AttributeType getAttributeType() {
		return attr.getAttributeType();
	}
}

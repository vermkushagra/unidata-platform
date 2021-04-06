package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.meta.DQActionDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQApplicableDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQEnrichDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRMappingDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQROriginsDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRRaiseDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQRuleDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.DQTypeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.PhaseDefinition;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.service.cleanse.DQUtils;
import com.unidata.mdm.meta.DQApplicableType;
import com.unidata.mdm.meta.DQRActionType;
import com.unidata.mdm.meta.DQREnrichDef;
import com.unidata.mdm.meta.DQRMappingDef;
import com.unidata.mdm.meta.DQROriginsDef;
import com.unidata.mdm.meta.DQRPhaseType;
import com.unidata.mdm.meta.DQRRaiseDef;
import com.unidata.mdm.meta.DQRSourceSystemRef;
import com.unidata.mdm.meta.DQRuleClass;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DQRuleType;

/**
 * The Class DQRuleDefToDQRuleDefinitionConverter.
 *
 * @author Michael Yashin. Created on 11.06.2015.
 */
public class DQRuleDefToDQRuleDefinitionConverter {



	/**
	 * Convert list.
	 *
	 * @param sourceList
	 *            the source list
	 * @param targetList
	 *            the target list
	 */
	public static void convertList(List<DQRuleDef> sourceList, List<DQRuleDefinition> targetList) {
		for (DQRuleDef source : sourceList) {
			DQRuleDefinition target = convert(source);
			targetList.add(target);
		}
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @return the DQ rule definition
	 */
	public static DQRuleDefinition convert(DQRuleDef source) {
		if (source == null) {
			return null;
		}

		DQRuleDefinition target = new DQRuleDefinition();
		target.setId(source.getId());
		target.setName(source.getName());
		target.setDescription(source.getDescription());
		target.setCleanseFunctionName(source.getCleanseFunctionName());
		target.setComplexAttributeName(source.getComplexAttributeName());
		if (source.getOrder() != null) {
			target.setOrder(source.getOrder().intValue());
		}
		mapInputs(source.getDqrMapping(), target.getInputs());
		mapOutputs(source.getDqrMapping(), target.getOutputs());
		target.setOrigins(convertDQROriginsDefinition(source.getOrigins()));
		target.setRaise(convertDQRRaiseDefinition(source.getRaise(), source.getRClass() == DQRuleClass.SYSTEM));
		target.setApplicable(convertApplicable(source.getApplicable()));
		target.setEnrich(convertEnrich(source.getEnrich()));
		target.setDqType(convertDQType(source.getType()));
		target.setSpecial(source.isSpecial());
		return target;
	}

	private static List<DQApplicableDefinition> convertApplicable(List<DQApplicableType> source) {

		List<DQApplicableDefinition> target = new ArrayList<DQApplicableDefinition>();

		for (DQApplicableType appl : source) {
			if (DQApplicableType.ETALON.equals(appl)) {
				target.add(DQApplicableDefinition.ETALON);
			} else if (DQApplicableType.ORIGIN.equals(appl)) {
				target.add(DQApplicableDefinition.ORIGIN);
			}
		}
		return target;
	}

	/**
	 * Convert enrich.
	 *
	 * @param source
	 *            the source
	 * @return the DQ enrich definition
	 */
	private static DQEnrichDefinition convertEnrich(DQREnrichDef source) {
		if (source == null) {
			return null;
		}
		DQEnrichDefinition target = new DQEnrichDefinition();
		target.setPhase(convertPhase(source.getPhase()));
		target.setAction(convertAction(source.getAction()));
		target.setSourceSystem(source.getSourceSystem());
		return target;
	}

	/**
	 * Convert action.
	 *
	 * @param source
	 *            the source
	 * @return the DQ action definition
	 */
	private static DQActionDefinition convertAction(DQRActionType source) {
		if (DQRActionType.CREATE_NEW.equals(source)) {
			return DQActionDefinition.CREATE_NEW;
		} else if (DQRActionType.UPDATE_CURRENT.equals(source)) {
			return DQActionDefinition.UPDATE_CURRENT;
		}
		return DQActionDefinition.UPDATE_CURRENT;
	}

	/**
	 * Convert dq type.
	 *
	 * @param source
	 *            the source
	 * @return the list
	 */
	private static List<DQTypeDefinition> convertDQType(List<DQRuleType> source) {
		if (source == null) {
			return null;
		}
		List<DQTypeDefinition> target = new ArrayList<DQTypeDefinition>();
		for (DQRuleType sourceType : source) {
			if (DQRuleType.ENRICH.equals(sourceType)) {
				target.add(DQTypeDefinition.ENRICH);
			} else if (DQRuleType.VALIDATE.equals(sourceType)) {
				target.add(DQTypeDefinition.VALIDATE);
			}
		}
		return target;
	}

	/**
	 * Convert phase.
	 *
	 * @param source
	 *            the phase
	 * @return the phase definition
	 */
	private static PhaseDefinition convertPhase(DQRPhaseType source) {
		if (DQRPhaseType.AFTER_MERGE.equals(source)) {
			return PhaseDefinition.AFTER_MERGE;
		} else if (DQRPhaseType.BEFORE_UPSERT.equals(source)) {
			return PhaseDefinition.BEFORE_UPSERT;
		} else if (DQRPhaseType.AFTER_UPSERT.equals(source)) {
			return PhaseDefinition.AFTER_UPSERT;
		}
		return null;
	}

	/**
	 * Map inputs.
	 *
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 */
	private static void mapInputs(List<DQRMappingDef> source, List<DQRMappingDefinition> target) {
		for (DQRMappingDef dqrMappingDef : source) {
			if (dqrMappingDef.getInputPort() == null) {
				continue;
			}

			SimpleAttributeRO attr = null;
			if (dqrMappingDef.getAttributeConstantValue() != null
			 && dqrMappingDef.getAttributeConstantValue().getType() != null) {
			    attr = new SimpleAttributeRO();
			    attr.setName(dqrMappingDef.getAttributeName());
			    attr.setType(SimpleDataType.fromValue(dqrMappingDef.getAttributeConstantValue().getType().value().value()));
			    switch (dqrMappingDef.getAttributeConstantValue().getType()) {
                case BOOLEAN:
                    attr.setValue(dqrMappingDef.getAttributeConstantValue().isBoolValue());
                    break;
                case DATE:
                    attr.setValue(ConvertUtils.localDate2Date(dqrMappingDef.getAttributeConstantValue().getDateValue()));
                    break;
                case INTEGER:
                    attr.setValue(dqrMappingDef.getAttributeConstantValue().getIntValue());
                    break;
                case NUMBER:
                    attr.setValue(dqrMappingDef.getAttributeConstantValue().getNumberValue());
                    break;
                case STRING:
                    attr.setValue(dqrMappingDef.getAttributeConstantValue().getStringValue());
                    break;
                case TIME:
                    attr.setValue(ConvertUtils.localTime2Date(dqrMappingDef.getAttributeConstantValue().getTimeValue()));
                    break;
                case TIMESTAMP:
                    attr.setValue(ConvertUtils.localDateTime2Date(dqrMappingDef.getAttributeConstantValue().getTimestampValue()));
                    break;
                default:
                    break;
                }
			}

			DQRMappingDefinition mappingDef = new DQRMappingDefinition();
			mappingDef.setAttributeName(dqrMappingDef.getAttributeName());
			mappingDef.setAttributeConstantValue(attr);
			mappingDef.setFunctionPort(dqrMappingDef.getInputPort());
			target.add(mappingDef);
		}
	}

	/**
	 * Map outputs.
	 *
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 */
	private static void mapOutputs(List<DQRMappingDef> source, List<DQRMappingDefinition> target) {
		for (DQRMappingDef dqrMappingDef : source) {
			if (dqrMappingDef.getOutputPort() == null) {
				continue;
			}
			DQRMappingDefinition mappingDef = new DQRMappingDefinition();
			mappingDef.setAttributeName(dqrMappingDef.getAttributeName());
			mappingDef.setFunctionPort(dqrMappingDef.getOutputPort());
			target.add(mappingDef);
		}
	}

	/**
	 * Convert dqr origins definition.
	 *
	 * @param source
	 *            the source
	 * @return the DQR origins definition
	 */
	private static DQROriginsDefinition convertDQROriginsDefinition(DQROriginsDef source) {
		if (source == null) {
			return null;
		}
		DQROriginsDefinition target = new DQROriginsDefinition();

		target.setAll(source.isAll());

		for (DQRSourceSystemRef sourceSystemRef : source.getSourceSystem()) {
			if (sourceSystemRef.getName() != null) {
				target.getSourceSystems().add(sourceSystemRef.getName());
			}
		}

		return target;
	}

	/**
	 * Convert dqr raise definition.
	 *
	 * @param source
	 *            the source
	 * @param isSystem system rule or not
	 * @return the DQR raise definition
	 */
	private static DQRRaiseDefinition convertDQRRaiseDefinition(DQRRaiseDef source, boolean isSystem) {
		if (source == null) {
			return null;
		}
		DQRRaiseDefinition target = new DQRRaiseDefinition();
		target.setCategoryPort(source.getCategoryPort());
		target.setCategoryText(source.getCategoryText());
		target.setFunctionRaiseErrorPort(source.getFunctionRaiseErrorPort());
		target.setMessagePort(source.getMessagePort());
		target.setMessageText(isSystem
		        ? DQUtils.extractSystemDQRaiseMessageText(source.getMessageText())
		        : source.getMessageText());
		target.setSeverityPort(source.getSeverityPort());
		target.setSeverityValue(source.getSeverityValue() == null
		        ? com.unidata.mdm.meta.SeverityType.CRITICAL.value()
		        : source.getSeverityValue().value());
		target.setPhase(convertPhase(source.getPhase()));
		return target;
	}
}

package com.unidata.mdm.backend.service.cleanse;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MetadataException;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.util.wrappers.AttributesWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.cleanse.common.CleanseConstants;
import com.unidata.mdm.cleanse.string.RegexpUtils;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ConstantValueDef;
import com.unidata.mdm.meta.DQApplicableType;
import com.unidata.mdm.meta.DQRMappingDef;
import com.unidata.mdm.meta.DQROriginsDef;
import com.unidata.mdm.meta.DQRPhaseType;
import com.unidata.mdm.meta.DQRRaiseDef;
import com.unidata.mdm.meta.DQRuleClass;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DQRuleType;
import com.unidata.mdm.meta.SeverityType;
import com.unidata.mdm.meta.SimpleAttributeDef;


/**
 * The Class.
 * @author ilya.bykov
 */
public final class DQUtils {
	/** The logger. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DQUtils.class);
	/**
	 * Internal rule attr prefix.
	 */
	private static final String ATTR_PREFIX = "attr__";
	/**
	 * Category CATEGORY_SYSTEM.
	 */
	public static final String CATEGORY_SYSTEM = "SYSTEM";
	/**
	/**
	 * Instantiates a new DQ utils.
	 */
	private DQUtils() {
		super();
	}

	/**
	 * Creates the consistency rule.
	 *
	 * @param path
	 *            the path
	 * @param attrHolder
	 *            the attr holder
	 * @param order
	 *            the order
	 * @return the DQ rule def
	 */
	private static DQRuleDef createConsistencyRule(String path, AttributeInfoHolder attrHolder, int order) {

		ConstantValueDef attr;
		if (attrHolder.isLookupLink() && attrHolder.isSimple()) {
			SimpleAttributeDef sad = attrHolder.narrow();
			attr = JaxbUtils.getMetaObjectFactory().createConstantValueDef().withStringValue(sad.getLookupEntityType());

		} else if (attrHolder.isLookupLink() && attrHolder.isArray()) {
			ArrayAttributeDef aad = attrHolder.narrow();
			attr = JaxbUtils.getMetaObjectFactory().createConstantValueDef().withStringValue(aad.getLookupEntityType());
		} else {
			final String message = "Consistence check rule cannot be created. The attribute {} in {} is neither lookup link nor simple or array attribute";
			LOGGER.warn(message, attrHolder.getAttribute().getName(), attrHolder.getEntity().getName());
			throw new MetadataException(message, ExceptionId.EX_META_CONSISTENCY_CHECK_RULE_INVALID_INPUT,
					attrHolder.getAttribute().getName(), attrHolder.getEntity().getName());
		}

		return new DQRuleDef().withCleanseFunctionName("Разное.ПроверкаПересеченияИнтервалов")
				.withRClass(DQRuleClass.SYSTEM).withApplicable(Collections.singletonList(DQApplicableType.ETALON))
				.withId(ATTR_PREFIX + path + "__Check_Ref_Range").withName(ATTR_PREFIX + path + "__Check_Ref_Range")
				.withDescription("System rule to check references overlapping.").withOrder(BigInteger.valueOf(order))
				.withSpecial(true).withOrigins(new DQROriginsDef().withAll(true))
				.withRaise(new DQRRaiseDef().withCategoryText(CATEGORY_SYSTEM)
						.withFunctionRaiseErrorPort(CleanseConstants.OUTPUT1).withMessagePort(CleanseConstants.OUTPUT2)
						.withSeverityValue(SeverityType.CRITICAL).withPhase(DQRPhaseType.BEFORE_UPSERT))
				.withType(DQRuleType.VALIDATE)
				.withDqrMapping(new DQRMappingDef().withAttributeName(path).withInputPort(CleanseConstants.INPUT1),
						new DQRMappingDef().withAttributeConstantValue(attr).withInputPort(CleanseConstants.INPUT2));
	}

	/**
	 * Creates the consistency rule.
	 *
	 * @param path
	 *            the path
	 * @param attrHolder
	 *            the attr holder
	 * @param order
	 *            the order
	 * @return the DQ rule def
	 */
	private static DQRuleDef createLinkCheckRule(String path, AttributeInfoHolder attrHolder, int order) {

		ConstantValueDef attr;
		if (attrHolder.isLookupLink() && attrHolder.isSimple()) {
			SimpleAttributeDef sad = attrHolder.narrow();
			attr = JaxbUtils.getMetaObjectFactory().createConstantValueDef().withStringValue(sad.getLookupEntityType());

		} else if (attrHolder.isLookupLink() && attrHolder.isArray()) {
			ArrayAttributeDef aad = attrHolder.narrow();
			attr = JaxbUtils.getMetaObjectFactory().createConstantValueDef().withStringValue(aad.getLookupEntityType());
		} else {
			final String message = "Consistence check rule cannot be created. The attribute {} in {} is either not a lookup link or not a simple or array attribute";
			LOGGER.warn(message, attrHolder.getAttribute().getName(), attrHolder.getEntity().getName());
			throw new MetadataException(message, ExceptionId.EX_META_LINK_CHECK_RULE_INVALID_INPUT,
					attrHolder.getAttribute().getName(), attrHolder.getEntity().getName());
		}

		return new DQRuleDef().withCleanseFunctionName("Разное.ПроверкаСсылки").withRClass(DQRuleClass.SYSTEM)
				.withApplicable(Collections.singletonList(DQApplicableType.ORIGIN))
				.withId(ATTR_PREFIX + path + "__Check_Ref").withName(ATTR_PREFIX + path + "__Check_Ref")
				.withDescription("System rule to check references.").withOrder(BigInteger.valueOf(order))
				.withSpecial(true).withOrigins(new DQROriginsDef().withAll(true))
				.withRaise(new DQRRaiseDef().withCategoryText(CATEGORY_SYSTEM)
						.withFunctionRaiseErrorPort(CleanseConstants.OUTPUT1).withMessagePort(CleanseConstants.OUTPUT2)
						.withSeverityValue(SeverityType.CRITICAL).withPhase(DQRPhaseType.BEFORE_UPSERT))
				.withType(DQRuleType.VALIDATE)
				.withDqrMapping(new DQRMappingDef().withAttributeName(path).withInputPort(CleanseConstants.INPUT1),
						new DQRMappingDef().withAttributeConstantValue(attr).withInputPort(CleanseConstants.INPUT2));
	}

	/**
	 * Creates the unique check.
	 *
	 * @param path
	 *            the path
	 * @param attributeHolder
	 *            the attribute holder
	 * @param order
	 *            the order
	 * @param isCode
	 *            the is code
	 * @return the DQ rule def
	 */
	private static final DQRuleDef createUniqueRule(String path, AttributeInfoHolder attributeHolder, int order,
			boolean isCode) {

		ConstantValueDef attr = JaxbUtils.getMetaObjectFactory().createConstantValueDef().withBoolValue(isCode);

		return new DQRuleDef()
		        .withCleanseFunctionName("Разное.ПроверкаДубликатов")
		        .withRClass(DQRuleClass.SYSTEM)
				.withApplicable(Collections.singletonList(DQApplicableType.ORIGIN))
				.withId(ATTR_PREFIX + path + "__Check_Unique").withName(ATTR_PREFIX + path + "__Check_Unique")
				.withDescription("System rule to check unique attribute.").withOrder(BigInteger.valueOf(order))
				.withSpecial(true)
				// mb will be another
				.withOrigins(new DQROriginsDef().withAll(true))
				.withRaise(new DQRRaiseDef()
				        .withCategoryText(CATEGORY_SYSTEM)
						.withFunctionRaiseErrorPort(CleanseConstants.OUTPUT1)
						.withMessageText(String.join("|", "app.cleanse.validation.unique", attributeHolder.getAttribute().getDisplayName())) // Ugly stuff
						.withSeverityValue(SeverityType.CRITICAL)
						.withPhase(DQRPhaseType.BEFORE_UPSERT))
				.withType(DQRuleType.VALIDATE)
				.withDqrMapping(new DQRMappingDef().withAttributeName(path).withInputPort(CleanseConstants.INPUT1),
						new DQRMappingDef().withAttributeConstantValue(attr).withInputPort("IS_CODE_ATTR"));
	}

	/**
	 * Creates the mask rule.
	 *
	 * @param path
	 *            the path
	 * @param mask
	 *            the mask
	 * @param order
	 *            the order
	 * @return the DQ rule def
	 */
	private static final DQRuleDef createValueMaskRule(String path, String mask, int order, boolean isRequired) {

		ConstantValueDef maskAttr = JaxbUtils.getMetaObjectFactory().createConstantValueDef().withStringValue(mask);

		ConstantValueDef regexAttr = JaxbUtils.getMetaObjectFactory().createConstantValueDef()
				.withStringValue(RegexpUtils.convertMaskToRegexString(mask));

	      ConstantValueDef reqAttr = JaxbUtils.getMetaObjectFactory().createConstantValueDef()
	                .withBoolValue(isRequired);

		return JaxbUtils.getMetaObjectFactory().createDQRuleDef().withCleanseFunctionName("Строковые.ПроверкаПоМаске")
				.withRClass(DQRuleClass.SYSTEM).withApplicable(DQApplicableType.ORIGIN, DQApplicableType.ETALON)
				.withId(ATTR_PREFIX + path + "__Check_Mask").withName(ATTR_PREFIX + path + "__Check_Mask")
				.withDescription("System rule to check value by mask.").withOrder(BigInteger.valueOf(order))
				.withSpecial(true).withOrigins(new DQROriginsDef().withAll(true))
				.withRaise(new DQRRaiseDef().withCategoryText(CATEGORY_SYSTEM)
						.withFunctionRaiseErrorPort(CleanseConstants.OUTPUT1).withMessagePort(CleanseConstants.OUTPUT2)
						.withSeverityValue(SeverityType.CRITICAL).withPhase(DQRPhaseType.BEFORE_UPSERT))
				.withType(DQRuleType.VALIDATE)
				.withDqrMapping(new DQRMappingDef().withAttributeName(path).withInputPort(CleanseConstants.INPUT3),
						new DQRMappingDef().withAttributeConstantValue(maskAttr)
						.withInputPort(CleanseConstants.INPUT2),
						new DQRMappingDef().withAttributeConstantValue(regexAttr)
								.withInputPort(CleanseConstants.INPUT1),
								new DQRMappingDef().withAttributeConstantValue(reqAttr)
                                .withInputPort(CleanseConstants.INPUT4));
	}

	/**
	 * Creates the required rule.
	 *
	 * @param path
	 *            the path
	 * @param order
	 *            the order
	 * @return the DQ rule def
	 */
	private static final DQRuleDef createRequiredRule(String path, int order) {
		return new DQRuleDef().withCleanseFunctionName("Разное.ПроверкаСуществованияАтрибута")
				.withRClass(DQRuleClass.SYSTEM).withApplicable(Collections.singletonList(DQApplicableType.ORIGIN))
				.withId(ATTR_PREFIX + path + "__Check_Exists").withName(ATTR_PREFIX + path + "__Check_Exists")
				.withDescription("System rule to check attribute existence.").withOrder(BigInteger.valueOf(order))
				.withSpecial(true)
				// mb will be another
				.withOrigins(new DQROriginsDef().withAll(true))
				.withRaise(new DQRRaiseDef().withCategoryText(CATEGORY_SYSTEM)
						.withFunctionRaiseErrorPort(CleanseConstants.OUTPUT1)
						.withMessageText("Обязательный атрибут не задан.").withSeverityValue(SeverityType.CRITICAL)
						.withPhase(DQRPhaseType.BEFORE_UPSERT))
				.withType(DQRuleType.VALIDATE)
				.withDqrMapping(new DQRMappingDef().withAttributeName(path).withInputPort(CleanseConstants.INPUT1));
	}

	/**
	 * Adjust entity system rules.
	 *
	 * @param aw
	 *            the wrapper
	 * @param rules
	 *            the rules
	 */
	public static void addSystemRules(AttributesWrapper aw, List<DQRuleDef> rules) {

        for (Entry<String, AttributeInfoHolder> entry : aw.getAttributes().entrySet()) {
            if (entry.getValue().hasMask()) {
                String mask = entry.getValue().isSimple()
                        ? ((SimpleAttributeDef) entry.getValue().getAttribute()).getMask()
                        : entry.getValue().isCode() ? ((CodeAttributeDef) entry.getValue().getAttribute()).getMask()
                                : ((ArrayAttributeDef) entry.getValue().getAttribute()).getMask();
                boolean isRequired = false;
                if (entry.getValue().getAttribute() instanceof ArrayAttributeDef) {
                    isRequired = !((ArrayAttributeDef) entry.getValue().getAttribute()).isNullable();
                } else if (entry.getValue().getAttribute() instanceof CodeAttributeDef) {
                    isRequired = !((CodeAttributeDef) entry.getValue().getAttribute()).isNullable();
                } else if (entry.getValue().getAttribute() instanceof SimpleAttributeDef) {
                    isRequired = !((SimpleAttributeDef) entry.getValue().getAttribute()).isNullable();
                }
                rules.add(createValueMaskRule(entry.getKey(), mask, rules.size() + 1, isRequired));
            }

			if (entry.getValue().isUnique()) {
				rules.add(createUniqueRule(entry.getKey(), entry.getValue(), rules.size() + 1,
						entry.getValue().isCode()));
			}

			if (entry.getValue().isLookupLink()) {
				rules.add(createConsistencyRule(entry.getKey(), entry.getValue(), rules.size() + 1));
				rules.add(createLinkCheckRule(entry.getKey(), entry.getValue(), rules.size() + 1));
			}
		}
	}

	/**
	 * Filter system rules.
	 *
	 * @param aw            the ew
	 * @param rules            the rules
	 */
	public static void removeSystemRules(AttributesWrapper aw, List<DQRuleDef> rules) {
		if (rules == null) {
			return;
		}
		rules.removeIf(r -> r.getRClass() == DQRuleClass.SYSTEM || r.isSpecial()
				|| r.getDqrMapping().stream().anyMatch(m -> StringUtils.isNotBlank(m.getAttributeName())
						&& !aw.getAttributes().containsKey(m.getAttributeName())));
	}

    // Ugly stuff
    public static String extractSystemDQRaiseMessageText(String text) {
    
        if (StringUtils.isBlank(text)) {
            return null;
        }
    
        String[] sepSplit = StringUtils.split(text, '|');
        if (sepSplit == null || sepSplit.length < 2) {
            return text;
        }
    
        return MessageUtils.getMessage(
                sepSplit[0],
                (Object[])
                Arrays.copyOfRange(sepSplit, 1, sepSplit.length));
    }
}

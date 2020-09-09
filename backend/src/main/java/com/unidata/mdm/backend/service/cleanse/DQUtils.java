/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.cleanse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.MetadataException;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.types.impl.DQRMappingDefImpl;
import com.unidata.mdm.backend.common.types.impl.DQRuleDefImpl;
import com.unidata.mdm.backend.common.upath.UPathApplicationMode;
import com.unidata.mdm.backend.common.upath.UPathExecutionContext;
import com.unidata.mdm.backend.service.data.upath.UPathService;
import com.unidata.mdm.backend.service.model.util.wrappers.AttributesWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.DataQualityWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.cleanse.common.CleanseConstants;
import com.unidata.mdm.cleanse.string.RegexpUtils;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ConstantValueDef;
import com.unidata.mdm.meta.DQApplicableType;
import com.unidata.mdm.meta.DQCleanseFunctionPortApplicationMode;
import com.unidata.mdm.meta.DQROriginsDef;
import com.unidata.mdm.meta.DQRPhaseType;
import com.unidata.mdm.meta.DQRRaiseDef;
import com.unidata.mdm.meta.DQRuleClass;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DQRuleExecutionContext;
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
     * Hack.
     */
    private static UPathService upathService;
    /**
     * Hack 2.
     */
    private static MetaModelService metaModelServive;
    /**
    /**
     * Instantiates a new DQ utils.
     */
    private DQUtils() {
        super();
    }
    /**
     * Init-like static method.
     * @param ctx the spring application context
     */
    public static void init(ApplicationContext ctx) {
        DQUtils.upathService = ctx.getBean(UPathService.class);
        DQUtils.metaModelServive = ctx.getBean(MetaModelService.class);
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

        return JaxbUtils.getMetaObjectFactory().createDQRuleDef()
                .withCleanseFunctionName("Разное.ПроверкаПересеченияИнтервалов")
                .withRClass(DQRuleClass.SYSTEM)
                .withApplicable(Collections.singletonList(DQApplicableType.ETALON))
                .withName(ATTR_PREFIX + path + "__Check_Ref_Range")
                .withDescription("System rule to check references overlapping.").withOrder(BigInteger.valueOf(order))
                .withSpecial(true).withOrigins(new DQROriginsDef().withAll(true))
                .withRaise(
                        JaxbUtils.getMetaObjectFactory().createDQRRaiseDef()
                            .withCategoryText(CATEGORY_SYSTEM)
                            .withFunctionRaiseErrorPort(CleanseConstants.OUTPUT1)
                            .withMessagePort(CleanseConstants.OUTPUT2)
                            .withSeverityValue(SeverityType.CRITICAL)
                            .withPhase(DQRPhaseType.BEFORE_UPSERT))
                .withType(DQRuleType.VALIDATE)
                .withDqrMapping(
                        JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                            .withAttributeName(path)
                            .withInputPort(CleanseConstants.INPUT1),
                        JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                            .withAttributeConstantValue(attr)
                            .withInputPort(CleanseConstants.INPUT2));
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

        return JaxbUtils.getMetaObjectFactory().createDQRuleDef()
                .withCleanseFunctionName("Разное.ПроверкаСсылки")
                .withRClass(DQRuleClass.SYSTEM)
                .withApplicable(Collections.singletonList(DQApplicableType.ORIGIN))
                .withName(ATTR_PREFIX + path + "__Check_Ref")
                .withDescription("System rule to check references.").withOrder(BigInteger.valueOf(order))
                .withSpecial(true)
                .withOrigins(new DQROriginsDef().withAll(true))
                .withRaise(
                        JaxbUtils.getMetaObjectFactory().createDQRRaiseDef()
                            .withCategoryText(CATEGORY_SYSTEM)
                            .withFunctionRaiseErrorPort(CleanseConstants.OUTPUT1)
                            .withMessagePort(CleanseConstants.OUTPUT2)
                            .withSeverityValue(SeverityType.CRITICAL)
                            .withPhase(DQRPhaseType.BEFORE_UPSERT))
                .withType(DQRuleType.VALIDATE)
                .withDqrMapping(JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                            .withAttributeName(path)
                            .withInputPort(CleanseConstants.INPUT1),
                        JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                            .withAttributeConstantValue(attr)
                            .withInputPort(CleanseConstants.INPUT2));
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

        return JaxbUtils.getMetaObjectFactory().createDQRuleDef()
                .withCleanseFunctionName("Разное.ПроверкаДубликатов")
                .withRClass(DQRuleClass.SYSTEM)
                .withApplicable(Collections.singletonList(DQApplicableType.ORIGIN))
                .withName(ATTR_PREFIX + path + "__Check_Unique")
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
                .withDqrMapping(JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                                .withAttributeName(path)
                                .withInputPort(CleanseConstants.INPUT1),
                            JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                                .withAttributeConstantValue(attr)
                                .withInputPort("IS_CODE_ATTR"));
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

        return JaxbUtils.getMetaObjectFactory().createDQRuleDef()
                .withCleanseFunctionName("Строковые.ПроверкаПоМаске")
                .withRClass(DQRuleClass.SYSTEM)
                .withApplicable(DQApplicableType.ORIGIN, DQApplicableType.ETALON)
                .withName(ATTR_PREFIX + path + "__Check_Mask")
                .withDescription("System rule to check value by mask.").withOrder(BigInteger.valueOf(order))
                .withSpecial(true).withOrigins(new DQROriginsDef().withAll(true))
                .withRaise(new DQRRaiseDef().withCategoryText(CATEGORY_SYSTEM)
                        .withFunctionRaiseErrorPort(CleanseConstants.OUTPUT1).withMessagePort(CleanseConstants.OUTPUT2)
                        .withSeverityValue(SeverityType.CRITICAL).withPhase(DQRPhaseType.BEFORE_UPSERT))
                .withType(DQRuleType.VALIDATE)
                .withDqrMapping(JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                                .withAttributeName(path)
                                .withInputPort(CleanseConstants.INPUT3),
                        JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                                .withAttributeConstantValue(maskAttr)
                                .withInputPort(CleanseConstants.INPUT2),
                        JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                                .withAttributeConstantValue(regexAttr)
                                .withInputPort(CleanseConstants.INPUT1),
                        JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                                .withAttributeConstantValue(reqAttr)
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
        return JaxbUtils.getMetaObjectFactory().createDQRuleDef()
                .withCleanseFunctionName("Разное.ПроверкаСуществованияАтрибута")
                .withRClass(DQRuleClass.SYSTEM).withApplicable(Collections.singletonList(DQApplicableType.ORIGIN))
                .withName(ATTR_PREFIX + path + "__Check_Exists")
                .withDescription("System rule to check attribute existence.").withOrder(BigInteger.valueOf(order))
                .withSpecial(true)
                // mb will be another
                .withOrigins(new DQROriginsDef().withAll(true))
                .withRaise(new DQRRaiseDef().withCategoryText(CATEGORY_SYSTEM)
                        .withFunctionRaiseErrorPort(CleanseConstants.OUTPUT1)
                        .withMessageText("Обязательный атрибут не задан.").withSeverityValue(SeverityType.CRITICAL)
                        .withPhase(DQRPhaseType.BEFORE_UPSERT))
                .withType(DQRuleType.VALIDATE)
                .withDqrMapping(JaxbUtils.getMetaObjectFactory().createDQRMappingDef()
                        .withAttributeName(path)
                        .withInputPort(CleanseConstants.INPUT1));
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

                boolean isRequired = false;
                String mask = null;
                if (entry.getValue().isSimple()) {
                    SimpleAttributeDef sa = entry.getValue().narrow();
                    mask = sa.getMask();
                    isRequired = !sa.isNullable();
                } else if (entry.getValue().isCode()) {
                    CodeAttributeDef ca = entry.getValue().narrow();
                    mask = ca.getMask();
                    isRequired = !ca.isNullable();
                } else {
                    ArrayAttributeDef aa = entry.getValue().narrow();
                    mask = aa.getMask();
                    isRequired = !aa.isNullable();
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

        rules.removeIf(r -> r.getRClass() == DQRuleClass.SYSTEM || r.isSpecial());
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
    /**
     * Does preprocessing of DQ rules.
     * @param entityName the entity name
     * @param rules the rules
     * @param attributes the attribute map
     */
    public static void prepareMappings(String entityName, List<DQRuleDef> rules, Map<String, AttributeInfoHolder> attributes) {

        if (CollectionUtils.isEmpty(rules)) {
            return;
        }

        rules.forEach(rule -> {

            DQRuleDefImpl ruleImpl = (DQRuleDefImpl) rule;
            if (StringUtils.isNotBlank(rule.getExecutionContextPath())) {
                ruleImpl.setUpath(upathService.upathCreate(entityName, rule.getExecutionContextPath(), attributes));
            }

            Map<String, DQRMappingDefImpl> input = new HashMap<>();
            Map<String, DQRMappingDefImpl> output = new HashMap<>();
            for (int i = 0; i < rule.getDqrMapping().size(); i++) {

                DQRMappingDefImpl mapping = (DQRMappingDefImpl) rule.getDqrMapping().get(i);
                boolean isInput = StringUtils.isNotBlank(mapping.getInputPort());

                // UPath
                if (Objects.isNull(mapping.getAttributeConstantValue())
                 && StringUtils.isNotBlank(mapping.getAttributeName())) {
                    mapping.setUpath(upathService.upathCreate(entityName, mapping.getAttributeName(), attributes));
                }

                if (isInput) {
                    input.put(mapping.getInputPort(), mapping);
                } else {
                    output.put(mapping.getOutputPort(), mapping);
                }
            }

            ruleImpl.setInput(input);
            ruleImpl.setOutput(output);
        });
    }
    /**
     * Prepares DQ mappings for factory.
     * @param wrapper the DQ wrapper
     */
    public static void prepareDataQualityWrapper(DataQualityWrapper wrapper) {

        // 1. Cleanup old
        wrapper.getOriginRules().clear();
        wrapper.getEtalonRules().clear();

        // 2. Check new
        if (CollectionUtils.isEmpty(wrapper.getDataQualities())) {
            return;
        }

        // 3. Init new
        DQUtils.prepareMappings(wrapper.getAbstractEntity().getName(), wrapper.getDataQualities(), wrapper.getAttributes());

        // 4. Build
        wrapper.getOriginRules().putAll(DQUtils.filterForOriginsSorted(wrapper.getDataQualities()));
        wrapper.getEtalonRules().addAll(DQUtils.filterForEtalonsSorted(wrapper.getDataQualities()));
    }
    /**
     * Filters rules for origin application.
     * @param rules the rules to filter
     * @return map
     */
    public static Map<String, List<DQRuleDef>> filterForOriginsSorted(List<DQRuleDef> rules) {

        Map<String, List<DQRuleDef>> origins = new HashMap<>();
        metaModelServive.getSourceSystemsList().forEach(ss -> origins.put(ss.getName(), new ArrayList<>()));

        if (CollectionUtils.isNotEmpty(rules)) {

            rules.stream()
                .filter(rule -> rule.getApplicable().contains(DQApplicableType.ORIGIN))
                .forEach(rule -> {
                    if (rule.getOrigins().isAll()) {
                        origins.entrySet().forEach(e -> e.getValue().add(rule));
                    } else {
                        rule.getOrigins().getSourceSystem().forEach(ss -> origins.get(ss.getName()).add(rule));
                    }
                });

            origins.values().forEach(collected -> Collections.sort(collected, (a1, a2) -> a1.getOrder().intValue() - a2.getOrder().intValue()));
        }

        return origins;
    }
    /**
     * Filters rules for etalon application.
     * @param rules the rules to filter
     * @return list
     */
    public static List<DQRuleDef> filterForEtalonsSorted(List<DQRuleDef> rules) {

        if (CollectionUtils.isNotEmpty(rules)) {
            return rules.stream()
                    .filter(rule -> rule.getApplicable().contains(DQApplicableType.ETALON))
                    .sorted(Comparator.comparing(DQRuleDef::getOrder))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public static UPathExecutionContext ofContext(DQRuleExecutionContext ctx) {

        if (Objects.isNull(ctx)) {
            return null;
        }

        switch (ctx) {
        case GLOBAL:
            return UPathExecutionContext.FULL_TREE;
        case LOCAL:
            return UPathExecutionContext.SUB_TREE;
        default :
            break;
        }

        return null;
    }

    public static UPathApplicationMode ofMode(DQCleanseFunctionPortApplicationMode mode) {

        if (Objects.isNull(mode)) {
            return null;
        }

        switch (mode) {
        case MODE_ALL:
            return UPathApplicationMode.MODE_ALL;
        case MODE_ONCE:
            return UPathApplicationMode.MODE_ONCE;
        case MODE_ALL_WITH_INCOMPLETE:
            return UPathApplicationMode.MODE_ALL_WITH_INCOMPLETE;
        default :
            break;
        }

        return null;
    }
}

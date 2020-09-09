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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.unidata.mdm.backend.common.types.DataQualityCallState;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.DataQualityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.dq.DataQualityExecutionMode;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.service.DataQualityService;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataQualityStatus;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractArrayAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.ComplexAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DQRMappingDefImpl;
import com.unidata.mdm.backend.common.types.impl.DQRuleDefImpl;
import com.unidata.mdm.backend.common.types.impl.DateSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimeSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;
import com.unidata.mdm.backend.common.upath.UPath;
import com.unidata.mdm.backend.common.upath.UPathApplicationMode;
import com.unidata.mdm.backend.common.upath.UPathExecutionContext;
import com.unidata.mdm.backend.common.upath.UPathResult;
import com.unidata.mdm.backend.service.data.driver.EtalonComposer;
import com.unidata.mdm.backend.service.data.driver.EtalonCompositionDriverType;
import com.unidata.mdm.backend.service.data.upath.UPathService;
import com.unidata.mdm.backend.service.data.util.DataUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.CleanseFunctionWrapper;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.data.DataQualityStatusType;
import com.unidata.mdm.meta.ConstantValueDef;
import com.unidata.mdm.meta.DQCleanseFunctionPortApplicationMode;
import com.unidata.mdm.meta.DQRMappingDef;
import com.unidata.mdm.meta.DQRRaiseDef;
import com.unidata.mdm.meta.DQRuleClass;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.DQRuleRunType;
import com.unidata.mdm.meta.DQRuleType;
import com.unidata.mdm.meta.Port;
import com.unidata.mdm.meta.SeverityType;
import com.unidata.mdm.meta.SimpleAttributeDef;

/**
 * The Class DataQualityServiceImpl.
 */
@Component
public class DataQualityServiceImpl implements DataQualityService {
    /**
	 * The cfs.
	 */
	@Autowired
	private CleanseFunctionServiceExt cleanseFunctionService;
	/**
	 * The mms.
	 */
	@Autowired
	private MetaModelServiceExt metaModelService;
	/**
	 * UPath service.
	 */
	@Autowired
	private UPathService upathService;
	/**
	 * The etalon composer.
	 */
    @Autowired
    private EtalonComposer etalonComposer;
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DataQualityServiceImpl.class);
	/**
     * {@inheritDoc}
     */
    @Override
    public void apply(DataQualityContext ctx) {

        List<DQRuleDef> rules = ctx.getRules();
        if (CollectionUtils.isEmpty(rules)) {
            return;
        }

        MeasurementPoint.start();
        try {

            // Run rules
            for (DQRuleDef rule : rules) {

                try {

                    // Check, rule is turned off
                    if (skipRule((DQRuleDefImpl) rule, ctx)) {
						if (ctx.getExecutionMode() == DataQualityExecutionMode.MODE_ONLINE) {
							ctx.getSkippedRules().add(rule.getName());
						}
                        continue;
                    }

                    // Prepare CF context, check general ability to execute rule
                    checkRule(rule, ctx);

                    // Run rule scoped
                    executeRule((DQRuleDefImpl) rule, ctx);

                } catch (Exception e) {
                    handleCleanseFunctionExecutionException(ctx, rule, e);
                }
            }
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Executes rule in the scope, defined by current rule.
     */
    private void executeRule(DQRuleDefImpl rule, DataQualityContext ctx) {

        MeasurementPoint.start();
        try {

            // Create cleanse function context
            CleanseFunctionContext cfc = CleanseFunctionContext.builder(ctx)
                    .ruleName(rule.getName())
                    .cleanseFunctionName(rule.getCleanseFunctionName())
                    .build();

            ContextUtils.storageCopy(ctx, cfc, ctx.keysId());

            // Filter records to execute the rule on for this scope. Return on empty selection.
            List<DataRecord> records = collectRecords(rule, ctx, cfc);
            if (CollectionUtils.isEmpty(records)) {
                return;
            }

            // Narrow CFW, holding port info
            CleanseFunctionWrapper cfw = ctx.getFromStorage(StorageId.DATA_DQ_CURRENT_FUNCTION);

            // State - validation error
            Boolean[] validations = new Boolean[records.size()];
            Boolean[] enrichments = new Boolean[records.size()];
            for (int i = 0; i < records.size(); i++) {

                // For each record do
                DataRecord record = records.get(i);

                // Collect input and output params
                List<CleanseFunctionInputParam> params = collectParams(cfw, rule, record);

                // Possibly clear old values and add new ones
                cfc.output().clear();
                cfc.input().clear();
                cfc.input(params);

                validations[i] = true;
                enrichments[i] = false;

                // Check ability to execute. Skip on inability
                if (skipCycle(rule, ctx, cfc)) {
            		if (ctx.getExecutionMode() == DataQualityExecutionMode.MODE_ONLINE) {
						ctx.getSkippedRules().add(rule.getName());
					}
                    continue;
                }

                // Execute the CF itself
                cleanseFunctionService.execute(cfc);

                // Post-Process call
                Pair<Boolean, Boolean> result = completeCycle(cfw, rule, cfc, record);

                validations[i] = result.getLeft();
                enrichments[i] = result.getRight();
            }

            // Finish rule processing
            finishRule(ctx, cfc, rule, validations, enrichments);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Creates cleanse function execution context.
     * @param rule the rule causing the execution
     * @param ctx the DQ context
     */
    private void checkRule(DQRuleDef rule, DataQualityContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Check the general ability to execute rule
            CleanseFunctionWrapper cfw = metaModelService.getValueById(rule.getCleanseFunctionName(), CleanseFunctionWrapper.class);
            if (Objects.isNull(cfw)) {
                final String message = "Cleanse function '{}' not found in the meta model!";
                LOGGER.warn(message, rule.getCleanseFunctionName());
                throw new CleanseFunctionExecutionException(message, ExceptionId.EX_DQ_CLEANSE_FUNCTION_NOT_FOUND_DQS, rule.getCleanseFunctionName());
            }

            if (!cfw.getCleanseFunctionDef().getSupportedExecutionContexts().contains(rule.getExecutionContext())) {
            	final String message = "Execution context [{}] is not supported by the function [{}], the rule tries to execute";
                LOGGER.warn(message, rule.getExecutionContext(), rule.getCleanseFunctionName());
                throw new CleanseFunctionExecutionException(message, ExceptionId.EX_DQ_EXECUTION_CONTEXT_MODE_NOT_SUPPORTED,
                        rule.getExecutionContext(), rule.getCleanseFunctionName());
            }

            ctx.putToStorage(StorageId.DATA_DQ_CURRENT_FUNCTION, cfw);

            // 2. Prepare data. Create state if needed.
            ensureState(ctx);

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Does post-processing after execution of a clense function.
     * @param dqc global data quality context
     * @param cfc cleanse function execution context to post-process
     * @param rule the rule currently being executed
     * @param validations the validations state
     * @param enrichments enrichments state
     * @param output the output ports map
     */
    private void finishRule(DataQualityContext dqc, CleanseFunctionContext cfc, DQRuleDefImpl rule,
            Boolean[] validations, Boolean[] enrichments) {

        MeasurementPoint.start();
        try {

            // 1. Validate
            boolean isValid = true;
            if (rule.getType().contains(DQRuleType.VALIDATE)) {
                isValid = finishValidate(rule, dqc, cfc, validations);
            }

            // 2. Enrich
            if (rule.getType().contains(DQRuleType.ENRICH) && isValid) {
                finishEnrich(rule, dqc, cfc, enrichments);
            }

            // 3. Collect additional errors
            collect(dqc, cfc);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Tells, whether to skip the rule or not.
     * @param ctx the context
     * @param cfc current CF context
     * @param rule the rule to check
     * @return true, if rule should be skipped, false otherwise
     */
    private boolean skipRule(DQRuleDefImpl rule, DataQualityContext ctx) {

        MeasurementPoint.start();
        try {

            DQRuleRunType runType = rule.getRunType() == null ? DQRuleRunType.RUN_ON_REQUIRED_PRESENT : rule.getRunType();

            // 1. The rule is turned off
            return runType == DQRuleRunType.RUN_NEVER
                    || (rule.getRClass() == DQRuleClass.SYSTEM && rule.isSpecial() && ctx.isSkipConsistencyChecks());
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Tells, whether to skip the rule cycle or not.
     * @param rule the rule to check
     * @param ctx the context
     * @param cfc current CF context
     * @return true, if rule should be skipped, false otherwise
     */
    private boolean skipCycle(DQRuleDefImpl rule, DataQualityContext ctx, CleanseFunctionContext cfc) {

        MeasurementPoint.start();
        try {

            DQRuleRunType runType = rule.getRunType() == null ? DQRuleRunType.RUN_ON_REQUIRED_PRESENT : rule.getRunType();

            // 1. Check run always mode
            if (runType == DQRuleRunType.RUN_ALWAYS) {
                return false;
            }

            // 2. Check required (or all) ports, exit silently, if some required ports are empty (original behaviour).
            CleanseFunctionWrapper cfw = ctx.getFromStorage(StorageId.DATA_DQ_CURRENT_FUNCTION);
            List<Port> inputPorts = cfw.getCleanseFunctionDef().getInputPorts();
            for (Port inputPort : inputPorts) {

                CleanseFunctionParam param = cfc.getInputParamByPortName(inputPort.getName());
                if (Objects.nonNull(param) && !param.isEmpty()) {
                    continue;
                }

                if (runType == DQRuleRunType.RUN_ON_ALL_PRESENT
                 || runType == DQRuleRunType.RUN_ON_REQUIRED_PRESENT && inputPort.isRequired()) {
                    return true;
                }
            }

            return false;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Does post-processing after execution of a clense function.
     * @param rule the rule currently being executed
     * @param cfc cleanse function execution context to post-process
     * @param record the record
     * @param output the output ports map
     */
    private Pair<Boolean, Boolean> completeCycle(CleanseFunctionWrapper cfw, DQRuleDefImpl rule,
            CleanseFunctionContext cfc, DataRecord record) {

        MeasurementPoint.start();
        try {

            // 1. Local validate
            boolean isValid = true;
            boolean hadHits = false;
            if (rule.getType().contains(DQRuleType.VALIDATE)) {

                DQRRaiseDef raise = rule.getRaise();
                CleanseFunctionOutputParam validate = cfc.getOutputParamByPortName(raise.getFunctionRaiseErrorPort());
                if (validate == null || validate.isEmpty()) {
                    LOGGER.warn("Cleanse function [{}] didn't return required value for port [{}]!",
                            rule.getCleanseFunctionName(), raise.getFunctionRaiseErrorPort());
                    isValid = false;
                } else {
                    SimpleAttribute<?> mark = validate.getSingleton();
                    isValid = mark.getDataType() == DataType.BOOLEAN && Boolean.TRUE.equals(mark.castValue());
                }
            }

            // 2. Local enrich
            if (rule.getType().contains(DQRuleType.ENRICH) && isValid) {
                hadHits = applyParams(cfw, rule, cfc, record);
            }

            return new ImmutablePair<>(isValid, hadHits);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Collects records to process.
     * @param rule the rule
     * @param ctx the context
     * @param cfc this function context
     * @return list of records
     */
    private List<DataRecord> collectRecords(DQRuleDefImpl rule, DataQualityContext ctx, CleanseFunctionContext cfc) {

        DataRecord topLevel = selectTopLevelRecord(rule, ctx, cfc);
        if (Objects.isNull(topLevel)) {
            return Collections.emptyList();
        }

        UPath upath = rule.getUpath();
        if (StringUtils.isBlank(rule.getExecutionContextPath())
                || (Objects.nonNull(upath) && upath.isRoot())) {
            return Collections.singletonList(topLevel);
        }

        if (Objects.isNull(upath)) {
            LOGGER.warn("UPath value is null, while executing [{}] rule in [{}]. Skip.", rule.getName(),
                    ctx.keys() != null ? ctx.keys().getEntityName() : ctx.getEntityName());
            return Collections.emptyList();
        } else if (!upath.getElements().get(upath.getElements().size() - 1).getInfo().isComplex()) {
            // TODO throw from MM validations.
        }

        UPathResult result = upathService.upathGet(rule.getUpath(), topLevel, UPathApplicationMode.MODE_ALL);
        List<DataRecord> collected = new ArrayList<>();
        for (Attribute attr : result.getAttributes()) {
            collected.addAll(((ComplexAttribute) attr).toCollection());
        }

        return collected;
    }
    /**
     * Collects input params for cleanse function execution context.
     * @param cfw the wrapper of the current function
     * @param rule the rule causing the execution
     * @param source the data record and attributes source
     * @return resulting execution context
     */
    private List<CleanseFunctionInputParam> collectParams(CleanseFunctionWrapper cfw, DQRuleDefImpl rule, DataRecord source) {

        MeasurementPoint.start();
        try {

            Map<String, DQRMappingDefImpl> input = rule.getInput();
            if (MapUtils.isEmpty(input)) {
                return Collections.emptyList();
            }

            List<CleanseFunctionInputParam> params = new ArrayList<>(input.size());
            for (DQRMappingDefImpl dqrm : input.values()) {

                CleanseFunctionInputParam param = null;
                if (Objects.nonNull(dqrm.getAttributeConstantValue())) {
                    param = ofConstantValue(dqrm);
                } else {
                    Port port = cfw.getInputPortByName(dqrm.getInputPort());
                    param = ofUpathValue(dqrm, source,
                            StringUtils.isNotBlank(rule.getExecutionContextPath()),
                            port.getApplicationMode());
                }

                if (Objects.nonNull(param)) {
                    params.add(param);
                }
            }

            return params;
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Does MB state preparation.
     * @param ctx the context
     */
    private void ensureState(DataQualityContext ctx) {

        MeasurementPoint.start();
        try {

            switch (ctx.getExecutionMode()) {
            case MODE_ORIGIN:
            case MODE_ONLINE:
            case MODE_PREVIEW:

                if (Objects.isNull(ctx.getModificationBox().originState())) {

                    // Origins always modify the same object
                    CalculableHolder<DataRecord> current = ctx.getModificationBox().peek(ctx.toBoxKey());
                    DataRecord state = SerializableDataRecord.of(current.getValue());
                    ctx.getModificationBox().originState(state);
                }
                break;
            case MODE_ETALON:

                if (Objects.isNull(ctx.getModificationBox().etalonState())) {

                    // Etalons are calculated. Small speed-up to skip the very first step.
                    List<CalculableHolder<DataRecord>> base = ctx.getModificationBox().toCalculables();
                    DataRecord state = etalonComposer.compose(EtalonCompositionDriverType.BVT, base, true, false);
                    ctx.getModificationBox().etalonState(state);
                }
                break;
            default:
                break;
            }
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Check validate output.
     * @param rule the rule currently being executed
     * @param cfc cleanse function execution context to validate
     * @param validations the validation state
     * @return error or null if no errors detected
     */
    private boolean finishValidate(DQRuleDefImpl rule, DataQualityContext dqc, CleanseFunctionContext cfc, Boolean[] validations) {

        MeasurementPoint.start();
        try {

            boolean isValid = BooleanUtils.and(validations);
            if (!isValid) {

                DQRRaiseDef raise = rule.getRaise();

                // 1. Message
                String message;
                if (StringUtils.isEmpty(raise.getMessageText())) {
                    CleanseFunctionOutputParam param = cfc.getOutputParamByPortName(raise.getMessagePort());
                    SimpleAttribute<?> attribute = param != null && param.isSingleton() ? param.getSingleton() : null;
                    message = attribute != null && !attribute.isEmpty() ? attribute.castValue() : "No value defined for DQ error message!";
                } else {
                    message = rule.getRClass() == DQRuleClass.SYSTEM
                            ? DQUtils.extractSystemDQRaiseMessageText(raise.getMessageText())
                            : raise.getMessageText();
                }

                // 2. Severity
                String severity;
                if (StringUtils.isNotEmpty(raise.getSeverityPort())) {
                    CleanseFunctionOutputParam param = cfc.getOutputParamByPortName(raise.getSeverityPort());
                    SimpleAttribute<?> attribute = param != null && param.isSingleton() ? param.getSingleton() : null;
                    severity = attribute != null && !attribute.isEmpty() ? attribute.castValue() : "No value defined for DQ error severity!";
                } else {
                    severity = raise.getSeverityValue().name();
                }

                // 3. Category
                String category;
                if (StringUtils.isNotEmpty(raise.getCategoryPort())) {
                    CleanseFunctionOutputParam param = cfc.getOutputParamByPortName(raise.getCategoryPort());
                    SimpleAttribute<?> attribute = param != null && param.isSingleton() ? param.getSingleton() : null;
                    category = attribute != null && !attribute.isEmpty() ? attribute.castValue() : "No value defined for DQ error category!";
                } else {
                    category = raise.getCategoryText();
                }
                List<DataQualityCallState> dataQualityCallStates = new ArrayList<>();

                rule.getInput().forEach((k, v) -> {
                    dataQualityCallStates.add(new DataQualityCallState(v.getUpath() != null ? v.getUpath().toUPath() : null, v.getInputPort(), cfc.getInputParamByPortName(v.getInputPort()).getAttributes()));
                });

                dqc.getErrors().add(DataQualityError.builder()
                        .category(category)
                        .status(DataQualityStatus.NEW)
                        .message(message)
                        .severity(severity)
                        .ruleName(rule.getName())
                        .callStates(dataQualityCallStates)
                        .values(cfc.failedValidations())
                        .executionMode(dqc.getExecutionMode())
                        .build());

                return isValid;
            }

            return isValid;

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Check enrichment output.
     * @param rule the rule currently being executed
     * @param dqc global data quality context
     * @param cfc cleanse function execution context to validate
     * @param enrichments the enrichments state
     * @return error or null if no errors detected
     */
    private void finishEnrich(DQRuleDefImpl rule, DataQualityContext dqc, CleanseFunctionContext cfc, Boolean[] enrichments) {

        MeasurementPoint.start();
        try {

            // 1. Check general input quality
            if (rule.getEnrich() == null || rule.getEnrich().getSourceSystem() == null) {

                final String message = "Invalid enrichment configuration in rule '{}'.";
                LOGGER.warn(message, rule.getName());
                dqc.getErrors().add(DataQualityError.builder()
                        .category(DQUtils.CATEGORY_SYSTEM)
                        .status(DataQualityStatus.NEW)
                        .message("Invalid enrichment configuration in rule '" + rule.getName() + "'")
                        .severity(SeverityType.CRITICAL.name())
                        .ruleName(rule.getName())
                        .executionMode(dqc.getExecutionMode())
                        .build());

                return;
            }

            switch (dqc.getExecutionMode()) {
            case MODE_ORIGIN:
            case MODE_ONLINE:
            case MODE_PREVIEW:
                // 2. Apply to origin source
                finishEnrichOrigin(rule, dqc, cfc, enrichments);
                break;
            case MODE_ETALON:
                // 3. Apply to master data or origin source
                finishEnrichEtalon(rule, dqc, cfc, enrichments);
                break;
            default:
                break;
            }
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Collects additional DQ errors.
     * @param dqc the {@link DataQualityContext}
     * @param cfc the {@link CleanseFunctionContext}
     */
    private void collect(DataQualityContext dqc, CleanseFunctionContext cfc) {
        MeasurementPoint.start();
        try {
            dqc.getErrors().addAll(cfc.errors());
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Applies params to origin record (source system).
     * @param rule current rule
     * @param dqc the global DQ context
     * @param cfc this function context
     * @param enrichments the enrichments state
     */
    private void finishEnrichOrigin(DQRuleDefImpl rule, DataQualityContext dqc, CleanseFunctionContext cfc, Boolean[] enrichments) {

        // Origin rules always modify the same record.
        DataRecord record = dqc.getModificationBox().originState();
        boolean hadHits = BooleanUtils.or(enrichments);
        if (hadHits) {

            Date point = new Date(System.currentTimeMillis());
            String user = SecurityUtils.getCurrentUserName();
            OriginKey key = dqc.keys() != null
                ? dqc.keys().getOriginKey()
                : OriginKey.builder()
                    .enrichment(true)
                    .entityName(dqc.getEntityName())
                    .externalId(dqc.getExternalId())
                    .sourceSystem(dqc.getSourceSystem())
                    .build();

            OriginRecord result = new OriginRecordImpl()
                .withDataRecord(SerializableDataRecord.of(record))
                .withInfoSection(new OriginRecordInfoSection()
                        .withCreateDate(point)
                        .withUpdateDate(point)
                        .withValidFrom(dqc.getValidFrom())
                        .withValidTo(dqc.getValidTo())
                        .withCreatedBy(user)
                        .withUpdatedBy(user)
                        .withShift(DataShift.REVISED)
                        .withStatus(RecordStatus.ACTIVE)
                        .withOriginKey(key));

            dqc.getModificationBox().push(CalculableHolder.of(result));
        }
    }
    /**
     * Applies params to etalon record (master data).
     * @param rule current rule
     * @param dqc the global DQ context
     * @param cfc this function context
     * @param enrichments the enrichments state
     */
    private void finishEnrichEtalon(DQRuleDefImpl rule, DataQualityContext dqc, CleanseFunctionContext cfc, Boolean[] enrichments) {

        DataRecord prev = dqc.getModificationBox().etalonState();
        DataRecord record = cfc.getFromStorage(StorageId.DATA_DQ_ENRICH_ETALON_COPY);

        boolean hadHits = BooleanUtils.or(enrichments);
        if (hadHits) {

            // Keys may be null for DQ online for instance. Support this.
            String entityName = dqc.keys() != null
                ? dqc.keys().getEntityName()
                : dqc.getEntityName();

            String etalonId = dqc.keys() != null && dqc.keys().getEtalonKey() != null
                ? dqc.keys().getEtalonKey().getId()
                : dqc.getExternalId();

            String user = SecurityUtils.getCurrentUserName();
            Date point = new Date(System.currentTimeMillis());
            OriginKey key = OriginKey.builder()
                .enrichment(true)
                .entityName(entityName)
                .id(IdUtils.v1String())
                .externalId(String.join("__", etalonId, rule.getName()))
                .sourceSystem(rule.getEnrich().getSourceSystem())
                .build();

            OriginRecordImpl enrichment = new OriginRecordImpl()
                .withDataRecord(record)
                .withInfoSection(new OriginRecordInfoSection()
                        .withCreateDate(point)
                        .withUpdateDate(point)
                        .withValidFrom(dqc.getValidFrom())
                        .withValidTo(dqc.getValidTo())
                        .withCreatedBy(user)
                        .withUpdatedBy(user)
                        .withShift(DataShift.REVISED)
                        .withStatus(RecordStatus.ACTIVE)
                        .withOriginKey(key));

            List<CalculableHolder<DataRecord>> base = dqc.getModificationBox().toCalculables();
            base.add(CalculableHolder.of(enrichment));

            DataRecord next = etalonComposer.compose(EtalonCompositionDriverType.BVT, base, true, false);

            // UN-2329
            DataRecord diff = DataUtils.simpleDataDiff(entityName, next, prev, null);

            // Has changes. Add.
            if (diff != null) {

                // Save diff only, save space.
                enrichment.withDataRecord(diff);

                dqc.getModificationBox().push(CalculableHolder.of(enrichment));
                dqc.getModificationBox().etalonState(next);
            }
        }
    }
    /**
     * Applies output params to a record.
     * @param cfw the cleanse function wrapper
     * @param rule current rule
     * @param cfc CF context
     * @param record the record to apply params to
     * @return true, if some modifications were made by the method. False otherwise
     */
    private boolean applyParams(CleanseFunctionWrapper cfw, DQRuleDefImpl rule, CleanseFunctionContext cfc, DataRecord record) {

        boolean hadHits = false;
        Map<String, DQRMappingDefImpl> output = rule.getOutput();
        for (Entry<String, DQRMappingDefImpl> portInfo : output.entrySet()) {

            CleanseFunctionOutputParam param = cfc.getOutputParamByPortName(portInfo.getKey());

            // Simple check against misconfiguration + guard against combo VALIDATE + ENRICH
            boolean skip = Objects.isNull(param)
                || (rule.getRaise() != null && StringUtils.isNotBlank(rule.getRaise().getFunctionRaiseErrorPort())
                 && rule.getRaise().getFunctionRaiseErrorPort().equals(portInfo.getKey()));

            if (skip) {
                continue;
            }

            // Output is always singleton
            DQRMappingDefImpl impl = portInfo.getValue();
            AttributeInfoHolder info = impl.getUpath().getTail().getInfo();
            Attribute attribute = param.getSingleton();

            // Repackage with real name and apply
            boolean wasSet = upathService.upathSet(impl.getUpath(), record, applyParam(attribute, info),
                    StringUtils.isNotBlank(rule.getExecutionContextPath()) ? UPathExecutionContext.SUB_TREE : UPathExecutionContext.FULL_TREE,
                    DQUtils.ofMode(cfw.getOutputPortByName(impl.getOutputPort()).getApplicationMode()));

            // Preserve previous application state
            hadHits = !hadHits ? wasSet : hadHits;
        }

        return hadHits;
    }
    /**
     * Applies (narrow and repackage) a single output param possibly narrowing it to a proper type.
     * @param attribute the output
     * @param info attribute description
     * @return narrowed and repackaged attribute
     */
    private Attribute applyParam(Attribute attribute, AttributeInfoHolder info) {

        if (attribute.getAttributeType() == AttributeType.SIMPLE) {

            SimpleAttribute<?> cast = attribute.narrow();

            // UN-7884, handle measured special case, allowing number values,
            // but let the type check to take place in UPathService
            DataType type = cast.getDataType();
            if (info.isMeasured() && cast.getDataType() == DataType.NUMBER) {
                type = DataType.MEASURED;
            }

            SimpleAttribute<?> target = AbstractSimpleAttribute.of(type, info.getAttribute().getName(), cast.getValue());

            // UN-7884, Same as above
            if (info.isMeasured() && cast.getDataType() == DataType.NUMBER) {

                MeasuredSimpleAttributeImpl msai = target.narrow();
                SimpleAttributeDef sad = info.narrow();

                msai.withValueId(sad.getMeasureSettings().getValueId())
                    .withInitialUnitId(sad.getMeasureSettings().getDefaultUnitId());
            }

            return target;

        } else if (attribute.getAttributeType() == AttributeType.ARRAY) {

            ArrayAttribute<?> cast = attribute.narrow();
            return AbstractArrayAttribute.of(cast.getDataType(), info.getAttribute().getName(), cast.toArray());
        } else if (attribute.getAttributeType() == AttributeType.COMPLEX) {

            ComplexAttribute cast = attribute.narrow();
            return new ComplexAttributeImpl(info.getAttribute().getName(), cast.toCollection());
        }

        return null;
    }
    /**
     * Creates cleanse function param from constant value.
     * @param impl {@link DQRMappingDef} instance
     * @param target data quality context
     * @param isSubTree the context of execution
     * @param mode the application mode
     * @return cleanse function param or null
     */
    private CleanseFunctionInputParam ofUpathValue(DQRMappingDefImpl impl,
            DataRecord target,
            boolean isSubTree,
            DQCleanseFunctionPortApplicationMode mode) {

        if (Objects.nonNull(target)) {
            return CleanseFunctionInputParam.of(impl.getInputPort(),
                    upathService.upathGet(impl.getUpath(), target,
                            isSubTree ? UPathExecutionContext.SUB_TREE : UPathExecutionContext.FULL_TREE,
                                    DQUtils.ofMode(mode)));
        }

        return null;
    }
    /**
     * Creates cleanse function param from constant value.
     * @param impl {@link DQRMappingDef} instance
     * @return cleanse function param or null
     */
    private CleanseFunctionInputParam ofConstantValue(DQRMappingDefImpl impl) {

        ConstantValueDef cvd = impl.getAttributeConstantValue();
        if (cvd.getType() == null) {
            return null;
        }

        SimpleAttribute<?> attribute = null;
        switch (cvd.getType()) {
        case BOOLEAN:
            attribute = new BooleanSimpleAttributeImpl(impl.getAttributeName(), cvd.isBoolValue());
            break;
        case DATE:
            attribute = new DateSimpleAttributeImpl(impl.getAttributeName(), cvd.getDateValue());
            break;
        case INTEGER:
            attribute = new IntegerSimpleAttributeImpl(impl.getAttributeName(), cvd.getIntValue());
            break;
        case NUMBER:
            attribute = new NumberSimpleAttributeImpl(impl.getAttributeName(), cvd.getNumberValue());
            break;
        case STRING:
            attribute = new StringSimpleAttributeImpl(impl.getAttributeName(), cvd.getStringValue());
            break;
        case TIME:
            attribute = new TimeSimpleAttributeImpl(impl.getAttributeName(), cvd.getTimeValue());
            break;
        case TIMESTAMP:
            attribute = new TimestampSimpleAttributeImpl(impl.getAttributeName(), cvd.getTimestampValue());
            break;
        default:
            break;
        }

        if (Objects.isNull(attribute)) {
            return null;
        }

        return CleanseFunctionInputParam.of(impl.getInputPort(), attribute);
    }
    /**
     * Selects top level record for processing.
     * @param rule the rule, currently being executed
     * @param dqc the {@link DataQualityContext}
     * @param cfc this function context
     * @return record or null
     */
    private DataRecord selectTopLevelRecord(DQRuleDefImpl rule, DataQualityContext dqc, CleanseFunctionContext cfc) {

        DataRecord target = null;
        switch (dqc.getExecutionMode()) {
        case MODE_ORIGIN:
        case MODE_ONLINE:
        case MODE_PREVIEW:
            target = dqc.getModificationBox().originState();
            break;
        case MODE_ETALON:
            // Data can be modified potentially. But the etalon state must be preserved.
            if (rule.getType().contains(DQRuleType.ENRICH)) {
                target = SerializableDataRecord.of(dqc.getModificationBox().<DataRecord>etalonState());
                cfc.putToStorage(StorageId.DATA_DQ_ENRICH_ETALON_COPY, target);
            } else {
                target = dqc.getModificationBox().etalonState();
            }
            break;
        default:
            break;
        }

        return target;
    }
    /**
     * Handles cleanse function execution exception.
     * @param ctx the context, being processed
     * @param rule the rule caused the exception
     * @param e the exception to handle
     */
    private void handleCleanseFunctionExecutionException(DataQualityContext ctx, DQRuleDef rule, Exception e) {

        final String message = "DQ error: category [{}],"
                + " status [{}], "
                + "exception while executing cleanse function: '{}', "
                + "exception message: {}], "
                + "severity [{}], "
                + "rule name [{}].";

        LOGGER.warn(message,
                DQUtils.CATEGORY_SYSTEM,
                DataQualityStatusType.NEW,
                rule.getCleanseFunctionName(),
                e.getMessage(),
                SeverityType.CRITICAL,
                rule.getName());

        ctx.getErrors().add(
            DataQualityError.builder()
                .category(DQUtils.CATEGORY_SYSTEM)
                .status(DataQualityStatus.NEW)
                .message(MessageFormatter.arrayFormat("Exception while executing cleanse function '{}'\n Stacktrace: \n",
                        new Object[] { rule.getCleanseFunctionName(), String.join("\n", ExceptionUtils.getRootCauseStackTrace(e)) }
                        ).getMessage())
                .severity(SeverityType.CRITICAL.name())
                .ruleName(rule.getName())
                .executionMode(ctx.getExecutionMode())
                .build());
    }
}
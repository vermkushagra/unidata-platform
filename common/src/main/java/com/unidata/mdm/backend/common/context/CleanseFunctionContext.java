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

package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.dq.DataQualityExecutionMode;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.UpsertAction;

/**
 * Data quality context.
 *
 * @author ilya.bykov
 * @param <T>
 *            the generic type
 */
public class CleanseFunctionContext extends CommonRequestContext implements RecordIdentityContext, ValidityRangeContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -7404007503023231104L;
    /**
     * Version (de)activation attribute validFrom.
     */
    private final Date validFrom;
    /**
     * Version (de)activation attribute validTo.
     */
    private final Date validTo;
    /**
     * Etalon key.
     */
    private final String etalonKey;
    /**
     * Origin key.
     */
    private final String originKey;
    /**
     * Origin foreign id.
     */
    private final String externalId;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Source system name.
     */
    private final String sourceSystem;
    /**
     * Executing CF name.
     */
    private final String cleanseFunctionName;
    /**
     * Executing rule name.
     */
    private final String ruleName;
    /**
     * Global sequence number.
     */
    private final Long gsn;
    /**
     * The execution mode.
     */
    private final DataQualityExecutionMode executionMode;
    /**
     * Action.
     */
    private final UpsertAction action;
    /**
     * Input by port name.
     */
    private final Map<String, CleanseFunctionInputParam> input = new HashMap<>(8);
    /**
     * Output by port name.
     */
    private final Map<String, CleanseFunctionOutputParam> output = new HashMap<>(8);
    /**
     * Failed local validation paths and values. Subject for refactoring.
     */
    private final List<Pair<String, Attribute>> failedValidations = new ArrayList<>(4);
    /**
     * The errors.
     */
    private final List<DataQualityError> errors = new ArrayList<>(2);
    /**
     * Instantiates a new CF context.
     */
    private CleanseFunctionContext(CleanseFunctionContextBuilder b) {
        super();
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.cleanseFunctionName = b.cleanseFunctionName;
        this.ruleName = b.ruleName;
        this.gsn = b.gsn;
        this.action = b.action;
        this.executionMode = b.executionMode;
        if (Objects.nonNull(b.input)) {
            this.input.putAll(b.input);
        }
    }
    /**
     * @return the etalonKey
     */
    @Override
    public String getEtalonKey() {
        return etalonKey;
    }
    /**
     * @return the originKey
     */
    @Override
    public String getOriginKey() {
        return originKey;
    }
    /**
     * @return the externalId
     */
    @Override
    public String getExternalId() {
        return externalId;
    }
    /**
     * @return the entityName
     */
    @Override
    public String getEntityName() {
        return entityName;
    }
    /**
     * @return the sourceSystem
     */
    @Override
    public String getSourceSystem() {
        return sourceSystem;
    }
    /**
     * @return the ruleName
     */
    public String getRuleName() {
        return ruleName;
    }
    /**
     * @return the cleanseFunctionName
     */
    public String getCleanseFunctionName() {
        return cleanseFunctionName;
    }
    /**
     * @return the gsn
     */
    @Override
    public Long getGsn() {
        return gsn;
    }
    /**
     * @return the action
     */
    public UpsertAction getAction() {
        return action;
    }
    /**
     * @return the executionMode
     */
    public DataQualityExecutionMode getExecutionMode() {
        return executionMode;
    }
    /**
     * @return record valid to date
     */
    @Override
    public Date getValidTo() {
        return validTo;
    }
    /**
     * @return record valid from date
     */
    @Override
    public Date getValidFrom() {
        return validFrom;
    }
    /**
     * Gets attributes collection by port name.
     * @param portName the port name
     * @return attributes collection
     */
    public Collection<Attribute> getAttributesByPortName(String portName) {
        CleanseFunctionInputParam param = input.get(portName);
        return Objects.isNull(param) ? Collections.emptyList() : param.getAttributes();
    }

    public CleanseFunctionInputParam getInputParamByPortName(String portName) {
        return input.get(portName);
    }

    public CleanseFunctionOutputParam getOutputParamByPortName(String portName) {
        return output.get(portName);
    }

    public void putOutputParam(CleanseFunctionOutputParam param) {
        output.put(param.getPortName(), param);
    }

    public Collection<CleanseFunctionOutputParam> output() {
        return MapUtils.isNotEmpty(output) ? output.values() : Collections.emptyList();
    }

    public Collection<CleanseFunctionInputParam> input() {
        return MapUtils.isNotEmpty(input) ? input.values() : Collections.emptyList();
    }
    /**
     * @param action the action to set
     * @return self
     */
    public void input(CleanseFunctionInputParam param) {
        input.put(param.getPortName(), param);
    }
    /**
     * @param action the action to set
     * @return self
     */
    public void input(CleanseFunctionInputParam... params) {
        for (int i = 0; params != null && i < params.length; i++) {
            input(params[i]);
        }
    }
    /**
     * @param action the action to set
     * @return self
     */
    public void input(Collection<CleanseFunctionInputParam> params) {
        for (CleanseFunctionInputParam param : params) {
            input(param);
        }
    }
    /**
     * @return the errors
     */
    public List<DataQualityError> errors() {
        return errors;
    }

    /**
     * @return the failedValidationPaths
     */
    public List<Pair<String, Attribute>> failedValidations() {
        return failedValidations;
    }
    /**
     * Input port names.
     * @return names
     */
    public Collection<String> inputPorts() {
        return input.keySet();
    }
    /**
     * Output port names.
     * @return names
     */
    public Collection<String> outputPorts() {
        return output.keySet();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(keysId());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.DATA_UPSERT_KEYS;
    }
    /**
     * The usual builder.
     * @return builder
     */
    public static CleanseFunctionContextBuilder builder(DataQualityContext dqCtx) {
        // Copy just input
        return new CleanseFunctionContextBuilder()
                .action(dqCtx.getAction())
                .executionMode(dqCtx.getExecutionMode())
                .validFrom(dqCtx.getValidFrom())
                .validTo(dqCtx.getValidTo())
                .etalonKey(dqCtx.getEtalonKey())
                .originKey(dqCtx.getOriginKey())
                .externalId(dqCtx.getExternalId())
                .entityName(dqCtx.getEntityName())
                .sourceSystem(dqCtx.getSourceSystem())
                .gsn(dqCtx.getGsn());
    }
    /**
     * Copy builder without params.
     * @param other the context
     * @return builder
     */
    public static CleanseFunctionContextBuilder builder(CleanseFunctionContext other) {
        return new CleanseFunctionContextBuilder()
                .action(other.getAction())
                .executionMode(other.getExecutionMode())
                .validFrom(other.getValidFrom())
                .validTo(other.getValidTo())
                .etalonKey(other.getEtalonKey())
                .originKey(other.getOriginKey())
                .externalId(other.getExternalId())
                .entityName(other.getEntityName())
                .sourceSystem(other.getSourceSystem())
                .cleanseFunctionName(other.getCleanseFunctionName())
                .ruleName(other.getRuleName())
                .gsn(other.getGsn());
    }
    /**
     * The usual builder.
     * @return builder
     */
    public static CleanseFunctionContextBuilder builder() {
        return new CleanseFunctionContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * The ususal builder class.
     */
    public static class CleanseFunctionContextBuilder {
        /**
         * Version (de)activation attribute validFrom.
         */
        private Date validFrom;
        /**
         * Version (de)activation attribute validTo.
         */
        private Date validTo;
        /**
         * Etalon key.
         */
        private String etalonKey;
        /**
         * Origin key.
         */
        private String originKey;
        /**
         * Origin foreign id.
         */
        private String externalId;
        /**
         * Entity name.
         */
        private String entityName;
        /**
         * Source system name.
         */
        private String sourceSystem;
        /**
         * Executing CF name.
         */
        private String cleanseFunctionName;
        /**
         * Executing rule name.
         */
        private String ruleName;
        /**
         * Global sequence number.
         */
        private Long gsn;
        /**
         * Action.
         */
        private UpsertAction action;
        /**
         * Execution mode.
         */
        private DataQualityExecutionMode executionMode;
        /**
         * Cleanse function input.
         */
        private Map<String, CleanseFunctionInputParam> input;
        /**
         * Constructor.
         */
        private CleanseFunctionContextBuilder() {
            super();
        }
        /**
         * @param from the validFrom validTo set
         */
        public CleanseFunctionContextBuilder validFrom(Date from) {
            this.validFrom = from;
            return this;
        }

        /**
         * @param to the validTo validTo set
         */
        public CleanseFunctionContextBuilder validTo(Date to) {
            this.validTo = to;
            return this;
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public CleanseFunctionContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the originKey to set
         */
        public CleanseFunctionContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public CleanseFunctionContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public CleanseFunctionContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public CleanseFunctionContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param gsn the gsn to set
         */
        public CleanseFunctionContextBuilder gsn(Long gsn) {
            this.gsn = gsn;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public CleanseFunctionContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the originKey to set
         */
        public CleanseFunctionContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }
        /**
         * @param action the action to set
         * @return self
         */
        public CleanseFunctionContextBuilder action(UpsertAction action) {
            this.action = action;
            return this;
        }
        /**
         * @param executionMode the {@link DataQualityExecutionMode} to set
         * @return self
         */
        public CleanseFunctionContextBuilder executionMode(DataQualityExecutionMode executionMode) {
            this.executionMode = executionMode;
            return this;
        }
        /**
         * Required.
         * @param ruleName the ruleName to set
         * @return self
         */
        public CleanseFunctionContextBuilder ruleName(String ruleName) {
            this.ruleName = ruleName;
            return this;
        }
        /**
         * Required.
         * @param cleanseFunctionName the cleanseFunctionName to set
         * @return self
         */
        public CleanseFunctionContextBuilder cleanseFunctionName(String cleanseFunctionName) {
            this.cleanseFunctionName = cleanseFunctionName;
            return this;
        }
        /**
         * @param action the action to set
         * @return self
         */
        public CleanseFunctionContextBuilder input(CleanseFunctionInputParam param) {

            if (Objects.isNull(input)) {
                input = new HashMap<>();
            }

            input.put(param.getPortName(), param);
            return this;
        }
        /**
         * @param action the action to set
         * @return self
         */
        public CleanseFunctionContextBuilder input(CleanseFunctionInputParam... params) {

            for (int i = 0; params != null && i < params.length; i++) {
                input(params[i]);
            }

            return this;
        }
        /**
         * @param action the action to set
         * @return self
         */
        public CleanseFunctionContextBuilder input(Collection<CleanseFunctionInputParam> params) {

            for (CleanseFunctionInputParam param : params) {
                input(param);
            }

            return this;
        }
        /**
         * Build.
         * @return ctx
         */
        public CleanseFunctionContext build() {
            return new CleanseFunctionContext(this);
        }
    }
}

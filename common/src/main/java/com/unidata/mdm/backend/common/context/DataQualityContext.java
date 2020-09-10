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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.unidata.mdm.backend.common.data.ModificationBox;
import com.unidata.mdm.backend.common.dq.DataQualityExecutionMode;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.meta.DQRuleDef;

/**
 * Data quality context.
 *
 * @author ilya.bykov
 */
public class DataQualityContext extends CommonRequestContext implements RecordIdentityContext, ValidityRangeContext {
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
     * Global sequence number.
     */
    private final Long gsn;
    /**
     * Action.
     */
    private final UpsertAction action;
    /**
     * Mod box.
     */
    private final transient ModificationBox modificationBox;
    /**
     * Box key of this context.
     */
    private String boxKey;
    /**
     * Rules to execute.
     */
    private final List<DQRuleDef> rules;
    /**
     * The mode to execute.
     */
    private final DataQualityExecutionMode executionMode;
    /**
     * The errors.
     */
    private final List<DataQualityError> errors = new ArrayList<>(8);
    /**
     * Instantiates a new DQ context.
     */
    private DataQualityContext(DataQualityContextBuilder b) {
        super();
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.gsn = b.gsn;
        this.action = b.action;
        this.rules = b.rules;
        this.modificationBox = b.modificationBox;
        this.executionMode = b.executionMode;

        flags.set(ContextUtils.CTX_FLAG_SKIP_CONSISTENCY_CHECKS, b.skipConsistencyChecks);
    }

    /**
     * Gets the rules.
     *
     * @return the rules
     */
    public List<DQRuleDef> getRules() {
        return Objects.isNull(rules) ? Collections.emptyList() : rules;
    }

    /**
     * Gets the errors.
     *
     * @return the errors
     */
    public List<DataQualityError> getErrors() {
        return errors;
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

    public ModificationBox getModificationBox() {
        return modificationBox;
    }

    /**
     * @return the executionMode
     */
    public DataQualityExecutionMode getExecutionMode() {
        return executionMode;
    }

    /**
     * @return the skipConsistencyChecks
     */
    public boolean isSkipConsistencyChecks() {
        return flags.get(ContextUtils.CTX_FLAG_SKIP_CONSISTENCY_CHECKS);
    }

    /**
     * Check for modifications.
     * @return true, if modified, false otherwise
     */
    public boolean isModified() {
        return Objects.nonNull(modificationBox) ? modificationBox.isDirty() : false;
    }

    /**
     * Gets box key of this context.
     * @return box key
     */
    public String toBoxKey() {

        if (this.boxKey == null) {
            this.boxKey = ModificationBox.toBoxKey(this);
        }

        return this.boxKey;
    }

    @Override
    public RecordKeys keys() {
        return getFromStorage(keysId());
    }

    @Override
    public StorageId keysId() {
        return StorageId.DATA_UPSERT_KEYS;
    }

    /**
     * The usual builder.
     * @return builder
     */
    public static DataQualityContextBuilder builder(UpsertRequestContext uCtx) {
        // Copy just input
        return new DataQualityContextBuilder()
                .action(uCtx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION))
                .validFrom(uCtx.getValidFrom())
                .validTo(uCtx.getValidTo())
                .etalonKey(uCtx.getEtalonKey())
                .originKey(uCtx.getOriginKey())
                .externalId(uCtx.getExternalId())
                .entityName(uCtx.getEntityName())
                .sourceSystem(uCtx.getSourceSystem())
                .skipConsistencyChecks(uCtx.isSkipConsistencyChecks())
                .gsn(uCtx.getGsn());
    }

    /**
     * The usual builder.
     * @return builder
     */
    public static DataQualityContextBuilder builder() {
        return new DataQualityContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * The ususal builder class.
     */
    public static class DataQualityContextBuilder {
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
         * Global sequence number.
         */
        private Long gsn;
        /**
         * Skip system rules.
         */
        private boolean skipConsistencyChecks;
        /**
         * Action.
         */
        private UpsertAction action;
        /**
         * Mod box.
         */
        private ModificationBox modificationBox;
        /**
         * Rules to execute.
         */
        private List<DQRuleDef> rules;
        /**
         * The execution mode.
         */
        private DataQualityExecutionMode executionMode;
        /**
         * Constructor.
         */
        private DataQualityContextBuilder() {
            super();
        }
        /**
         * @param from the validFrom validTo set
         */
        public DataQualityContextBuilder validFrom(Date from) {
            this.validFrom = from;
            return this;
        }

        /**
         * @param to the validTo validTo set
         */
        public DataQualityContextBuilder validTo(Date to) {
            this.validTo = to;
            return this;
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public DataQualityContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the originKey to set
         */
        public DataQualityContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public DataQualityContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public DataQualityContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public DataQualityContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param gsn the gsn to set
         */
        public DataQualityContextBuilder gsn(Long gsn) {
            this.gsn = gsn;
            return this;
        }

        /**
         * @param skipConsistencyChecks the skipConsistencyChecks to set
         */
        public DataQualityContextBuilder skipConsistencyChecks(boolean skipConsistencyChecks) {
            this.skipConsistencyChecks = skipConsistencyChecks;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public DataQualityContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the originKey to set
         */
        public DataQualityContextBuilder originKey(OriginKey originKey) {
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
        public DataQualityContextBuilder action(UpsertAction action) {
            this.action = action;
            return this;
        }
        /**
         * @param modificationBox the ModificationBox to set
         * @return self
         */
        public DataQualityContextBuilder modificationBox(ModificationBox modificationBox) {
            this.modificationBox = modificationBox;
            return this;
        }
        /**
         * @param rules the rules
         * @return self
         */
        public DataQualityContextBuilder rules(List<DQRuleDef> rules) {
            this.rules = rules;
            return this;
        }
        /**
         * @param executionMode the {@link DataQualityExecutionMode} to set
         * @return self
         */
        public DataQualityContextBuilder executionMode(DataQualityExecutionMode executionMode) {
            this.executionMode = executionMode;
            return this;
        }
        /**
         * Build.
         * @return ctx
         */
        public DataQualityContext build() {
            return new DataQualityContext(this);
        }
    }
}

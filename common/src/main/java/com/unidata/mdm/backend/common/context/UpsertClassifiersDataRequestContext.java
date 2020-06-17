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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.DataQualityError;

/**
 * @author Mikhail Mikhailov
 * Set of classifier data records to up-sert.
 */
public class UpsertClassifiersDataRequestContext extends CommonRequestContext implements RecordIdentityContext, ValidityRangeContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -1974029977531373746L;
    /**
     * Etalon from key.
     */
    private final String etalonKey;
    /**
     * Origin from key.
     */
    private final String originKey;
    /**
     * Origin from external id.
     */
    private final String externalId;
    /**
     * Entity from name.
     */
    private final String entityName;
    /**
     * Origin from name.
     */
    private final String sourceSystem;
    /**
     * The relations to upsert.
     */
    private final Map<String, List<UpsertClassifierDataRequestContext>> classifiers;
     /**
     * Last update date to use (optional).
     */
    private final Date lastUpdate;
    /**
     * Skip cleanse functions.
     */
    private final boolean skipCleanse;
    /**
     * Bypass extension points during upsert.
     */
    private final boolean bypassExtensionPoints;
    /**
     * Set range from.
     */
    private final Date validFrom;
    /**
     * Set range to.
     */
    private final Date validTo;
    /**
     * Override on the supplied validFrom and validTo.
     */
    private final boolean override;
    /**
     * List with data quality errors.
     */
    private List<DataQualityError> dqErrors;
    /**
     * Constructor.
     */
    private UpsertClassifiersDataRequestContext(UpsertClassifiersDataRequestContextBuilder b) {
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.classifiers = b.classifiers;
        this.lastUpdate = b.lastUpdate;
        this.skipCleanse = b.skipCleanse;
        this.bypassExtensionPoints = b.bypassExtensionPoints;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.override = b.override;
    }

    @Override
    public void setOperationId(String operationId) {
        super.setOperationId(operationId);
        if (Objects.nonNull(classifiers)) {
            classifiers.values().stream()
                    .flatMap(Collection::stream)
                    .forEach(ctx -> ctx.setOperationId(operationId));
        }
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
        return StorageId.RECORDS_RECORD_KEYS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEtalonKey() {
        return etalonKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOriginKey() {
        return originKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return externalId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityName() {
        return entityName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceSystem() {
        return sourceSystem;
    }
    /**
     * @return the relations
     */
    public Map<String, List<UpsertClassifierDataRequestContext>> getClassifiers() {
        return classifiers;
    }
    /**
     * @return the lastUpdate
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @return the skipCleanse
     */
    public boolean isSkipCleanse() {
        return skipCleanse;
    }

    /**
     * @return the bypassExtensionPoints
     */
    public boolean isBypassExtensionPoints() {
        return bypassExtensionPoints;
    }

    /**
     * @return the validFrom
     */
    @Override
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @return the validTo
     */
    @Override
    public Date getValidTo() {
        return validTo;
    }

    /**
     * @return the override
     */
    public boolean isOverride() {
        return override;
    }

    /**
     * @return the dqErrors
     */
    public List<DataQualityError> getDqErrors() {
        return dqErrors;
    }
    /**
     * @param dqErrors the dqErrors to set
     */
    public void setDqErrors(List<DataQualityError> dqErrors) {
        this.dqErrors = dqErrors;
    }
    /**
     * Gets new builder.
     * @return builder
     */
    public static UpsertClassifiersDataRequestContextBuilder builder() {
        return new UpsertClassifiersDataRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder class.
     */
    public static class UpsertClassifiersDataRequestContextBuilder {
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
         * The classifier data records to upsert.
         */
        private Map<String, List<UpsertClassifierDataRequestContext>> classifiers;
        /**
         * Last update date to use (optional).
         */
        private Date lastUpdate;
        /**
         * Skip cleanse functions.
         */
        private boolean skipCleanse;
        /**
         * Bypass extension points during upsert.
         */
        private boolean bypassExtensionPoints;
        /**
         * Set range from.
         */
        private Date validFrom;
        /**
         * Set range to.
         */
        private Date validTo;
        /**
         * Override on the supplied validFrom and validTo.
         */
        private boolean override;
        /**
         * Constructor.
         */
        private UpsertClassifiersDataRequestContextBuilder() {
            super();
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public UpsertClassifiersDataRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the originKey to set
         */
        public UpsertClassifiersDataRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public UpsertClassifiersDataRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public UpsertClassifiersDataRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public UpsertClassifiersDataRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param classifiers the classifiers to set
         * @return self
         */
        public UpsertClassifiersDataRequestContextBuilder classifiers(Map<String, List<UpsertClassifierDataRequestContext>> classifiers) {
            this.classifiers = classifiers;
            return this;
        }
        /**
         * @param lastUpdate the last update to set
         */
        public UpsertClassifiersDataRequestContextBuilder lastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        /**
         * @param skipCleanse skip cleanse or not
         */
        public UpsertClassifiersDataRequestContextBuilder skipCleanse(boolean skipCleanse) {
            this.skipCleanse = skipCleanse;
            return this;
        }

        /**
         * @param bypassExtensionPoints bypass extension points or not
         */
        public UpsertClassifiersDataRequestContextBuilder bypassExtensionPoints(boolean bypassExtensionPoints) {
            this.bypassExtensionPoints = bypassExtensionPoints;
            return this;
        }

        /**
         * @param validFrom the range from to set
         */
        public UpsertClassifiersDataRequestContextBuilder validFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        /**
         * @param validTo the range to to set
         */
        public UpsertClassifiersDataRequestContextBuilder validTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }

        /**
         * Override all missing relations on the supplied period.
         * @param override the override to set
         * @return self
         */
        public UpsertClassifiersDataRequestContextBuilder override(boolean override) {
            this.override = override;
            return this;
        }
        /**
         * @param etalonKey the goldenKey to set
         */
        public UpsertClassifiersDataRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }
        /**
         * @param etalonKey the goldenKey to set
         */
        public UpsertClassifiersDataRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }
        /**
         * Builds a context.
         * @return a new context
         */
        public UpsertClassifiersDataRequestContext build() {
            return new UpsertClassifiersDataRequestContext(this);
        }
    }
}

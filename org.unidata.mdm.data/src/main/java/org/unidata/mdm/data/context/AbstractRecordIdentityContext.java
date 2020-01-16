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

package org.unidata.mdm.data.context;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.core.type.calculables.ModificationBoxKey;
import org.unidata.mdm.core.type.keys.ExternalId;
import org.unidata.mdm.core.type.keys.LSN;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.system.context.AbstractCompositeRequestContext;

/**
 * @author Mikhail Mikhailov
 * Base class, implementing key resolution interface.
 */
public abstract class AbstractRecordIdentityContext extends AbstractCompositeRequestContext
    implements ModificationBoxKey, RecordIdentityContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5651795326189175383L;
    /**
     * Etalon key.
     */
    protected final String etalonKey;
    /**
     * Origin key.
     */
    protected final String originKey;
    /**
     * Origin foreign id.
     */
    protected ExternalId externalId;
    /**
     * Local (to partition) sequence number.
     */
    protected final LSN lsn;
    /**
     * Constructor.
     */
    protected AbstractRecordIdentityContext(AbstractRecordIdentityContextBuilder<?> b) {
        super(b);
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.lsn = b.lsn != null && b.shard != null
                ? LSN.of(b.shard, b.lsn)
                : null;
        this.externalId = StringUtils.isNotBlank(b.externalId) || StringUtils.isNotBlank(b.sourceSystem) || StringUtils.isNotBlank(b.entityName)
                ? ExternalId.of(StringUtils.trim(b.externalId), StringUtils.trim(b.entityName), StringUtils.trim(b.sourceSystem))
                : null; // Need this to support ext ID (re) setting / id generation.
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
     * @return the sourceSystem
     */
    @Override
    public String getSourceSystem() {
        return externalId != null ? externalId.getSourceSystem() : null;
    }
    /**
     * @return the entityName
     */
    @Override
    public String getEntityName() {
        return externalId != null ? externalId.getEntityName() : null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return externalId != null ? externalId.getId() : null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalId getExternalIdAsObject() {
        return externalId;
    }
    /**
     * @return the gsn
     */
    @Override
    public Long getLsn() {
        return lsn != null ? lsn.getLsn() : null;
    }
    /**
     * @return the shard
     */
    @Override
    public Integer getShard() {
        return lsn != null ? lsn.getShard() : null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public LSN getLsnAsObject() {
        return lsn;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toBoxKey() {
        RecordKeys keys = keys();
        String selectedSourceSystem = keys == null ? getSourceSystem() : keys.getOriginKey().getSourceSystem();
        String selectedExternalId = keys == null ? getExternalId() : keys.getOriginKey().getExternalId();
        return String.join("|", selectedSourceSystem, selectedExternalId);
    }
    /**
     * @author Mikhail Mikhailov
     *
     * @param <X> the concrete builder class
     */
    public abstract static class AbstractRecordIdentityContextBuilder<X extends AbstractRecordIdentityContextBuilder<X>>
        extends AbstractCompositeRequestContextBuilder<X> {
        /**
         * Shard number.
         */
        protected Integer shard;
        /**
         * Global sequence number.
         */
        protected Long lsn;
        /**
         * Etalon key.
         */
        protected String etalonKey;
        /**
         * Origin key.
         */
        protected String originKey;
        /**
         * Origin foreign id.
         */
        protected String externalId;
        /**
         * Entity name.
         */
        protected String entityName;
        /**
         * Source system name.
         */
        protected String sourceSystem;
        /**
         * Constructor.
         */
        protected AbstractRecordIdentityContextBuilder() {
            super();
        }
        /**
         * Constructor.
         * @param other the object to copy
         */
        protected AbstractRecordIdentityContextBuilder(AbstractRecordIdentityContext other) {
            super(other);
            this.etalonKey = other.etalonKey;
            this.originKey = other.originKey;
            this.sourceSystem = other.externalId != null ? other.externalId.getSourceSystem() : null;
            this.entityName = other.externalId != null ? other.externalId.getEntityName() : null;
            this.externalId = other.externalId != null ? other.externalId.getId() : null;
            this.lsn = other.lsn != null ? other.lsn.getLsn() : null;
            this.shard = other.lsn != null ? other.lsn.getShard() : null;
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public X etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return self();
        }
        /**
         * @param originKey the etalonKey to set
         */
        public X originKey(String originKey) {
            this.originKey = originKey;
            return self();
        }
        /**
         * @param externalId the externalId to set
         */
        public X externalId(ExternalId externalId) {
            this.externalId = externalId != null ? externalId.getId() : null;
            this.sourceSystem = externalId != null ? externalId.getSourceSystem() : null;
            this.entityName = externalId != null ? externalId.getEntityName() : null;
            return self();
        }
        /**
         * @param externalId the externalId to set
         */
        public X externalId(String externalId) {
            this.externalId = externalId;
            return self();
        }
        /**
         * @param entityName the entityName to set
         */
        public X entityName(String entityName) {
            this.entityName = entityName;
            return self();
        }
        /**
         * @param sourceSystem the sourceSystem to set
         */
        public X sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return self();
        }
        /**
         * @param gsn the gsn to set
         */
        public X lsn(LSN lsn) {
            this.lsn = lsn != null ? lsn.getLsn() : null;
            this.shard = lsn != null ? lsn.getShard() : null;
            return self();
        }
        /**
         * @param lsn the lsn to set
         */
        public X lsn(Long lsn) {
            this.lsn = lsn;
            return self();
        }
        /**
         * @param shard the shard to set
         */
        public X shard(Integer shard) {
            this.shard = shard;
            return self();
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public X etalonKey(RecordEtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return self();
        }
        /**
         * @param originKey the originKey to set
         */
        public X originKey(RecordOriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return self();
        }
    }
}

package com.unidata.mdm.backend.common.keys;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Immutable record key.
 */
public class RecordKeys implements Keys, Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5712117666167666097L;
    /**
     * Etalon key.
     */
    private final EtalonKey etalonKey;
    /**
     * Origin key.
     */
    private final OriginKey originKey;
    /**
     * List of origin keys included in etalon.
     */
    private final List<OriginKey> supplementaryKeys;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Etalon status.
     */
    private final RecordStatus etalonStatus;
    /**
     * Origin status.
     */
    private final RecordStatus originStatus;
    /**
     * Etalon approval state.
     */
    private final ApprovalState etalonState;
    /**
     * Whether this record was published.
     */
    private final boolean published;
    /**
     * Global sequence number.
     */
    private final long gsn;
    /**
     * Constructor.
     */
    private RecordKeys(RecordKeysBuilder b) {
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.entityName = b.entityName != null
                ? b.entityName
                : this.originKey == null
                    ? null
                    : this.originKey.getEntityName();
        this.etalonStatus = b.etalonStatus;
        this.originStatus = b.originStatus;
        this.etalonState = b.etalonState;
        this.supplementaryKeys = b.supplementaryKeys == null ? Collections.emptyList(): b.supplementaryKeys;
        this.published = b.published;
        this.gsn = b.gsn;
    }
    /**
     * @return the entityName
     */
    public String getEntityName() {
        return this.entityName;
    }
    /**
     * @return the etalonKey
     */
    public EtalonKey getEtalonKey() {
        return etalonKey;
    }
    /**
     * @return the originKey
     */
    public OriginKey getOriginKey() {
        return originKey;
    }
    /**
     * @return the etalonStatus
     */
    public RecordStatus getEtalonStatus() {
        return etalonStatus;
    }
    /**
     * @return the originStatus
     */
    public RecordStatus getOriginStatus() {
        return originStatus;
    }
    /**
     * @return the etalonState
     */
    public ApprovalState getEtalonState() {
        return etalonState;
    }
    /**
     * @return the gsn
     */
    public long getGsn() {
        return gsn;
    }
    /**
     * @return true if record is in pending state.
     */
    public boolean isPending() {
        return etalonState == ApprovalState.PENDING;
    }
    /**
     * @return true if etalon is inactive.
     */
    public boolean isEtalonInactive() {
        return etalonStatus == RecordStatus.INACTIVE;
    }
    /**
     * @return true if etalon is active.
     */
    public boolean isEtalonActive() {
        return etalonStatus == RecordStatus.ACTIVE;
    }
    /**
     * @return true if etalon is inactive.
     */
    public boolean isOriginInactive() {
        return originStatus == RecordStatus.INACTIVE;
    }
    /**
     * @return the published
     */
    public boolean isPublished() {
        return published;
    }
    /**
     * @return supplementary keys
     */
    public List<OriginKey> getSupplementaryKeys() {
        return supplementaryKeys;
    }

    /**
     * @return only not enriched supplementary keys
     */
    public List<OriginKey> getNotEnrichedSupplementaryKeys() {
        return supplementaryKeys.stream().filter(key -> !key.isEnriched()).collect(Collectors.toList());
    }

    /**
     * Finds an origin key by external id.
     * @param externalId the external id to match
     * @param entityName the entity name to match
     * @param sourceSystem the source system to match
     * @return key or null, if not found
     */
    public OriginKey findByExternalId(String externalId, String entityName, String sourceSystem) {

        for (OriginKey ok : getSupplementaryKeys()) {
            if (ok.getEntityName().equals(entityName)
             && ok.getExternalId().equals(externalId)
             && ok.getSourceSystem().equals(sourceSystem)) {
                return ok;
            }
        }

        return null;
    }

    /**
     * Finds an origin key by external id.
     * @param sourceSystem the source system to match
     * @return key or null, if not found
     */
    public OriginKey getKeyBySourceSystem(String sourceSystem){
        for (OriginKey ok : getSupplementaryKeys()) {
            if (ok.getSourceSystem().equals(sourceSystem)) {
                return ok;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KeysType getType() {
        return KeysType.RECORD_KEYS;
    }

    /**
     * Builder getter.
     * @return new builder instance
     */
    public static RecordKeysBuilder builder() {
        return new RecordKeysBuilder();
    }
    /**
     * Builder getter.
     * @param keys the keys to copy initial values from
     * @return new builder instance
     */
    public static RecordKeysBuilder builder(RecordKeys keys) {
        return new RecordKeysBuilder(keys);
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class RecordKeysBuilder {
        /**
         * Etalon key.
         */
        private EtalonKey etalonKey;
        /**
         * Origin key.
         */
        private OriginKey originKey;
        /**
         * Entity name.
         */
        private String entityName;
        /**
         * Etalon status.
         */
        private RecordStatus etalonStatus;
        /**
         * Origin status.
         */
        private RecordStatus originStatus;
        /**
         * Etalon approval state.
         */
        private ApprovalState etalonState;
        /**
         * Whether this record was published.
         */
        private boolean published;
        /**
         * Global sequence number.
         */
        private long gsn;
        /**
         * The supplementary keys.
         */
        private List<OriginKey> supplementaryKeys;
        /**
         * Constructor.
         */
        private RecordKeysBuilder() {
            super();
        }
        /**
         * Copy constructor.
         * @param keys the keys to copy
         */
        private RecordKeysBuilder(RecordKeys keys) {
            this.etalonKey = keys.etalonKey;
            this.originKey = keys.originKey;
            this.entityName = keys.entityName != null
                    ? keys.entityName
                    : this.originKey == null
                        ? null
                        : this.originKey.getEntityName();
            this.etalonStatus = keys.etalonStatus;
            this.originStatus = keys.originStatus;
            this.etalonState = keys.etalonState;
            this.supplementaryKeys = keys.supplementaryKeys;
            this.gsn = keys.gsn;
            this.published = keys.published;
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public RecordKeysBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }
        /**
         * @param originKey the originKey to set
         */
        public RecordKeysBuilder originKey(OriginKey originKey) {
            this.originKey = originKey;
            return this;
        }
        /**
         * @param entityName the entityName to set
         */
        public RecordKeysBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }
        /**
         * @param etalonStatus the etalonStatus to set
         */
        public RecordKeysBuilder etalonStatus(RecordStatus etalonStatus) {
            this.etalonStatus = etalonStatus;
            return this;
        }
        /**
         * @param originStatus the originStatus to set
         */
        public RecordKeysBuilder originStatus(RecordStatus originStatus) {
            this.originStatus = originStatus;
            return this;
        }
        /**
         * @param etalonState the etalonState to set
         */
        public RecordKeysBuilder etalonState(ApprovalState etalonState) {
            this.etalonState = etalonState;
            return this;
        }
        /**
         * @param published the published to set
         */
        public RecordKeysBuilder published(boolean published) {
            this.published = published;
            return this;
        }
        /**
         * @param gsn the gsn to set
         */
        public RecordKeysBuilder gsn(long gsn) {
            this.gsn = gsn;
            return this;
        }
        /**
         * @param supplementaryKeys
         */
        public RecordKeysBuilder supplementaryKeys(List<OriginKey> supplementaryKeys) {
            this.supplementaryKeys = supplementaryKeys;
            return this;
        }
        /**
         * @return new keys object
         */
        public RecordKeys build() {
            return new RecordKeys(this);
        }
    }
}

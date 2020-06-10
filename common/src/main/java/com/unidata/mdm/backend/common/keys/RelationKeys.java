/**
 *
 */
package com.unidata.mdm.backend.common.keys;

import java.io.Serializable;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Relation keys.
 */
public class RelationKeys implements Keys, Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -2872129636945155801L;
    /**
     * From (left) side.
     */
    private final RecordKeys from;
    /**
     * To (right) side.
     */
    private final RecordKeys to;
    /**
     * Relation name.
     */
    private final String relationName;
    /**
     * Record etalon id.
     */
    private final String etalonId;
    /**
     * Etalon status of the record.
     */
    private final RecordStatus etalonStatus;
    /**
     * Etalon approval state of the record.
     */
    private final ApprovalState etalonState;
    /**
     * Record origin id.
     */
    private final String originId;
    /**
     * This origin's current revision.
     */
    private final int originRevision;
    /**
     * Origin status of the record.
     */
    private final RecordStatus originStatus;
    /**
     * Origin source system.
     */
    private final String originSourceSystem;
    /**
     * Global sequence number.
     */
    private final long gsn;
    /**
     * Constructor.
     * @param b the builder
     */
    private RelationKeys(RelationKeysBuilder b) {
        super();
        this.from = b.from;
        this.to = b.to;
        this.relationName = b.relationName;
        this.etalonId = b.etalonId;
        this.etalonStatus = b.etalonStatus;
        this.etalonState = b.etalonState;
        this.originId = b.originId;
        this.originRevision = b.originRevision;
        this.originStatus = b.originStatus;
        this.originSourceSystem = b.originSourceSystem;
        this.gsn = b.gsn;
    }
    /**
     * {@inheritDoc}
     */
    public RecordKeys getFrom() {
        return from;
    }

    /**
     * {@inheritDoc}
     */
    public RecordKeys getTo() {
        return to;
    }

    /**
     * @return the realtionName
     */
    public String getRelationName() {
        return relationName;
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @return the etalonStatus
     */
    public RecordStatus getEtalonStatus() {
        return etalonStatus;
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
     * Is in pending state.
     * @return true if so, false otherwise
     */
    public boolean isPending() {
        return etalonState == ApprovalState.PENDING;
    }

    /**
     * Is in pending state.
     * @return true if so, false otherwise
     */
    public boolean isEtalonInactive() {
        return etalonStatus == RecordStatus.INACTIVE;
    }

    /**
     * Is in pending state.
     * @return true if so, false otherwise
     */
    public boolean isOriginInactive() {
        return originStatus == RecordStatus.INACTIVE;
    }

    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * @return the originRevision
     */
    public int getOriginRevision() {
        return originRevision;
    }

    /**
     * @return the originStatus
     */
    public RecordStatus getOriginStatus() {
        return originStatus;
    }

    /**
     * @return the originSourceSystem
     */
    public String getOriginSourceSystem() {
        return originSourceSystem;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public KeysType getType() {
        return KeysType.RELATION_KEYS;
    }
    /**
     * Creates a new builder instance.
     * @return new builder instance
     */
    public static RelationKeysBuilder builder() {
        return new RelationKeysBuilder();
    }
    /**
     * Creates a new builder instance.
     * @return new builder instance
     */
    public static RelationKeysBuilder builder(RelationKeys keys) {
        return new RelationKeysBuilder(keys);
    }
    /**
     * @author Mikhail Mikhailov
     * Relations keys builder class.
     */
    public static class RelationKeysBuilder {
        /**
         * From (left) side.
         */
        private RecordKeys from;
        /**
         * To (right) side.
         */
        private RecordKeys to;
        /**
         * Relation name.
         */
        private String relationName;
        /**
         * Record etalon id.
         */
        private String etalonId;
        /**
         * Etalon status of the record.
         */
        private RecordStatus etalonStatus;
        /**
         * Etalon approval state of the record.
         */
        private ApprovalState etalonState;
        /**
         * Record origin id.
         */
        private String originId;
        /**
         * This origin's current revision.
         */
        private int originRevision;
        /**
         * Origin status of the record.
         */
        private RecordStatus originStatus;
        /**
         * Origin source system.
         */
        private String originSourceSystem;
        /**
         * Global sequence number.
         */
        private long gsn;
        /**
         * Constructor.
         */
        private RelationKeysBuilder() {
            super();
        }
        /**
         * Constructor.
         * @param keys the keys to copy
         */
        private RelationKeysBuilder(RelationKeys keys) {
            super();
            this.from = keys.from;
            this.to = keys.to;
            this.relationName = keys.relationName;
            this.etalonId = keys.etalonId;
            this.etalonStatus = keys.etalonStatus;
            this.etalonState = keys.etalonState;
            this.originId = keys.originId;
            this.originRevision = keys.originRevision;
            this.originStatus = keys.originStatus;
            this.originSourceSystem = keys.originSourceSystem;
            this.gsn = keys.gsn;
        }
        /**
         * @param from the from to set
         */
        public RelationKeysBuilder from(RecordKeys from) {
            this.from = from;
            return this;
        }
        /**
         * @param to the to to set
         */
        public RelationKeysBuilder to(RecordKeys to) {
            this.to = to;
            return this;
        }
        /**
         * @param relationName the relationName to set
         */
        public RelationKeysBuilder relationName(String relationName) {
            this.relationName = relationName;
            return this;
        }
        /**
         * @param etalonId the etalonId to set
         */
        public RelationKeysBuilder etalonId(String etalonId) {
            this.etalonId = etalonId;
            return this;
        }
        /**
         * @param etalonStatus the etalonStatus to set
         */
        public RelationKeysBuilder etalonStatus(RecordStatus etalonStatus) {
            this.etalonStatus = etalonStatus;
            return this;
        }
        /**
         * @param etalonState the etalonState to set
         */
        public RelationKeysBuilder etalonState(ApprovalState etalonState) {
            this.etalonState = etalonState;
            return this;
        }
        /**
         * @param originId the originId to set
         */
        public RelationKeysBuilder originId(String originId) {
            this.originId = originId;
            return this;
        }
        /**
         * @param originRevision the revision to set
         */
        public RelationKeysBuilder originRevision(int originRevision) {
            this.originRevision = originRevision;
            return this;
        }
        /**
         * @param originStatus the originStatus to set
         */
        public RelationKeysBuilder originStatus(RecordStatus originStatus) {
            this.originStatus = originStatus;
            return this;
        }
        /**
         * @param originSourceSystem the originSourceSystem to set
         */
        public RelationKeysBuilder originSourceSystem(String originSourceSystem) {
            this.originSourceSystem = originSourceSystem;
            return this;
        }
        /**
         * @param gsn the gsn to set
         */
        public RelationKeysBuilder gsn(long gsn) {
            this.gsn = gsn;
            return this;
        }
        /**
         * New relation keys instance.
         * @return keys
         */
        public RelationKeys build() {
            return new RelationKeys(this);
        }
    }
}

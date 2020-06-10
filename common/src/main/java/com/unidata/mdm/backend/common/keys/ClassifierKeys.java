package com.unidata.mdm.backend.common.keys;

import java.io.Serializable;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Classifier keys.
 */
public class ClassifierKeys implements Keys, Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -806895260134229462L;
    /**
     * Record etalon id.
     */
    private final String etalonId;
    /**
     * Etalon approval state of the record.
     */
    private final ApprovalState etalonState;
    /**
     * Etalon status of the classifier record.
     */
    private final RecordStatus etalonStatus;
    /**
     * Record origin id.
     */
    private final String originId;
    /**
     * Origin status of the classifier record.
     */
    private final RecordStatus originStatus;
    /**
     * Origin source system.
     */
    private final String originSourceSystem;
    /**
     * Referenced record keys.
     */
    private final RecordKeys record;
    /**
     * Classifier name.
     */
    private final String name;
    /**
     * Node id.
     */
    private final String nodeId;
    /**
     * Node name.
     */
    private String nodeName;

    /**
     * Global sequence number.
     */
    private final long gsn;
    /**
     * This origin revision.
     */
    private final int originRevision;

    /**
     * Constructor.
     */
    private ClassifierKeys(ClassifierKeysBuilder b) {
        super();
        this.record = b.record;
        this.name = b.name;
        this.nodeId = b.nodeId;
        this.nodeName = b.nodeName;
        this.etalonId = b.etalonId;
        this.etalonStatus = b.etalonStatus;
        this.etalonState = b.etalonState;
        this.originId = b.originId;
        this.originStatus = b.originStatus;
        this.originSourceSystem = b.originSourceSystem;
        this.gsn = b.gsn;
        this.originRevision = b.originRevision;
    }
    /**
     * @return the record
     */
    public RecordKeys getRecord() {
        return record;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the nodeId
     */
    public String getNodeId() {
        return nodeId;
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
     * @return the originRevision
     */
    public int getOriginRevision() {
        return originRevision;
    }

    /**
     * @return the gsn
     */
    public long getGsn() {
        return gsn;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public KeysType getType() {
        return KeysType.CLASSIFIER_KEYS;
    }
    /**
     * Creates new builder instance.
     * @return new builder
     */
    public static ClassifierKeysBuilder builder(ClassifierKeys keys) {
        return new ClassifierKeysBuilder(keys);
    }
    /**
     * Creates new builder instance.
     * @return new builder
     */
    public static ClassifierKeysBuilder builder() {
        return new ClassifierKeysBuilder();
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class ClassifierKeysBuilder {

        /**
         * Referenced record keys.
         */
        private RecordKeys record;
        /**
         * Classifier name.
         */
        private String name;
        /**
         * Node id.
         */
        private String nodeId;
        /**
         * Node name.
         */
        private String nodeName;

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
         * This origin's current revision.
         */
        private int originRevision;
        /**
         * Constructor.
         */
        private ClassifierKeysBuilder() {
            super();
        }
        /**
         * Constructor.
         * @param keys other keys
         */
        private ClassifierKeysBuilder(ClassifierKeys keys) {
            this();
            this.record = keys.record;
            this.name = keys.name;
            this.nodeId = keys.nodeId;
            this.nodeName = keys.nodeName;
            this.etalonId = keys.etalonId;
            this.etalonStatus = keys.etalonStatus;
            this.etalonState = keys.etalonState;
            this.originId = keys.originId;
            this.originStatus = keys.originStatus;
            this.originSourceSystem = keys.originSourceSystem;
            this.gsn = keys.gsn;
        }
        /**
         * @param record the record to set
         */
        public ClassifierKeysBuilder record(RecordKeys record) {
            this.record = record;
            return this;
        }
        /**
         * @param name the name to set
         */
        public ClassifierKeysBuilder name(String name) {
            this.name = name;
            return this;
        }
        /**
         * @param nodeId the nodeId to set
         */
        public ClassifierKeysBuilder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }
        /**
         * @param nodeName the node name to set
         */
        public ClassifierKeysBuilder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }
        /**
         * @param etalonId the etalonId to set
         */
        public ClassifierKeysBuilder etalonId(String etalonId) {
            this.etalonId = etalonId;
            return this;
        }
        /**
         * @param etalonStatus the etalonStatus to set
         */
        public ClassifierKeysBuilder etalonStatus(RecordStatus etalonStatus) {
            this.etalonStatus = etalonStatus;
            return this;
        }
        /**
         * @param etalonState the etalonState to set
         */
        public ClassifierKeysBuilder etalonState(ApprovalState etalonState) {
            this.etalonState = etalonState;
            return this;
        }
        /**
         * @param originId the originId to set
         */
        public ClassifierKeysBuilder originId(String originId) {
            this.originId = originId;
            return this;
        }
        /**
         * @param originStatus the originStatus to set
         */
        public ClassifierKeysBuilder originStatus(RecordStatus originStatus) {
            this.originStatus = originStatus;
            return this;
        }
        /**
         * @param originSourceSystem the originSourceSystem to set
         */
        public ClassifierKeysBuilder originSourceSystem(String originSourceSystem) {
            this.originSourceSystem = originSourceSystem;
            return this;
        }
        /**
         * @param originRevision the revision to set
         */
        public ClassifierKeysBuilder originRevision(int originRevision) {
            this.originRevision = originRevision;
            return this;
        }
        /**
         * @param gsn the gsn to set
         */
        public ClassifierKeysBuilder gsn(long gsn) {
            this.gsn = gsn;
            return this;
        }
        /**
         * Builds object.
         * @return new object
         */
        public ClassifierKeys build() {
            return new ClassifierKeys(this);
        }
    }
}

package org.unidata.mdm.data.type.rendering;

import org.unidata.mdm.system.type.rendering.AbstractMapInputSource;

/**
 * @author Mikhail Mikhailov on Jan 16, 2020
 */
public class BulkDataRecalculationInputSource extends AbstractMapInputSource {

    private boolean skipConsistencyChecks;

    private boolean skipCleanse;

    private boolean skipExtensionPoints;

    private boolean skipIndexDrop;

    private String operationId;

    public BulkDataRecalculationInputSource withSkipConsistencyChecks(boolean skipConsistencyChecks) {
        this.skipConsistencyChecks = skipConsistencyChecks;
        return this;
    }

    public BulkDataRecalculationInputSource withSkipCleanse(boolean skipCleanse) {
        this.skipCleanse = skipCleanse;
        return this;
    }

    public BulkDataRecalculationInputSource withSkipExtensionPoints(boolean skipExtensionPoints) {
        this.skipExtensionPoints = skipExtensionPoints;
        return this;
    }

    public BulkDataRecalculationInputSource withSkipIndexDrop(boolean skipIndexDrop) {
        this.skipIndexDrop = skipIndexDrop;
        return this;
    }

    public BulkDataRecalculationInputSource withOperationId(String operationId) {
        this.operationId = operationId;
        return this;
    }

    /**
     * @return the skipConsistencyChecks
     */
    public boolean skipConsistencyChecks() {
        return skipConsistencyChecks;
    }

    /**
     * @return the skipCleanse
     */
    public boolean skipCleanse() {
        return skipCleanse;
    }

    /**
     * @return the skipExtensionPoints
     */
    public boolean skipExtensionPoints() {
        return skipExtensionPoints;
    }

    /**
     * @return the skipIndexDrop
     */
    public boolean skipIndexDrop() {
        return skipIndexDrop;
    }

    /**
     * @return the operationId
     */
    public String getOperationId() {
        return operationId;
    }
}

package org.unidata.mdm.data.dto;

import org.unidata.mdm.system.dto.AbstractCompositeResult;

/**
 * @author Mikhail Mikhailov on Dec 16, 2019
 */
public class AbstractBulkResultDTO extends AbstractCompositeResult {
    /**
     * Updated count. Delete period writes to this variable.
     */
    protected long updated = 0L;
    /**
     * General operation failure count.
     */
    protected long failed = 0L;
    /**
     * Skipped due to NO_ACTION or the like.
     */
    protected long skipped = 0L;
    /**
     * Number of inserted
     */
    protected long inserted = 0L;
    /**
     * Number of deleted.
     */
    protected long deleted = 0L;
    /**
     * @return the updated
     */
    public long getUpdated() {
        return updated;
    }
    /**
     * @param updated the updated to set
     */
    public void setUpdated(long updated) {
        this.updated = updated;
    }
    /**
     * @return the failed
     */
    public long getFailed() {
        return failed;
    }
    /**
     * @param failed the failed to set
     */
    public void setFailed(long failed) {
        this.failed = failed;
    }
    /**
     * @return the skipped
     */
    public long getSkipped() {
        return skipped;
    }
    /**
     * @param skipped the skipped to set
     */
    public void setSkipped(long skipped) {
        this.skipped = skipped;
    }
    /**
     * @return the inserted
     */
    public long getInserted() {
        return inserted;
    }
    /**
     * @param inserted the inserted to set
     */
    public void setInserted(long inserted) {
        this.inserted = inserted;
    }
    /**
     * @return the deleted
     */
    public long getDeleted() {
        return deleted;
    }
    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(long deleted) {
        this.deleted = deleted;
    }
}

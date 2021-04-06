package com.unidata.mdm.backend.service.job.exchange.out;

/**
 * @author Mikhail Mikhailov
 * Statistic page.
 */
public class ExportDataStatisticPage {
    /**
     * Failed.
     */
    private long failed = 0L;
    /**
     * Skept.
     */
    private long skept = 0L;
    /**
     * Updated.
     */
    private long updated = 0L;
    /**
     * Inserted.
     */
    private long inserted = 0L;
    /**
     * @param failed the failed to set
     */
    public void incrementFailed(long failed) {
        this.failed += failed;
    }
    /**
     * @param skept the skept to set
     */
    public void incrementSkept(long skept) {
        this.skept += skept;
    }
    /**
     * @param updated the updated to set
     */
    public void incrementUpdated(long updated) {
        this.updated += updated;
    }
    /**
     * @param inserted the inserted to set
     */
    public void incrementInserted(long inserted) {
        this.inserted += inserted;
    }
    /**
     * @return the failed
     */
    public long getFailed() {
        return failed;
    }
    /**
     * @return the skept
     */
    public long getSkept() {
        return skept;
    }
    /**
     * @return the updated
     */
    public long getUpdated() {
        return updated;
    }
    /**
     * @return the inserted
     */
    public long getInserted() {
        return inserted;
    }
}
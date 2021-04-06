package com.unidata.mdm.backend.service.job.importJob;

import com.unidata.mdm.backend.service.job.AbstractUnidataWriter;

/**
 * Class responsible for counting items.
 *
 * @param <T>
 */
public abstract class AbstractCountingWriter<T> extends AbstractUnidataWriter<T> {

    /**
     * Total input items quantity
     */
    private int total = 0;
    /**
     * Total failed items quantity
     */
    private int failed = 0;

    protected void incrementFailed() {
        failed++;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFailed() {
        return failed;
    }
}

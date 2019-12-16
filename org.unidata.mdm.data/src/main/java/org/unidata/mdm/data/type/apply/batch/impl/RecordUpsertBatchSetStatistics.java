package org.unidata.mdm.data.type.apply.batch.impl;

import org.unidata.mdm.data.dto.UpsertRecordDTO;

/**
 * @author Mikhail Mikhailov on Dec 13, 2019
 */
public class RecordUpsertBatchSetStatistics extends AbstractBatchSetStatistics<UpsertRecordDTO> {
    /**
     * Number of inserted
     */
    private long inserted = 0L;
    /**
     * Constructor.
     */
    public RecordUpsertBatchSetStatistics() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        super.reset();
        this.inserted = 0L;
    }
    /**
     * @param inserted the inserted to add
     */
    public void incrementInserted() {
        incrementFailed(1L);
    }
    /**
     * @param inserted the inserted to add
     */
    public void incrementInserted(long inserted) {
        this.inserted += inserted;
    }
    /**
     * @return the inserted
     */
    public long getInserted() {
        return inserted;
    }
}

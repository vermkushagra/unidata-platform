package org.unidata.mdm.data.type.apply.batch.impl;

import org.unidata.mdm.data.dto.DeleteRelationsDTO;

/**
 * @author Mikhail Mikhailov on Dec 16, 2019
 */
public class RelationDeleteBatchSetStatistics extends AbstractBatchSetStatistics<DeleteRelationsDTO> {
    /**
     * Number of deleted.
     */
    private long deleted = 0L;
    /**
     * Constructor.
     */
    public RelationDeleteBatchSetStatistics() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        super.reset();
        this.deleted = 0L;
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
    public void incrementDeleted() {
        incrementDeleted(1L);
    }
    /**
     * @param deleted the deleted to set
     */
    public void incrementDeleted(long deleted) {
        this.deleted += deleted;
    }
}

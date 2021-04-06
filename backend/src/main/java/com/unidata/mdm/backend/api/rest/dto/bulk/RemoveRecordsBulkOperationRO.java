package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

public class RemoveRecordsBulkOperationRO extends BulkOperationBaseRO {

    /**
     * wipe
     */
    private boolean wipe = false;

    /**
     * Constructor.
     */
    public RemoveRecordsBulkOperationRO() {
        super();
    }

    /**
     * Bulk operation type.
     *
     * @return type
     */
    @Override
    public BulkOperationType getType() {
        return BulkOperationType.REMOVE_RECORDS;
    }

    public void setWipe(boolean wipe) {
        this.wipe = wipe;
    }

    public boolean isWipe() {
        return wipe;
    }
}

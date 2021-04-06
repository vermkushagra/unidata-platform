package com.unidata.mdm.backend.service.bulk;

import com.unidata.mdm.backend.common.bulk.BulkOperationConfiguration;
import com.unidata.mdm.backend.common.types.BulkOperationType;

public class RemoveRecordsConfiguration extends BulkOperationConfiguration {

    /**
     * wipe
     */
    private boolean wipeRecords;

    /**
     * Constructor.
     */
    public RemoveRecordsConfiguration() {
        super(BulkOperationType.REMOVE_RECORDS);
    }

    public boolean isWipeRecords() {
        return wipeRecords;
    }

    public void setWipeRecords(boolean wipeRecords) {
        this.wipeRecords = wipeRecords;
    }
}

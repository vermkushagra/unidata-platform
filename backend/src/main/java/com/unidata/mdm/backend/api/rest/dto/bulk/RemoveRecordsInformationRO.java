package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

public class RemoveRecordsInformationRO extends BulkOperationInformationBaseRO {

    /**
     * Constructor.
     */
    public RemoveRecordsInformationRO() {
        super(BulkOperationType.REMOVE_RECORDS.name());
    }
}

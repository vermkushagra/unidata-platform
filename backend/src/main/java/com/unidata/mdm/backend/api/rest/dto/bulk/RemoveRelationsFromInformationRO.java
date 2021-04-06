package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

public class RemoveRelationsFromInformationRO extends BulkOperationInformationBaseRO {

    public RemoveRelationsFromInformationRO() {
        super(BulkOperationType.REMOVE_RELATIONS_FROM.name());
    }
}

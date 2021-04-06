package com.unidata.mdm.backend.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

public class RemoveRelationsFromInformationDTO implements BulkOperationInformationDTO {

    @Override
    public BulkOperationType getType() {
        return BulkOperationType.REMOVE_RELATIONS_FROM;
    }
}

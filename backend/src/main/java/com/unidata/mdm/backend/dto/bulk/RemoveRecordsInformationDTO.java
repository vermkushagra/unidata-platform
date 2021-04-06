package com.unidata.mdm.backend.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Configuration container, specific to remove operation.
 */
public class RemoveRecordsInformationDTO implements BulkOperationInformationDTO {

    /**
     * Constructor.
     */
    public RemoveRecordsInformationDTO() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BulkOperationType getType() {
        return BulkOperationType.REMOVE_RECORDS;
    }
}

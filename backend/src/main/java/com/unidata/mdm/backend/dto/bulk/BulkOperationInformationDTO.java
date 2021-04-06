package com.unidata.mdm.backend.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Should be better named 'configuration', but the name is already used for BO 'run' config.
 */
public interface BulkOperationInformationDTO {

    /**
     * Gets the type.
     * @return type
     */
    BulkOperationType getType();
}

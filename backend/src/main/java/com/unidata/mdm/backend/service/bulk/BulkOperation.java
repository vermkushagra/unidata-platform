package com.unidata.mdm.backend.service.bulk;

import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.dto.bulk.BulkOperationInformationDTO;

/**
 * @author Mikhail Mikhailov
 * Bulk operation interface.
 */
public interface BulkOperation {
    /**
     * Runs operation.
     * @param ctx the context
     * @return true upon success, false otherwise
     */
    boolean run(BulkOperationRequestContext ctx);

    /**
     * Gets configuration information, specific to the bulk operation. Optional.
     * @return information
     */
    BulkOperationInformationDTO configure();
}

/**
 *
 */
package com.unidata.mdm.backend.service.bulk;

import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.dto.bulk.BulkOperationInformationDTO;
import com.unidata.mdm.backend.dto.bulk.RepublishRecordsInformationDTO;

/**
 * @author Mikhail Mikhailov
 * Re-send records to a specified sink bulk.
 */
public class RepublishRecordsBulkOperation implements BulkOperation {

    /**
     * Constructor.
     */
    public RepublishRecordsBulkOperation() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean run(BulkOperationRequestContext ctx) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BulkOperationInformationDTO configure() {
        // Nothing specific so far
        return new RepublishRecordsInformationDTO();
    }
}

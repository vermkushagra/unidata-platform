/**
 *
 */
package com.unidata.mdm.backend.service.bulk;

import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.dto.bulk.BulkOperationInformationDTO;
import com.unidata.mdm.backend.dto.bulk.ImportFromXlsInformationDTO;

/**
 * @author Mikhail Mikhailov
 * Import records from XLS bulk operation.
 */
public class ImportRecordsFromXlsBulkOperation implements BulkOperation {

    /**
     * Constructor.
     */
    public ImportRecordsFromXlsBulkOperation() {
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
        return new ImportFromXlsInformationDTO();
    }
}

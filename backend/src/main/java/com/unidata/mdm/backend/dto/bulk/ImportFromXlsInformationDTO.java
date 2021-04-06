/**
 *
 */
package com.unidata.mdm.backend.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;


/**
 * @author Mikhail Mikhailov
 * Configuration container, specific to import operation.
 */
public class ImportFromXlsInformationDTO implements BulkOperationInformationDTO {

    /**
     * Constructor.
     */
    public ImportFromXlsInformationDTO() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BulkOperationType getType() {
        return BulkOperationType.IMPORT_RECORDS_FROM_XLS;
    }

}

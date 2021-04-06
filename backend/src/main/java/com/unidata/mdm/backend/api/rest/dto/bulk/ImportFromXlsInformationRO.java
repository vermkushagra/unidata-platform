/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Configuration information, specific to 'import' operation.
 */
public class ImportFromXlsInformationRO extends BulkOperationInformationBaseRO {

    /**
     * Constructor.
     */
    public ImportFromXlsInformationRO() {
        super(BulkOperationType.IMPORT_RECORDS_FROM_XLS.name());
    }

}

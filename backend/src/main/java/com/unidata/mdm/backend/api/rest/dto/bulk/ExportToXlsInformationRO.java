/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Configuration information, specific to 'export' operation.
 */
public class ExportToXlsInformationRO extends BulkOperationInformationBaseRO {

    /**
     * Constructor.
     */
    public ExportToXlsInformationRO() {
        super(BulkOperationType.EXPORT_RECORDS_TO_XLS.name());
    }

}

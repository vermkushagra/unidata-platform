/**
 *
 */
package com.unidata.mdm.backend.service.bulk;

import com.unidata.mdm.backend.common.bulk.BulkOperationConfiguration;
import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Export to XLS configuration.
 */
public class ExportToXlsConfiguration extends BulkOperationConfiguration {

    /**
     * Constructor.
     */
    public ExportToXlsConfiguration() {
        super(BulkOperationType.EXPORT_RECORDS_TO_XLS);
    }

}

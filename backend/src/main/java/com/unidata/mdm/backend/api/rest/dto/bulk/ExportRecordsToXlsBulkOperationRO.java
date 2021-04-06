/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * REST parameter class specific to XLS export bulk operation.
 */
public class ExportRecordsToXlsBulkOperationRO extends BulkOperationBaseRO {

    /**
     * Constructor.
     */
    public ExportRecordsToXlsBulkOperationRO() {
        super();
    }

    /**
     * Bulk operation type.
     * @return type
     */
    @JsonIgnore
    public BulkOperationType getType() {
        return BulkOperationType.EXPORT_RECORDS_TO_XLS;
    }
}

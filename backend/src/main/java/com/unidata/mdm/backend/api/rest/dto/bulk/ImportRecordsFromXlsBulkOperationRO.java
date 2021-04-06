/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * REST Parameter class specific to XLS import bulk operation.
 */
public class ImportRecordsFromXlsBulkOperationRO extends BulkOperationBaseRO {

    /**
     * Constructor.
     */
    public ImportRecordsFromXlsBulkOperationRO() {
        super();
    }

    /**
     * Bulk operation type.
     * @return type
     */
    @JsonIgnore
    public BulkOperationType getType() {
        return BulkOperationType.IMPORT_RECORDS_FROM_XLS;
    }
}

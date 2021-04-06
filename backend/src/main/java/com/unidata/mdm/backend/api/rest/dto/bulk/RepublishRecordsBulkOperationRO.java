/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * REST Parameter class specific for republish bulk operation.
 */
public class RepublishRecordsBulkOperationRO extends BulkOperationBaseRO {

    /**
     * Constructor.
     */
    public RepublishRecordsBulkOperationRO() {
        super();
    }

    /**
     * Bulk operation type.
     * @return type
     */
    @JsonIgnore
    public BulkOperationType getType() {
        return BulkOperationType.REPUBLISH_RECORDS;
    }
}

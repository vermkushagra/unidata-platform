/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Configuration information, specific to 'modify' operation.
 */
public class ModifyRecordsInformationRO extends BulkOperationInformationBaseRO {

    /**
     * Constructor.
     */
    public ModifyRecordsInformationRO() {
        super(BulkOperationType.MODIFY_RECORDS.name());
    }
}

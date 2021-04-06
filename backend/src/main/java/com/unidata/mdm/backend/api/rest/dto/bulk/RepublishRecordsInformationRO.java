/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Configuration information, specific to 'republish' operation.
 */
public class RepublishRecordsInformationRO extends BulkOperationInformationBaseRO {

    /**
     * Constructor.
     */
    public RepublishRecordsInformationRO() {
        super(BulkOperationType.REPUBLISH_RECORDS.name());
    }

}

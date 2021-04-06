/**
 *
 */
package com.unidata.mdm.backend.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;


/**
 * @author Mikhail Mikhailov
 * Configuration container, specific to republish operation.
 */
public class RepublishRecordsInformationDTO implements BulkOperationInformationDTO {

    /**
     * Constructor.
     */
    public RepublishRecordsInformationDTO() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BulkOperationType getType() {
        return BulkOperationType.REPUBLISH_RECORDS;
    }

}

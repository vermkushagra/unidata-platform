/**
 *
 */
package com.unidata.mdm.backend.service.bulk;

import com.unidata.mdm.backend.common.bulk.BulkOperationConfiguration;
import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Republish records configuration.
 */
public class RepublishRecordsConfiguration extends BulkOperationConfiguration {

    /**
     * Constructor.
     */
    public RepublishRecordsConfiguration() {
        super(BulkOperationType.REPUBLISH_RECORDS);
    }

}

/**
 *
 */
package com.unidata.mdm.backend.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;


/**
 * @author Mikhail Mikhailov
 * Configuration container, specific to export operation.
 */
public class ExportToXlsInformationDTO implements BulkOperationInformationDTO {

    /**
     * Constructor.
     */
    public ExportToXlsInformationDTO() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BulkOperationType getType() {
        return BulkOperationType.EXPORT_RECORDS_TO_XLS;
    }

}

/**
 *
 */
package com.unidata.mdm.backend.common.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Bulk operation configuration.
 */
public abstract class BulkOperationConfiguration {

    /**
     * Op type.
     */
    private final BulkOperationType type;
    /**
     * Constructor.
     */
    protected BulkOperationConfiguration(BulkOperationType type) {
        super();
        this.type = type;
    }

    /**
     * @return the type
     */
    public BulkOperationType getType() {
        return type;
    }

}

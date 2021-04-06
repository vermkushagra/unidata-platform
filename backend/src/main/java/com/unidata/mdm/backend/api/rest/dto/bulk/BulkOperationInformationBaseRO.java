/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;


/**
 * @author Mikhail Mikhailov
 * Configuration information, specific to an operation.
 */
public abstract class BulkOperationInformationBaseRO {

    /**
     * The type.
     */
    private final String type;
    /**
     * Constructor.
     */
    public BulkOperationInformationBaseRO(String type) {
        super();
        this.type = type;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
}

/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.bulk;


/**
 * @author Mikhail Mikhailov
 * Bulk operation code and description.
 */
public class GetBulkOperationRO {

    /**
     * Description.
     */
    private String description;

    /**
     * Type.
     */
    private String type;

    /**
     * Constructor.
     */
    public GetBulkOperationRO() {
        super();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }


    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return the type
     */
    public String getType() {
        return type;
    }


    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}

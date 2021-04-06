package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * @author Mikhail Mikhailov
 * Bulk operation description.
 */
public class GetBulkOperationDTO {
    /**
     * Description.
     */
    private final String description;
    /**
     * Type.
     */
    private final BulkOperationType type;
    /**
     * Constructor.
     */
    public GetBulkOperationDTO(BulkOperationType type, String description) {
        super();
        this.type = type;
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the type
     */
    public BulkOperationType getType() {
        return type;
    }

}

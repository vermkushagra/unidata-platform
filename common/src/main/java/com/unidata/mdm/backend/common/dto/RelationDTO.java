package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.RelationType;

/**
 * Relation DTO common signatures.
 * @author Mikhail Mikhailov
 */
public interface RelationDTO {
    /**
     * Gets the relation type.
     * @return the relation type
     */
    public RelationType getRelationType();
    /**
     * Gets keys.
     * @return keys
     */
    RelationKeys getRelationKeys();
}

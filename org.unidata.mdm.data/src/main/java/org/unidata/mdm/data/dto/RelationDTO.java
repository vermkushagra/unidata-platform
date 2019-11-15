package org.unidata.mdm.data.dto;

import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;

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

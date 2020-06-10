package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Common part for all relation info sections.
 */
public abstract class AbstractRelationInfoSection extends InfoSection {
    /**
     * The entity name.
     */
    protected String relationName;
    /**
     * Relation type.
     */
    protected RelationType type;
    /**
     * From entity name.
     */
    protected String fromEntityName;
    /**
     * To entity name.
     */
    protected String toEntityName;
    /**
     * Gets the entity name.
     * @return name
     */
    public String getRelationName() {
        return relationName;
    }
    /**
     * Sets entity name field.
     * @param relationName value to set
     */
    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
    /**
     * @return the type
     */
    public RelationType getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(RelationType type) {
        this.type = type;
    }
    /**
     * @return the fromEntityName
     */
    public String getFromEntityName() {
        return fromEntityName;
    }
    /**
     * @param fromEntityName the fromEntityName to set
     */
    public void setFromEntityName(String fromEntityName) {
        this.fromEntityName = fromEntityName;
    }
    /**
     * @return the toEntityName
     */
    public String getToEntityName() {
        return toEntityName;
    }
    /**
     * @param toEntityName the toEntityName to set
     */
    public void setToEntityName(String toEntityName) {
        this.toEntityName = toEntityName;
    }
}

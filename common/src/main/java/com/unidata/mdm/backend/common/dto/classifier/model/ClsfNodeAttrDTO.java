package com.unidata.mdm.backend.common.dto.classifier.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;

/**
 * The Class ClsfNodeSimpleAttrDTO.
 */
public abstract class ClsfNodeAttrDTO {

    /** The attr name. */
    private String name;

    /** The display name. */
    private String displayName;

    /** The description. */
    private String description;

    /** The data type. */
    private String lookupEntityType;
    /**
     * Lookup entity data type
     */
    private CodeAttribute.CodeDataType lookupEntityCodeAttributeType;

	/** The data type. */
    private DataType dataType;

    /** The read only. */
    private boolean readOnly;

    /** The hidden. */
    private boolean hidden;

    /** The nullable. */
    private boolean nullable;

    /** The inherited. */
    private boolean inherited;
    /** The unique. */
    private boolean unique;

    /** The searchable. */
    private boolean searchable;

    /** The created at. */
    private Date createdAt;

    /** The updated at. */
    private Date updatedAt;

    /** The created by. */
    private String createdBy;

    /** The updated by. */
    private String updatedBy;

    private int order;
    /**
     * Custom properties for classifier node attr
     */
    private List<CustomPropertyDefinition> customProperties;

    /**
     * Instantiates a new clsf node attr DTO.
     */
    public ClsfNodeAttrDTO() {
    }

    /**
     * Instantiates a new clsf node attr DTO.
     *
     * @param name the name
     * @param displayName the display name
     * @param description the description
     * @param dataType the data type
     * @param lookupEntityType the lookup data type
     * @param readOnly the read only
     * @param hidden the hidden
     * @param nullable the nullable
     * @param inherited the inherited
     * @param unique the unique
     * @param searchable the searchable
     * @param createdAt the created at
     * @param updatedAt the updated at
     * @param createdBy the created by
     * @param updatedBy the updated by
     */
    public ClsfNodeAttrDTO(
            final String name,
            final String displayName,
            final String description,
            final DataType dataType,
            final String lookupEntityType,
            final CodeAttribute.CodeDataType lookupEntityCodeAttributeType,
            final boolean readOnly,
            final boolean hidden,
            final boolean nullable,
            final boolean inherited,
            final boolean unique,
            final boolean searchable,
            final Date createdAt,
            final Date updatedAt,
            final String createdBy,
            final String updatedBy,
            final int order
    ) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.dataType = dataType;
        this.lookupEntityType = lookupEntityType;
        this.lookupEntityCodeAttributeType = lookupEntityCodeAttributeType;
        this.readOnly = readOnly;
        this.hidden = hidden;
        this.nullable = nullable;
        this.inherited = inherited;
        this.unique = unique;
        this.searchable = searchable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.order = order;
    }

    /**
     * Instantiates a new clsf node attr DTO.
     *
     * @param name the name
     * @param dataType the data type
     * @param displayName the display name
     * @param description the description
     * @param hidden the hidden
     * @param nullable the nullable
     * @param readOnly the read only
     * @param searchable the searchable
     * @param unique the unique
     * @param createdBy the created by
     * @param createdAt the created at
     */
    public ClsfNodeAttrDTO(
            final String name,
            final DataType dataType,
            final String displayName,
            final String description,
            final boolean hidden,
            final boolean nullable,
            final boolean readOnly,
            final boolean searchable,
            final boolean unique,
            final String createdBy,
            final Date createdAt,
            final int order
    ) {
        this.name = name;
        this.dataType = dataType;
        this.displayName = displayName;
        this.description = description;
        this.hidden = hidden;
        this.nullable = nullable;
        this.readOnly = readOnly;
        this.unique = unique;
        this.searchable = searchable;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.order = order;
    }

    /**
     * Gets the attr name.
     *
     * @return the attr name
     */
    public String getAttrName() {
        return name;
    }

    /**
     * Sets the attr name.
     *
     * @param attrName
     *            the new attr name
     */
    public void setAttrName(String attrName) {
        this.name = attrName;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName
     *            the new display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description
     *            the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the data type.
     *
     * @return the data type
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Sets the data type.
     *
     * @param dataType
     *            the new data type
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Get the lookup entity type
     * @return lookup entity type
     */
    public String getLookupEntityType() {
        return lookupEntityType;
    }

    /**
     * Set the lookup data type
     * @param lookupEntityType lookup data type
     */
    public void setLookupEntityType(String lookupEntityType) {
        this.lookupEntityType = lookupEntityType;
    }

    public CodeAttribute.CodeDataType getLookupEntityCodeAttributeType() {
        return lookupEntityCodeAttributeType;
    }

    public void setLookupEntityCodeAttributeType(final CodeAttribute.CodeDataType lookupEntityCodeAttributeType) {
        this.lookupEntityCodeAttributeType = lookupEntityCodeAttributeType;
    }

    /**
     * Checks if is read only.
     *
     * @return true, if is read only
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the read only.
     *
     * @param readOnly
     *            the new read only
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Checks if is hidden.
     *
     * @return true, if is hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the hidden.
     *
     * @param hidden
     *            the new hidden
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Checks if is nullable.
     *
     * @return true, if is nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Sets the nullable.
     *
     * @param nullable
     *            the new nullable
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * Checks if is unique.
     *
     * @return true, if is unique
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * Checks if is inherited.
     *
     * @return true, if is inherited
     */
    public boolean isInherited() {
        return inherited;
    }

    /**
     * Sets the inherited.
     *
     * @param inherited the new inherited
     */
    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    /**
     * Sets the unique.
     *
     * @param unique
     *            the new unique
     */
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    /**
     * Checks if is searchable.
     *
     * @return true, if is searchable
     */
    public boolean isSearchable() {
        return searchable;
    }

    /**
     * Sets the searchable.
     *
     * @param searchable
     *            the new searchable
     */
    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    /**
     * Gets the created at.
     *
     * @return the created at
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the created at.
     *
     * @param createdAt
     *            the new created at
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the updated at.
     *
     * @return the updated at
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the updated at.
     *
     * @param updatedAt
     *            the new updated at
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the created by.
     *
     * @return the created by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the created by.
     *
     * @param createdBy
     *            the new created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the updated by.
     *
     * @return the updated by
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the updated by.
     *
     * @param updatedBy
     *            the new updated by
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClsfNodeAttrDTO other = (ClsfNodeAttrDTO) obj;
        return Objects.equals(name, other.name);
    }

    public List<CustomPropertyDefinition> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(List<CustomPropertyDefinition> customProperties) {
        this.customProperties = customProperties;
    }
    public boolean isSimple() {
        return false;
    }
    public boolean isArray() {
        return false;
    }
}

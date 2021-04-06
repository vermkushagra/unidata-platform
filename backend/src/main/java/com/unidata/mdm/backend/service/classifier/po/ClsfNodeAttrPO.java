package com.unidata.mdm.backend.service.classifier.po;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class ClsfNodeAttrPO.
 */
public class ClsfNodeAttrPO {

	/** The id. */
	private int id;
	/** The id. */
	private int nodeId;

	/** The attr name. */
	private String attrName;

	/** The display name. */
	private String displayName;

	/** The description. */
	private String description;

	/** The data type. */
	private String dataType;

	/** The read only. */
	private boolean readOnly;

	/** The hidden. */
	private boolean hidden;

	/** The nullable. */
	private boolean nullable;

	/** The unique. */
	private boolean unique;

	/** The searchable. */
	private boolean searchable;

	/** The default value. */
	private String defaultValue;

	/** The created at. */
	private Date createdAt;

	/** The updated at. */
	private Date updatedAt;

	/** The created by. */
	private String createdBy;

	/** The updated by. */
	private String updatedBy;

	/**
	 * The Enum FieldColumns.
	 */
	public enum FieldColumns {
		
		/** The id. */
		ID,
		
		/** The attr name. */
		ATTR_NAME,
		
		/** The display name. */
		DISPLAY_NAME,
		
		/** The description. */
		DESCRIPTION,
		
		/** The data type. */
		DATA_TYPE,
		
		/** The is read only. */
		IS_READ_ONLY,
		
		/** The is hidden. */
		IS_HIDDEN,
		
		/** The is nullable. */
		IS_NULLABLE,
		
		/** The is unique. */
		IS_UNIQUE,
		
		/** The is searchable. */
		IS_SEARCHABLE,
		
		/** The default value. */
		DEFAULT_VALUE,
		
		/** The created by. */
		CREATED_BY,
		
		/** The updated by. */
		UPDATED_BY,
		
		/** The created at. */
		CREATED_AT,
		
		/** The updated at. */
		UPDATED_AT,
		
		/** The clsf node id. */
		CLSF_NODE_ID
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the attr name.
	 *
	 * @return the attr name
	 */
	public String getAttrName() {
		return attrName;
	}

	/**
	 * Sets the attr name.
	 *
	 * @param attrName
	 *            the new attr name
	 */
	public void setAttrName(String attrName) {
		this.attrName = attrName;
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
	public String getDataType() {
		return dataType;
	}

	/**
	 * Sets the data type.
	 *
	 * @param dataType
	 *            the new data type
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
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
	 * Gets the default value.
	 *
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value.
	 *
	 * @param defaultValue
	 *            the new default value
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

}

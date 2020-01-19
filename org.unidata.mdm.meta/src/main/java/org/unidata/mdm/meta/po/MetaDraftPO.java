/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.meta.po;

import java.sql.Date;

/**
 * The Class MetaDraftPO.
 */
public class MetaDraftPO {
	
	/** The id. */
	private long id;
	
	/** The type. */
	private Type type;
	
	/** The active. */
	private boolean active;
	
	/** The value. */
	private byte[] value;
	
	/** The version. */
	private long version;
	
	/** The name. */
	private String name;
	
	/** The storage id. */
	private String storageId;
	
	/** The created at. */
	private Date createdAt;
	
	/** The created by. */
	private String createdBy;
	
	/** The updated at. */
	private Date updatedAt;
	
	/** The updated by. */
	private String updatedBy;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public byte[] getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(byte[] value) {
		this.value = value;
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
	 * @param createdAt the new created at
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
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
	 * @param createdBy the new created by
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	 * @param updatedAt the new updated at
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
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
	 * @param updatedBy the new updated by
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Gets the storage id.
	 *
	 * @return the storage id
	 */
	public String getStorageId() {
		return storageId;
	}

	/**
	 * Sets the storage id.
	 *
	 * @param storageId the new storage id
	 */
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active the new active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * The Enum Type.
	 */
	public enum Type {
		
		/** The model. */
		MODEL, 
 /** The measure. */
 MEASURE, 
 /** The custom cf. */
 CUSTOM_CF, 
 /** The classifier. */
 CLASSIFIER, 
 /** The matching. */
 MATCHING

	}

	/**
	 * The Class Field.
	 */
	public static class Field {
		
		/** The Constant ID. */
		public static final String ID = "id";
		
		/** The Constant TYPE. */
		public static final String TYPE = "type";
		
		/** The Constant ACTIVE. */
		public static final String ACTIVE = "active";
		
		/** The Constant VERSION. */
		public static final String VERSION = "version";
		
		/** The Constant NAME. */
		public static final String NAME = "name";
		
		/** The Constant STORAGE_ID. */
		public static final String STORAGE_ID = "storage_id";
		
		/** The Constant VALUE. */
		public static final String VALUE = "value";
		
		/** The Constant CREATED_AT. */
		public static final String CREATED_AT = "created_at";
		
		/** The Constant CREATED_BY. */
		public static final String CREATED_BY = "created_by";
		
		/** The Constant UPDATED_AT. */
		public static final String UPDATED_AT = "updated_at";
		
		/** The Constant UPDATED_BY. */
		public static final String UPDATED_BY = "updated_by";
	}

}

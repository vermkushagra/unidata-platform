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

package com.unidata.mdm.backend.dto.initializer;

import java.io.Serializable;
import java.util.Date;

/**
 * System element data transfer object.
 * 
 * @author ilya.bykov
 *
 */
public class SystemElementDTO implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	private int id;

	/** The type. */
	private ElementTypeDTO type;

	/** The name. */
	private String name;

	/** The description. */
	private String description;

	/** The checksum. */
	private String checksum;

	/** The action. */
	private ActionTypeDTO action;

	/** The content. */
	private byte[] content;

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
	 * With id.
	 *
	 * @param id
	 *            the id
	 * @return the system element po
	 */
	public SystemElementDTO withId(int id) {
		this.id = id;
		return this;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public ElementTypeDTO getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(ElementTypeDTO type) {
		this.type = type;
	}

	/**
	 * With type.
	 *
	 * @param type
	 *            the type
	 * @return the system element po
	 */
	public SystemElementDTO withType(ElementTypeDTO type) {
		this.type = type;
		return this;
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
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * With name.
	 *
	 * @param name
	 *            the name
	 * @return the system element po
	 */
	public SystemElementDTO withName(String name) {
		this.name = name;
		return this;
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
	 * With description.
	 *
	 * @param description
	 *            the description
	 * @return the system element po
	 */
	public SystemElementDTO withDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Gets the checksum.
	 *
	 * @return the checksum
	 */
	public String getChecksum() {
		return checksum;
	}

	/**
	 * Sets the checksum.
	 *
	 * @param checksum
	 *            the new checksum
	 */
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	/**
	 * With checksum.
	 *
	 * @param checksum
	 *            the checksum
	 * @return the system element po
	 */
	public SystemElementDTO withChecksum(String checksum) {
		this.checksum = checksum;
		return this;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public ActionTypeDTO getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action
	 *            the new action
	 */
	public void setAction(ActionTypeDTO action) {
		this.action = action;
	}

	/**
	 * With action.
	 *
	 * @param action
	 *            the action
	 * @return the system element po
	 */
	public SystemElementDTO withAction(ActionTypeDTO action) {
		this.action = action;
		return this;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 *
	 * @param content
	 *            the new content
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

	/**
	 * With content.
	 *
	 * @param content
	 *            the content
	 * @return the system element po
	 */
	public SystemElementDTO withContent(byte[] content) {
		this.content = content;
		return this;
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
	 * With created at.
	 *
	 * @param createdAt
	 *            the created at
	 * @return the system element po
	 */
	public SystemElementDTO withCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
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
	 * With created by.
	 *
	 * @param createdBy
	 *            the created by
	 * @return the system element po
	 */
	public SystemElementDTO withCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
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
	 * With updated at.
	 *
	 * @param updatedAt
	 *            the updated at
	 * @return the system element po
	 */
	public SystemElementDTO withUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
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

	/**
	 * With updated by.
	 *
	 * @param updatedBy
	 *            the updated by
	 * @return the system element po
	 */
	public SystemElementDTO withUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
		return this;
	}
}

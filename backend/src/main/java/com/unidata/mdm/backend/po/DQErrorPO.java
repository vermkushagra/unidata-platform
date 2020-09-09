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

/**
 *
 */
package com.unidata.mdm.backend.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The Class DQErrorPO.
 */
public class DQErrorPO {

	/** The id. */
	private long id;

	/** The request id. */
	private String requestId;

	/** The record id. */
	private String recordId;

	/** The entity name. */
	private String entityName;

	/** The rule name. */
	private List<String> ruleName;

	/** The severity. */
	private List<String> severity;

	/** The status. */
	private List<String> status;

	/** The category. */
	private List<String> category;

	/** The message. */
	private List<String> message;

	/** The created at. */
	private Date createdAt;

	/** The created by. */
	private String createdBy;

	/** The updated at. */
	private Date updatedAt;

	/** The updated by. */
	private String updatedBy;

	/**
	 * Instantiates a new DQ error PO.
	 */
	public DQErrorPO() {
		super();
	}

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
	 * @param id
	 *            the new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the request id.
	 *
	 * @return the request id
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * Sets the request id.
	 *
	 * @param requestId
	 *            the new request id
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * Gets the record id.
	 *
	 * @return the record id
	 */
	public String getRecordId() {
		return recordId;
	}

	/**
	 * Sets the record id.
	 *
	 * @param recordId
	 *            the new record id
	 */
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	/**
	 * Gets the entity name.
	 *
	 * @return the entity name
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Sets the entity name.
	 *
	 * @param entityName
	 *            the new entity name
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * Gets the rule name.
	 *
	 * @return the rule name
	 */
	public List<String> getRuleName() {
		if (this.ruleName == null) {
			this.ruleName = new ArrayList<>();
		}
		return ruleName;
	}

	/**
	 * Sets the rule name.
	 *
	 * @param ruleName
	 *            the new rule name
	 */
	public void setRuleName(List<String> ruleName) {
		this.ruleName = ruleName;
	}

	/**
	 * Adds the rule name.
	 *
	 * @param ruleName the rule name
	 */
	public void addRuleName(String ruleName) {
		if (this.ruleName == null) {
			this.ruleName = new ArrayList<>();
		}
		this.ruleName.add(ruleName);
	}

	/**
	 * Gets the severity.
	 *
	 * @return the severity
	 */
	public List<String> getSeverity() {
		if (this.severity == null) {
			this.severity = new ArrayList<>();
		}
		return severity;
	}

	/**
	 * Sets the severity.
	 *
	 * @param severity
	 *            the new severity
	 */
	public void setSeverity(List<String> severity) {
		this.severity = severity;
	}

	/**
	 * Adds the severity.
	 *
	 * @param severity the severity
	 */
	public void addSeverity(String severity) {
		if (this.severity == null) {
			this.severity = new ArrayList<>();
		}
		this.severity.add(severity);
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public List<String> getStatus() {
		if (this.status == null) {
			this.status = new ArrayList<>();
		}
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the new status
	 */
	public void setStatus(List<String> status) {
		this.status = status;
	}

	/**
	 * Adds the status.
	 *
	 * @param status the status
	 */
	public void addStatus(String status) {
		if (this.status == null) {
			this.status = new ArrayList<>();
		}
		this.status.add(status);
	}

	/**
	 * Gets the category.
	 *
	 * @return the category
	 */
	public List<String> getCategory() {
		if (this.category == null) {
			this.category = new ArrayList<>();
		}
		return category;
	}

	/**
	 * Sets the category.
	 *
	 * @param category
	 *            the new category
	 */
	public void setCategory(List<String> category) {
		this.category = category;
	}

	/**
	 * Adds the category.
	 *
	 * @param category the category
	 */
	public void addCategory(String category) {
		if (this.category == null) {
			this.category = new ArrayList<>();
		}
		this.category.add(category);
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public List<String> getMessage() {
		if (this.message == null) {
			this.message = new ArrayList<>();
		}
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message
	 *            the new message
	 */
	public void setMessage(List<String> message) {
		this.message = message;
	}

	/**
	 * Adds the message.
	 *
	 * @param message the message
	 */
	public void addMessage(String message) {
		if (this.message == null) {
			this.message = new ArrayList<>();
		}
		this.message.add(message);
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DQErrorPO [id=" + id + ", requestId=" + requestId + ", recordId=" + recordId + ", entityName="
				+ entityName + ", ruleName=" + ruleName + ", severity=" + severity + ", status=" + status
				+ ", category=" + category + ", message=" + message + ", createdAt=" + createdAt + ", createdBy="
				+ createdBy + ", updatedAt=" + updatedAt + ", updatedBy=" + updatedBy + "]";
	}

}

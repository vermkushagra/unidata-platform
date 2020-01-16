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

package org.unidata.mdm.meta.type.ie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The Class MetaEntry.
 * 
 * @author ilya.bykov
 */
public class MetaVertex implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	private String id;

	/** The display name. */
	private String displayName;

	/** The type. */
	private MetaType type;

	/** The action. */
	private MetaAction action;

	/** The status. */
	private MetaExistence status;

	/** The messages. */
	private List<MetaMessage> messages;

	/** The custom props. */
	private Map<MetaPropKey, String> customProps;

	/**
	 * Instantiates a new meta vertex.
	 */
	private MetaVertex() {
		super();
	}

	/**
	 * Instantiates a new meta vertex.
	 *
	 * @param id
	 *            the id
	 * @param type
	 *            the type
	 */
	public MetaVertex(String id, MetaType type) {
		this();
		this.id = id;
		this.type = type;
	}

	/**
	 * Instantiates a new meta vertex.
	 *
	 * @param id
	 *            the id
	 * @param displayName
	 *            the display name
	 * @param type
	 *            the type
	 * @param action
	 *            the action
	 * @param status
	 *            the status
	 * @param message
	 *            the message
	 */
	public MetaVertex(String id, String displayName, MetaType type, MetaAction action, MetaExistence status,
			MetaMessage... message) {
		this();
		this.id = id;
		this.displayName = displayName;
		this.type = type;
		this.action = action;
		this.status = status;
		this.messages = new ArrayList<>(Arrays.asList(message));

	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MetaType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(MetaType type) {
		this.type = type;
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
	 * Gets the action.
	 *
	 * @return the action
	 */
	public MetaAction getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action
	 *            the new action
	 */
	public void setAction(MetaAction action) {
		this.action = action;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public MetaExistence getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the new status
	 */
	public void setStatus(MetaExistence status) {
		this.status = status;
	}

	/**
	 * Gets the messages.
	 *
	 * @return the messages
	 */
	public List<MetaMessage> getMessages() {
		if (this.messages == null) {
			this.messages = new ArrayList<>();
		}
		return messages;
	}

	/**
	 * Sets the messages.
	 *
	 * @param messages
	 *            the new messages
	 */
	public void setMessages(List<MetaMessage> messages) {
		this.messages = messages;
	}

	/**
	 * Gets the custom props.
	 *
	 * @return the custom props
	 */
	public Map<MetaPropKey, String> getCustomProps() {
		return customProps;
	}

	/**
	 * Sets the custom props.
	 *
	 * @param customProps
	 *            the custom props
	 */
	public void setCustomProps(Map<MetaPropKey, String> customProps) {
		this.customProps = customProps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + typeHashCode();
		return result;
	}

	private int typeHashCode() {
		if (type == null) {
			return 0;
		}
		if((type == MetaType.CF || type == MetaType.COMPOSITE_CF || type == MetaType.CUSTOM_CF)){
			return MetaType.CF.hashCode();
		}
		if((type == MetaType.ENTITY || type == MetaType.LOOKUP)){
			return MetaType.ENTITY.hashCode();
		}
		return type.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
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
		MetaVertex other = (MetaVertex) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		if ((type == MetaType.CF || type == MetaType.COMPOSITE_CF || type == MetaType.CUSTOM_CF)
				&& (other.type == MetaType.CF || other.type == MetaType.CUSTOM_CF
						|| other.type == MetaType.COMPOSITE_CF)) {
			return true;
		}
		if ((type == MetaType.ENTITY || type == MetaType.LOOKUP)
				&& (other.type == MetaType.ENTITY || other.type == MetaType.LOOKUP)) {
			return true;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}
}

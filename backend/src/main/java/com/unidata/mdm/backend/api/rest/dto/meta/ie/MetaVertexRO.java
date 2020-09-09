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

package com.unidata.mdm.backend.api.rest.dto.meta.ie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class MetaVertexRO.
 * @author ilya.bykov
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaVertexRO {

	/** The id. */
	private String id;

	/** The display name. */
	private String displayName;

	/** The action. */
	private MetaActionRO action;

	/** The status. */
	private MetaExistenceRO existence;
	
	/** The statuses. */
	private List<MetaMessageRO> statuses;
	
	/** The custom props. */
	private List<MetaCustomPropRO> customProps;
	/** The type. */
	private MetaTypeRO type;

	/**
	 * Instantiates a new meta vertex RO.
	 */
	public MetaVertexRO() {

	}

	/**
	 * Instantiates a new meta vertex RO.
	 *
	 * @param id            the id
	 * @param displayName            the display name
	 * @param action            the action
	 * @param type            the type
	 * @param existence the existence
	 * @param statuses the statuses
	 */
	public MetaVertexRO(String id, String displayName, MetaActionRO action, MetaTypeRO type, MetaExistenceRO existence,
			MetaMessageRO... statuses) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.action = action;
		this.type = type;
		this.existence = existence;
		this.statuses = Arrays.asList(statuses);
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
	public MetaActionRO getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action
	 *            the new action
	 */
	public void setAction(MetaActionRO action) {
		this.action = action;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MetaTypeRO getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *            the new type
	 */
	public void setType(MetaTypeRO type) {
		this.type = type;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public MetaExistenceRO getExistence() {
		return existence;
	}

	/**
	 * Sets the status.
	 *
	 * @param existence the new existence
	 */
	public void setExistence(MetaExistenceRO existence) {
		this.existence = existence;
	}

	/**
	 * Gets the messages.
	 *
	 * @return the messages
	 */
	public List<MetaMessageRO> getStatuses() {
		if (this.statuses == null) {
			this.statuses = new ArrayList<>();
		}
		return this.statuses;
	}

	/**
	 * Sets the messages.
	 *
	 * @param statuses the new statuses
	 */
	public void setStatuses(List<MetaMessageRO> statuses) {
		this.statuses = statuses;
	}

	/**
	 * Gets the custom props.
	 *
	 * @return the custom props
	 */
	public List<MetaCustomPropRO> getCustomProps() {
		return customProps;
	}

	/**
	 * Sets the custom props.
	 *
	 * @param customProps the new custom props
	 */
	public void setCustomProps(List<MetaCustomPropRO> customProps) {
		this.customProps = customProps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetaVertexRO [id=" + id + ", displayName=" + displayName + ", action=" + action + ", type=" + type
				+ "]";
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
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
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
		MetaVertexRO other = (MetaVertexRO) obj;
		if (action != other.action)
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}

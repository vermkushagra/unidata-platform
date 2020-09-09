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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class MetaEdgeRO.
 * @author ilya.bykov
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaEdgeRO {

	/** The from. */
	private MetaVertexRO from;

	/** The to. */
	private MetaVertexRO to;
	
	/** The existence. */
	private MetaExistenceRO existence;

	/**
	 * Instantiates a new meta edge RO.
	 */
	public MetaEdgeRO() {

	}

	/**
	 * Instantiates a new meta edge RO.
	 *
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	public MetaEdgeRO(MetaVertexRO from, MetaVertexRO to) {
		super();
		this.from = from;
		this.to = to;
	}

	/**
	 * Gets the from.
	 *
	 * @return the from
	 */
	public MetaVertexRO getFrom() {
		return from;
	}

	/**
	 * Sets the from.
	 *
	 * @param from
	 *            the new from
	 */
	public void setFrom(MetaVertexRO from) {
		this.from = from;
	}

	/**
	 * Gets the to.
	 *
	 * @return the to
	 */
	public MetaVertexRO getTo() {
		return to;
	}

	/**
	 * Sets the to.
	 *
	 * @param to
	 *            the new to
	 */
	public void setTo(MetaVertexRO to) {
		this.to = to;
	}

	/**
	 * Gets the existence.
	 *
	 * @return the existence
	 */
	public MetaExistenceRO getExistence() {
		return existence;
	}

	/**
	 * Sets the existence.
	 *
	 * @param existence the new existence
	 */
	public void setExistence(MetaExistenceRO existence) {
		this.existence = existence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetaEdgeRO [from=" + from + ", to=" + to + "]";
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
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		MetaEdgeRO other = (MetaEdgeRO) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

}

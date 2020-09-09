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

package com.unidata.mdm.backend.service.data.export.xlsx;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Key for the main displayable cache.
 */
public class DNKey implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The etalon id. */
	private String etalonId;

	/** The as of. */
	private Date asOf;

	/** The entity name. */
	private String entityName;

	/** The field name. */
	private List<String> fieldName;

	/**
	 * Not a singleton and not supposed to be.
	 * 
	 * @return new instance of the {@link DNKey}
	 */
	public static DNKey newInstance() {
		return new DNKey();
	}

	/**
	 * Gets the etalon id.
	 *
	 * @return the etalon id
	 */
	public String getEtalonId() {
		return etalonId;
	}

	/**
	 * Gets the as of.
	 *
	 * @return the as of
	 */
	public Date getAsOf() {
		return asOf;
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
	 * With etalon id.
	 *
	 * @param etalonId
	 *            the etalon id
	 * @return the DN key
	 */
	public DNKey withEtalonId(String etalonId) {
		this.etalonId = etalonId;
		return this;
	}

	/**
	 * With as of.
	 *
	 * @param asOf
	 *            the as of
	 * @return the DN key
	 */
	public DNKey withAsOf(Date asOf) {
		this.asOf = asOf;
		return this;
	}

	/**
	 * With entity name.
	 *
	 * @param entityName
	 *            the entity name
	 * @return the DN key
	 */
	public DNKey withEntityName(String entityName) {
		this.entityName = entityName;
		return this;
	}

	/**
	 * With field name.
	 *
	 * @param fieldName
	 *            the field name
	 * @return the DN key
	 */
	public DNKey withFieldName(List<String> fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	/**
	 * Gets the field name.
	 *
	 * @return the field name
	 */
	public List<String> getFieldName() {
		return fieldName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((asOf == null) ? 0 : asOf.hashCode());
		result = prime * result + ((entityName == null) ? 0 : entityName.hashCode());
		result = prime * result + ((etalonId == null) ? 0 : etalonId.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DNKey other = (DNKey) obj;
		if (asOf == null) {
			if (other.asOf != null) {
				return false;
			}
		} else if (!asOf.equals(other.asOf)) {
			return false;
		}
		if (entityName == null) {
			if (other.entityName != null) {
				return false;
			}
		} else if (!entityName.equals(other.entityName)) {
			return false;
		}
		if (etalonId == null) {
			if (other.etalonId != null) {
				return false;
			}
		} else if (!etalonId.equals(other.etalonId)) {
			return false;
		}
		if (fieldName == null) {
			if (other.fieldName != null) {
				return false;
			}
		} else if (!fieldName.equals(other.fieldName)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DNKey [etalonId=").append(etalonId).append(", asOf=").append(asOf).append(", entityName=")
				.append(entityName).append(", fieldName=").append(fieldName).append("]");
		return builder.toString();
	}

}

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

package com.unidata.mdm.backend.api.rest.dto.dataimport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class ImportParams.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportParams {

	/** The entity name. */
	private String entityName;

	/** The source system. */
	private String sourceSystem;

	/** Flag indicates merge with previous version flow. */
	private boolean mergeWithPreviousVersion;

	/**
	 * Gets the entity name.
	 *
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * Sets the entity name.
	 *
	 * @param entityName
	 *            the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * Gets the source system.
	 *
	 * @return the sourceSystem
	 */
	public String getSourceSystem() {
		return sourceSystem;
	}

	/**
	 * Sets the source system.
	 *
	 * @param sourceSystem
	 *            the sourceSystem to set
	 */
	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	/**
	 * Is merge merge with previous version flow enabled.
	 * @return
	 */
	public boolean isMergeWithPreviousVersion() {
		return mergeWithPreviousVersion;
	}

	/**
	 * Set flag.
	 * @param mergeWithPreviousVersion true if importing records should be merged, false otherwise
	 */
	public void setMergeWithPreviousVersion(boolean mergeWithPreviousVersion) {
		this.mergeWithPreviousVersion = mergeWithPreviousVersion;
	}
}

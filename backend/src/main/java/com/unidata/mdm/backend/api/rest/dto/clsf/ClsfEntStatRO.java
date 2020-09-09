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

package com.unidata.mdm.backend.api.rest.dto.clsf;

import com.unidata.mdm.backend.api.rest.RestConstants;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * The Class ClassifiedEntStatRO.
 */
public class ClsfEntStatRO {

	/** The name. */
	private String name;
	
	/** The display name. */
	private String displayName;
	
	/** The type. */
	private String type;
	
	/** The count. */
	private long count;

	/**
	 * Instantiates a new classified ent stat RO.
	 *
	 * @param le the le
	 */
	public ClsfEntStatRO(LookupEntityDef le) {
		super();
		this.name = le.getName();
		this.type = RestConstants.LOOKUP_ENTITY_TYPE;
		this.displayName = le.getDisplayName();
	}

	/**
	 * Instantiates a new classified ent stat RO.
	 *
	 * @param e the e
	 */
	public ClsfEntStatRO(EntityDef e) {
		super();
		this.name = e.getName();
		this.type = RestConstants.REGISTER_ENTITY_TYPE;
		this.displayName = e.getDisplayName();
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
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public long getCount() {
		return this.count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count the new count
	 */
	public void setCount(long count) {
		this.count = count;
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
	 * @param displayName the new display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}

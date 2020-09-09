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
package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author mikhail
 * Merge settings for an attributes (REST version).
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MergeAttributeDefRO {
	/**
	 * Name of the attribute.
	 */
	private String name;
	
	/**
	 * Source systems.
	 */
	private List<SourceSystemDefinition> sourceSystemsConfig = new ArrayList<>();

	/**
	 * @return the sourceSystems
	 */
	public List<SourceSystemDefinition> getSourceSystemsConfig() {
		return sourceSystemsConfig;
	}

	/**
	 * @param sourceSystems the sourceSystems to set
	 */
	public void setSourceSystemsConfig(List<SourceSystemDefinition> sourceSystems) {
		this.sourceSystemsConfig = sourceSystems;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}

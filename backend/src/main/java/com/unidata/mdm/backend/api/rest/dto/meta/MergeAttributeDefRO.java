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

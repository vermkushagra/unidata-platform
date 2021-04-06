/**
 * 
 */
package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author mikhail
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BVRMergeSettingsDefRO {
	
	/**
	 * Source systems.
	 */
	private List<SourceSystemDefinition> sourceSystemsConfig = new ArrayList<>();

	/**
	 * @return the sourceSystemsConfig
	 */
	public List<SourceSystemDefinition> getSourceSystemsConfig() {
		return sourceSystemsConfig;
	}

	/**
	 * @param sourceSystemsConfig the sourceSystemsConfig to set
	 */
	public void setSourceSystemsConfig(List<SourceSystemDefinition> sourceSystemsConfig) {
		this.sourceSystemsConfig = sourceSystemsConfig;
	}
	
}

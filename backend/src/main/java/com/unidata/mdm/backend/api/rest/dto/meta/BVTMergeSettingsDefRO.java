/**
 * 
 */
package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author mikhail
 * BVT merge settings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BVTMergeSettingsDefRO {

	/**
	 * Attribute definitions.
	 */
	private List<MergeAttributeDefRO> attributes = new ArrayList<>();
	
	/**
	 * @return the attributes
	 */
	public List<MergeAttributeDefRO> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<MergeAttributeDefRO> attributes) {
		this.attributes = attributes;
	}
	
}

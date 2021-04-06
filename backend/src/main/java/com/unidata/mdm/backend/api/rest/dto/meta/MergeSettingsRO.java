/**
 * 
 */
package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author mikhail
 * Merge settings aggregating type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MergeSettingsRO {
	/**
	 * BVT merge settings.
	 */
	private BVTMergeSettingsDefRO bvtMergeSettings;
	
	/**
	 * BVR merge settings.
	 */
	private BVRMergeSettingsDefRO bvrMergeSettings;

	/**
	 * @return the bvtMergeSettings
	 */
	public BVTMergeSettingsDefRO getBvtMergeSettings() {
		return bvtMergeSettings;
	}

	/**
	 * @param bvtMergeSettings the bvtMergeSettings to set
	 */
	public void setBvtMergeSettings(BVTMergeSettingsDefRO bvtMergeSettings) {
		this.bvtMergeSettings = bvtMergeSettings;
	}

	/**
	 * @return the bvrMergeSettings
	 */
	public BVRMergeSettingsDefRO getBvrMergeSettings() {
		return bvrMergeSettings;
	}

	/**
	 * @param bvrMergeSettings the bvrMergeSettings to set
	 */
	public void setBvrMergeSettings(BVRMergeSettingsDefRO bvrMergeSettings) {
		this.bvrMergeSettings = bvrMergeSettings;
	}
}

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

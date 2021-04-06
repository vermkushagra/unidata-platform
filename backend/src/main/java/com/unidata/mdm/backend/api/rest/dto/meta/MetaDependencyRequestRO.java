package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaTypeRO;


/**
 * The Class MetaDependencyRequestRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaDependencyRequestRO {
	
	/** The storage id. */
	private String storageId;
	
	/** The for types. */
	private List<MetaTypeRO> forTypes;
	
	/** The skip types. */
	private List<MetaTypeRO> skipTypes;

	/**
	 * Gets the storage id.
	 *
	 * @return the storage id
	 */
	public String getStorageId() {
		return storageId;
	}

	/**
	 * Sets the storage id.
	 *
	 * @param storageId the new storage id
	 */
	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	/**
	 * Gets the for types.
	 *
	 * @return the for types
	 */
	public List<MetaTypeRO> getForTypes() {
		return forTypes;
	}

	/**
	 * Sets the for types.
	 *
	 * @param forTypes the new for types
	 */
	public void setForTypes(List<MetaTypeRO> forTypes) {
		this.forTypes = forTypes;
	}

	/**
	 * Gets the skip types.
	 *
	 * @return the skip types
	 */
	public List<MetaTypeRO> getSkipTypes() {
		return skipTypes;
	}

	/**
	 * Sets the skip types.
	 *
	 * @param skipTypes the new skip types
	 */
	public void setSkipTypes(List<MetaTypeRO> skipTypes) {
		this.skipTypes = skipTypes;
	}
}

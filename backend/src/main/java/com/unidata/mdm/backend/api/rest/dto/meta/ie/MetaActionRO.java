
package com.unidata.mdm.backend.api.rest.dto.meta.ie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author ilya.bykov 
 * The Enum MetaActionRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public enum MetaActionRO {

	/** The create. */
	UPSERT,

	/** The no action. */
	NONE;

	/**
	 * From value.
	 *
	 * @param v
	 *            the v
	 * @return the meta action RO
	 */
	@JsonCreator
	public static MetaActionRO fromValue(String v) {

		return MetaActionRO.valueOf(v);
	}
}

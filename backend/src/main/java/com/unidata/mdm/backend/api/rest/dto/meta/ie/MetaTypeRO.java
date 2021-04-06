package com.unidata.mdm.backend.api.rest.dto.meta.ie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Enum MetaTypeRO.
 * @author ilya.bykov
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public enum MetaTypeRO {
	/** The entity. */
	ENTITY,

	/** The lookup. */
	LOOKUP,

	/** The enum. */
	ENUM,

	/** The classifier. */
	CLASSIFIER,

	/** The measure. */
	MEASURE,

	/** The custom cf. */
	CUSTOM_CF,

	/** The composite cf. */
	COMPOSITE_CF,

	/** The match rule. */
	MATCH_RULE,

	/** The merge rule. */
	MERGE_RULE,

	/** The sorce system. */
	SOURCE_SYSTEM,
	/** The zip. */
	ZIP,

	/** The relation. */
	RELATION,
	
	/** The nested entity. */
	NESTED_ENTITY,
	
	/** The groups. */
	GROUPS;
	/**
	 * From value.
	 *
	 * @param v
	 *            the v
	 * @return the meta type RO
	 */
	@JsonCreator
	public static MetaTypeRO fromValue(String v) {

		return MetaTypeRO.valueOf(v);
	}
}

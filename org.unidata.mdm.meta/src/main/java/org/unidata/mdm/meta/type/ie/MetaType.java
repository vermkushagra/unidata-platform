package org.unidata.mdm.meta.type.ie;

import java.io.Serializable;

/**
 * The Enum MetaType.
 */
public enum MetaType implements Serializable{

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
	CF,
	
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
	NESTED_ENTITY,
	GROUPS
}

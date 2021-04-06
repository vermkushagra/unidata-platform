package com.unidata.mdm.backend.api.rest.dto.clsf;

import com.unidata.mdm.backend.api.rest.RestConstants;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * The Class ClassifiedEntStatRO.
 */
public class ClsfEntStatRO {

	/** The name. */
	private String name;
	
	/** The display name. */
	private String displayName;
	
	/** The type. */
	private String type;
	
	/** The count. */
	private long count;

	/**
	 * Instantiates a new classified ent stat RO.
	 *
	 * @param le the le
	 */
	public ClsfEntStatRO(LookupEntityDef le) {
		super();
		this.name = le.getName();
		this.type = RestConstants.LOOKUP_ENTITY_TYPE;
		this.displayName = le.getDisplayName();
	}

	/**
	 * Instantiates a new classified ent stat RO.
	 *
	 * @param e the e
	 */
	public ClsfEntStatRO(EntityDef e) {
		super();
		this.name = e.getName();
		this.type = RestConstants.REGISTER_ENTITY_TYPE;
		this.displayName = e.getDisplayName();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	public long getCount() {
		return this.count;
	}

	/**
	 * Sets the count.
	 *
	 * @param count the new count
	 */
	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName the new display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}

package com.unidata.mdm.backend.api.rest.dto.security;


/**
 * The Class UserAPIRO.
 * @author ilya.bykov
 */
public class UserAPIRO{

/** The name. */
private String name;

/** The display name. */
private String displayName;

/** The description. */
private String description;

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

/**
 * Gets the description.
 *
 * @return the description
 */
public String getDescription() {
	return description;
}

/**
 * Sets the description.
 *
 * @param description the new description
 */
public void setDescription(String description) {
	this.description = description;
}
}

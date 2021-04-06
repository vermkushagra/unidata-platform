package com.unidata.mdm.backend.common.dto.security;

import java.io.Serializable;

import com.unidata.mdm.backend.common.integration.auth.CustomProperty;

/**
 * The Class UserProperty.
 *
 * @author ilya.bykov
 */
public class UserPropertyDTO implements CustomProperty, Serializable {
	/**
     * SVUID.
     */
    private static final long serialVersionUID = -8381928058844771461L;

    /** Identifier. */
	private Long id;

	/** The name. */
	private String name;

	/** The display name. */
	private String displayName;

	/** The value. */
	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@Override
    public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	@Override
    public String getDisplayName() {
		return displayName;
	}

	/**
	 * Sets the display name.
	 *
	 * @param displayName
	 *            the new display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	@Override
    public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}

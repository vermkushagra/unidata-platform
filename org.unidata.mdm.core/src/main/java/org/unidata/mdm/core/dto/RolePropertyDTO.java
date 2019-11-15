/**
 * Date: 05.07.2016
 */

package org.unidata.mdm.core.dto;

import java.io.Serializable;

import org.unidata.mdm.core.type.security.CustomProperty;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class RolePropertyDTO implements CustomProperty, Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6203608704528641376L;

    /** Identifier. */
    private Long id;

    /** The name. */
   	private String name;
   	/**
   	 * Is required.
   	 */
   	private boolean required;

   	/** The display name. */
   	private String displayName;

   	/** The value. */
   	private String value;

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Checks if is required.
     *
     * @return true, if is required
     */
    public boolean isRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 *
	 * @param required the new required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.common.integration.auth.CustomProperty#getName()
	 */
	@Override
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

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.integration.auth.CustomProperty#getDisplayName()
     */
    @Override
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

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.integration.auth.CustomProperty#getValue()
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }
}

/**
 * Date: 05.07.2016
 */

package com.unidata.mdm.backend.common.dto.security;

import java.io.Serializable;

import com.unidata.mdm.backend.common.integration.auth.CustomProperty;

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

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

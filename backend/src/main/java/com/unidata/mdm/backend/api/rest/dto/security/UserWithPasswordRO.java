package com.unidata.mdm.backend.api.rest.dto.security;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// TODO: Auto-generated Javadoc
/**
 * The Class UserWithPasswordRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserWithPasswordRO extends UserRO {

    /** The password. */
    private String password;

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
    	if(StringUtils.isEmpty(password)){
    		this.password = null;
    		return;
    	}
        this.password = password;
    }
}

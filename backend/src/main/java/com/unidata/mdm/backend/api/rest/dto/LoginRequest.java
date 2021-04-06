package com.unidata.mdm.backend.api.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class LoginRequest.
 */
public class LoginRequest {

    /** The user name. */
    @JsonProperty(index = 1, required = true, value = "userName")
    private String userName;

    /** The password. */
    @JsonProperty(index = 2, required = true, value = "password")
    private String password;


    /**
     * Gets the user name.
     *
     * @return the userName
     */
    public String getUserName() {
	return userName;
    }

    /**
     * Sets the user name.
     *
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
	this.userName = userName;
    }

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
     *            the password to set
     */
    public void setPassword(String password) {
	this.password = password;
    }


}

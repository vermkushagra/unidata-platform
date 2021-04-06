package com.unidata.mdm.backend.api.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TODO !!!!!Rewrite all this code!!!!!
 */
public class SetPasswordRequest {
    /** The user name. */
    @JsonProperty(index = 1, required = true, value = "userName")
    private String userName;

    /** The password. */
    @JsonProperty(index = 2, required = true, value = "password")
    private String password;

    /** The password. */
    @JsonProperty(index = 3, required = false, value = "oldPassword")
    private String oldPassword;

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

    /**
     * Gets old password.
     * @return old password
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Sets old password.
     * @param  oldPassword old password
     * @param  oldPassword old password
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}

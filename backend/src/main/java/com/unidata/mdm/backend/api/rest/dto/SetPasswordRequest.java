/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

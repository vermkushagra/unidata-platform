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

package com.unidata.mdm.backend.common.dto.security;

public class PasswordDTO extends BaseSecurityDTO {
    private final UserDTO user;
    private final String hashedText;
    private final String text;
    private final boolean isActive;

    public PasswordDTO(UserDTO user, String hashedText, boolean isActive) {
        this.user = user;
        this.hashedText = hashedText;
        this.isActive = isActive;
        this.text = null;
    }

    public PasswordDTO(UserDTO user, String hashedText, String text, boolean isActive) {
        this.user = user;
        this.hashedText = hashedText;
        this.text = text;
        this.isActive = isActive;
    }

    public UserDTO getUser() {
        return user;
    }

    public String getHashedText() {
        return hashedText;
    }

    public String getText() {
        return text;
    }

    public boolean isActive() {
        return isActive;
    }
}

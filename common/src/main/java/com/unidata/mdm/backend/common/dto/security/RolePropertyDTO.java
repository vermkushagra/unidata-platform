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

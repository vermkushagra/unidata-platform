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

package com.unidata.mdm.backend.api.rest.dto.security;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class SecuredResourceRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecuredResourceRO extends BaseSecurityRO {

    /** The name. */
    private String name;

    /** The display name. */
    private String displayName;

    /** The type. */
    private SecuredResourceTypeRO type;

    /** The type. */
    private SecuredResourceCategoryRO category;

    /** Parent. */
    private SecuredResourceRO parent;

    /** Children. */
    private List<SecuredResourceRO> children;
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
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the display name.
     *
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public SecuredResourceTypeRO getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(SecuredResourceTypeRO type) {
        this.type = type;
    }



    /**
     * @return the category
     */
    public SecuredResourceCategoryRO getCategory() {
        return category;
    }


    /**
     * @param category the category to set
     */
    public void setCategory(SecuredResourceCategoryRO category) {
        this.category = category;
    }

    /**
     * @return the parent
     */
    public SecuredResourceRO getParent() {
        return parent;
    }


    /**
     * @param parent the parent to set
     */
    public void setParent(SecuredResourceRO parent) {
        this.parent = parent;
    }


    /**
     * @return the children
     */
    public List<SecuredResourceRO> getChildren() {
        return children;
    }


    /**
     * @param children the children to set
     */
    public void setChildren(List<SecuredResourceRO> children) {
        this.children = children;
    }

}

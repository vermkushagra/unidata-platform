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

package com.unidata.mdm.backend.api.rest.dto.clsf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class ClassifierRO.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ClsfRO {

    /** The name. */
    private String name;

    /** The display name. */
    private String displayName;

    /** The description. */
    private String description;

    /** The code pattern. */
    @JsonProperty(required = false)
    private String codePattern;

    /** The children. */
    private List<ClsfNodeRO> children;

    private boolean validateCodeByLevel;
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
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description
     *            the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the code pattern.
     *
     * @return the code pattern
     */
    public String getCodePattern() {
        return codePattern;
    }

    /**
     * Sets the code pattern.
     *
     * @param codePattern
     *            the new code pattern
     */
    public void setCodePattern(String codePattern) {
        this.codePattern = codePattern;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public List<ClsfNodeRO> getChildren() {
        if(this.children==null){
            this.children = new ArrayList<>();
        }
        return children;
    }

    /**
     * Sets the children.
     *
     * @param children
     *            the new children
     */
    public void setChildren(List<ClsfNodeRO> children) {
        this.children = children;
    }

    public boolean isValidateCodeByLevel() {
        return validateCodeByLevel;
    }

    public void setValidateCodeByLevel(boolean validateCodeByLevel) {
        this.validateCodeByLevel = validateCodeByLevel;
    }
}
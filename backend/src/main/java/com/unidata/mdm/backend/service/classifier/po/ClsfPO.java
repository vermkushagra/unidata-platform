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

package com.unidata.mdm.backend.service.classifier.po;

import java.util.Date;

/**
 * The Class ClassifierPO.
 */
public class ClsfPO{

    /** The name. */
    private String name;

    /** The display name. */
    private String displayName;

    /** The description. */
    private String description;

    /** The code pattern. */
    private String codePattern;

    /** The created at. */
    private Date createdAt;

    /** The updated at. */
    private Date updatedAt;

    /** The created by. */
    private String createdBy;

    /** The updated by. */
    private String updatedBy;

    /** The root node. */
    private ClsfNodePO rootNode;

    /** Validate code by level */
    private boolean validateCodeByLevel = true;

    /**
     * The Enum FieldColumns.
     */
    public enum FieldColumns {

        /** The name. */
        NAME,

        /** The display name. */
        DISPLAY_NAME,

        /** The code pattern. */
        CODE_PATTERN,

        /** The description. */
        DESCRIPTION,

        /** The created at. */
        CREATED_AT,

        /** The updated at. */
        UPDATED_AT,

        /** The created by. */
        CREATED_BY,

        /** The updated by. */
        UPDATED_BY,

        VALIDATE_CODE_BY_LEVEL
    }
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
     * @param name the new name
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
     * @param displayName the new display name
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
     * @param description the new description
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
     * @param codePattern the new code pattern
     */
    public void setCodePattern(String codePattern) {
        this.codePattern = codePattern;
    }

    /**
     * Gets the root node.
     *
     * @return the root node
     */
    public ClsfNodePO getRootNode() {
        return rootNode;
    }

    /**
     * Sets the root node.
     *
     * @param rootNode the new root node
     */
    public void setRootNode(ClsfNodePO rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * Gets the created at.
     *
     * @return the created at
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the created at.
     *
     * @param createdAt the new created at
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the updated at.
     *
     * @return the updated at
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the updated at.
     *
     * @param updatedAt the new updated at
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the created by.
     *
     * @return the created by
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the created by.
     *
     * @param createdBy the new created by
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the updated by.
     *
     * @return the updated by
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the updated by.
     *
     * @param updatedBy the new updated by
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public boolean isValidateCodeByLevel() {
        return validateCodeByLevel;
    }

    public void setValidateCodeByLevel(boolean validateCodeByLevel) {
        this.validateCodeByLevel = validateCodeByLevel;
    }
}

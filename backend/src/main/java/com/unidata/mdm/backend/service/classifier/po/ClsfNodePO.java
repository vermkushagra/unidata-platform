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

import com.unidata.mdm.backend.po.MetaModelCustomPropertyPO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * The Class NodePO.
 */
public class ClsfNodePO {

    /** The id. */
    private int id;

    /** The clsf name */
    private String clsfName;

    /** The name. */
    private String name;

    /** The description. */
    private String description;

    /** The code. */
    private String code;

    /** The node id. */
    private String nodeId;

    /** The parent id. */
    private String parentId;

    /** The node simple attrs. */
    private List<ClsfNodeSimpleAttrPO> nodeSimpleAttrs;

    /** The node array attrs. */
    private List<ClsfNodeArrayAttrPO> nodeArrayAttrs;

    /** The children. */
    private List<ClsfNodePO> children;

    private String customProperties;

    /** The created at. */
    private Date createdAt;

    /** The updated at. */
    private Date updatedAt;

    /** The created by. */
    private String createdBy;

    /** The updated by. */
    private String updatedBy;

    /** The child count. */
    private int childCount;

    /** The has own attrs. */
    private boolean hasOwnAttrs;

    /**
     * The Enum FieldColumns.
     */
    public enum FieldColumns {

        /** The id. */
        ID,

        /** The name. */
        NAME,

        /** The description. */
        DESCRIPTION,

        /** The code. */
        CODE,

        /** The node id. */
        NODE_ID,

        /** The parent node id. */
        PARENT_NODE_ID,

        /** The created at. */
        CREATED_AT,

        /** The created by. */
        CREATED_BY,

        /** The updated at. */
        UPDATED_AT,

        /** The updated by. */
        UPDATED_BY,

        /** The clsf name */
        CLSF_NAME,

        /** The child count. */
        CHILD_COUNT,

        /** The path. */
        PATH,
        /** Custom properties */
        CUSTOM_PROPS
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get clsf name
     * @return clsf name
     */
    public String getClsfName() {
        return clsfName;
    }

    /**
     * Set clsf name
     * @param clsfName clsf name
     */
    public void setClsfName(String clsfName) {
        this.clsfName = clsfName;
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
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the parent id.
     *
     * @return the parent id
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Sets the parent id.
     *
     * @param parentId the new parent id
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }



    /**
     * Gets the node simple attrs.
     *
     * @return the node simple attrs
     */
    public List<ClsfNodeSimpleAttrPO> getNodeSimpleAttrs() {
        if (this.nodeSimpleAttrs == null) {
            this.nodeSimpleAttrs = new ArrayList<>();
        }
        return nodeSimpleAttrs;
    }

    /**
     * Sets the node simple attrs.
     *
     * @param nodeSimpleAttrs the new node simple attrs
     */
    public void setNodeSimpleAttrs(List<ClsfNodeSimpleAttrPO> nodeSimpleAttrs) {
        this.nodeSimpleAttrs = nodeSimpleAttrs;
    }

    /**
     * Gets the node array attrs.
     *
     * @return the node array attrs
     */
    public List<ClsfNodeArrayAttrPO> getNodeArrayAttrs() {
        if (nodeArrayAttrs == null) {
            nodeArrayAttrs = new ArrayList<>();
        }
        return nodeArrayAttrs;
    }

    /**
     * Sets the node array attrs.
     *
     * @param nodeArrayAttrs the new node array attrs
     */
    public void setNodeArrayAttrs(List<ClsfNodeArrayAttrPO> nodeArrayAttrs) {
        this.nodeArrayAttrs = nodeArrayAttrs;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public List<ClsfNodePO> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return children;
    }

    /**
     * Sets the children.
     *
     * @param children the new children
     */
    public void setChildren(List<ClsfNodePO> children) {
        this.children = children;
    }

    /**
     * Gets the node id.
     *
     * @return the node id
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId the new node id
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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

    /**
     * Gets the child count.
     *
     * @return the child count
     */
    public int getChildCount() {
        return childCount;
    }

    /**
     * Sets the child count.
     *
     * @param childCount the new child count
     */
    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    /**
     * Checks if is checks for own attrs.
     *
     * @return true, if is checks for own attrs
     */
    public boolean isHasOwnAttrs() {
        return hasOwnAttrs;
    }

    /**
     * Sets the checks for own attrs.
     *
     * @param hasOwnAttrs the new checks for own attrs
     */
    public void setHasOwnAttrs(boolean hasOwnAttrs) {
        this.hasOwnAttrs = hasOwnAttrs;
    }

    public String getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(String customProperties) {
        this.customProperties = customProperties;
    }



}

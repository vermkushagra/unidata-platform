package com.unidata.mdm.backend.common.dto.classifier.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Class NodeDTO.
 */
public class ClsfNodeDTO {

    /** The name. */
    private String name;

    /** The clsf name. */
    private String clsfName;

    /** The description. */
    private String description;

    /** The code. */
    private String code;

    /** The node id. */
    private String nodeId;

    /** The parent id. */
    private String parentId;

    /** The has own attrs. */
    private boolean hasOwnAttrs;

    /** The child count. */
    private int childCount;

    /** The node attrs. */
    private List<ClsfNodeAttrDTO> nodeAttrs;

    /** The children. */
    private List<ClsfNodeDTO> children;

    /** The created at. */
    private Date createdAt;

    /** The updated at. */
    private Date updatedAt;

    /** The created by. */
    private String createdBy;

    /** The updated by. */
    private String updatedBy;

    public ClsfNodeDTO() {
    }

    public ClsfNodeDTO(
            final String name,
            final String clsfName,
            final String description,
            final String code,
            final String nodeId,
            final String parentId,
            final boolean hasOwnAttrs,
            final int childCount,
            final List<ClsfNodeAttrDTO> nodeAttrs,
            final List<ClsfNodeDTO> children,
            final Date createdAt,
            final Date updatedAt,
            final String createdBy,
            final String updatedBy
    ) {
        this.name = name;
        this.clsfName = clsfName;
        this.description = description;
        this.code = code;
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.hasOwnAttrs = hasOwnAttrs;
        this.childCount = childCount;
        this.nodeAttrs = nodeAttrs;
        this.children = children;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public ClsfNodeDTO(
            final String name,
            final String nodeId,
            final String parentId,
            final String code,
            final String description,
            final String createdBy,
            final Date createdAt,
            final List<ClsfNodeAttrDTO> nodeAttrs
    ) {
        this.name = name;
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.code = code;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.nodeAttrs = nodeAttrs;
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
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the clsf name.
     *
     * @return the clsf name
     */
    public String getClsfName() {
        return clsfName;
    }

    /**
     * Sets the clsf name.
     *
     * @param clsfName
     *            the new clsf name
     */
    public void setClsfName(String clsfName) {
        this.clsfName = clsfName;
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
     * @param code
     *            the new code
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
     * @param parentId
     *            the new parent id
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the node attrs.
     *
     * @return the node attrs
     */
    public List<ClsfNodeAttrDTO> getNodeAttrs() {
        if (this.nodeAttrs == null) {
            this.nodeAttrs = new ArrayList<>();
        }
        return nodeAttrs;
    }

    /**
     * Sets the node attrs.
     *
     * @param nodeAttrs
     *            the new node attrs
     */
    public void setNodeAttrs(List<ClsfNodeAttrDTO> nodeAttrs) {
        this.nodeAttrs = nodeAttrs;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public List<ClsfNodeDTO> getChildren() {
        if (this.children == null) {
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
    public void setChildren(List<ClsfNodeDTO> children) {
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
     * @param nodeId
     *            the new node id
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
     * @param createdAt
     *            the new created at
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
     * @param updatedAt
     *            the new updated at
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
     * @param createdBy
     *            the new created by
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
     * @param updatedBy
     *            the new updated by
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
        return this.childCount;
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


}

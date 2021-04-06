package com.unidata.mdm.backend.api.rest.dto.clsf;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class ClsfNodeRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClsfNodeRO {

	/** The is own node attrs. */
	private boolean isOwnNodeAttrs;

	/** The children. */
	private List<ClsfNodeRO> children;

	/** The child count. */
	private int childCount = 0;

	/** The name. */
	private String name;

	/** The description. */
	private String description;

	/** The code. */
	private String code;
	
	/** The has data. */
	private boolean hasData;

	/** The id. */
	@JsonProperty(required = false)
	private String id;

	/** The parent id. */
	@JsonProperty(required = false)
	private String parentId;

	/** The classifier name. */
	@JsonProperty(required = false)
	private String classifierName;

	/** The node attrs. */
	private List<ClsfNodeAttrRO> nodeAttrs;

	/** The inherited node attrs. */
	private List<ClsfNodeAttrRO> inheritedNodeAttrs;

	/**
	 * Checks if is own node attrs.
	 *
	 * @return true, if is own node attrs
	 */
	public boolean isOwnNodeAttrs() {
		return isOwnNodeAttrs;
	}

	/**
	 * Sets the own node attrs.
	 *
	 * @param isOwnNodeAttrs
	 *            the new own node attrs
	 */
	public void setOwnNodeAttrs(boolean isOwnNodeAttrs) {
		this.isOwnNodeAttrs = isOwnNodeAttrs;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<ClsfNodeRO> getChildren() {
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
	 * @param childCount
	 *            the new child count
	 */
	public void setChildCount(int childCount) {
		this.childCount = childCount;
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
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
	 * Gets the classifier name.
	 *
	 * @return the classifier name
	 */
	public String getClassifierName() {
		return classifierName;
	}

	/**
	 * Sets the classifier name.
	 *
	 * @param classifierName
	 *            the new classifier name
	 */
	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}

	/**
	 * Gets the node attrs.
	 *
	 * @return the node attrs
	 */
	public List<ClsfNodeAttrRO> getNodeAttrs() {
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
	public void setNodeAttrs(List<ClsfNodeAttrRO> nodeAttrs) {
		this.nodeAttrs = nodeAttrs;
	}

	/**
	 * Gets the inherited node attrs.
	 *
	 * @return the inherited node attrs
	 */
	public List<ClsfNodeAttrRO> getInheritedNodeAttrs() {
		if (this.inheritedNodeAttrs == null) {
			this.inheritedNodeAttrs = new ArrayList<>();
		}
		return inheritedNodeAttrs;
	}

	/**
	 * Sets the inherited node attrs.
	 *
	 * @param inheritedNodeAttrs
	 *            the new inherited node attrs
	 */
	public void setInheritedNodeAttrs(List<ClsfNodeAttrRO> inheritedNodeAttrs) {
		this.inheritedNodeAttrs = inheritedNodeAttrs;
	}

	/**
	 * Checks if is checks for data.
	 *
	 * @return true, if is checks for data
	 */
	public boolean isHasData() {
		return hasData;
	}

	/**
	 * Sets the checks for data.
	 *
	 * @param hasData the new checks for data
	 */
	public void setHasData(boolean hasData) {
		this.hasData = hasData;
	}

}

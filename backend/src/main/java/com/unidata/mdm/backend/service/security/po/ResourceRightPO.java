package com.unidata.mdm.backend.service.security.po;

import java.io.Serializable;

/**
 * The persistent class for the s_right_s_resource database table.
 * 
 * @author ilya.bykov
 */
public class ResourceRightPO extends BaseSecurityPO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	private Integer id;

	// bi-directional many-to-one association to SResource
	/** The S resource. */
	private ResourcePO resource;

	// bi-directional many-to-one association to SRight
	/** The S right. */
	private RightPO right;

	// bi-directional many-to-one association to SRole
	/** The S role. */
	private RolePO role;

	/**
	 * Instantiates a new resource right po.
	 */
	public ResourceRightPO() {
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the s resource.
	 *
	 * @return the sResource
	 */
	public ResourcePO getResource() {
		return this.resource;
	}

	/**
	 * Gets the s right.
	 *
	 * @return the sRight
	 */
	public RightPO getRight() {
		return this.right;
	}

	/**
	 * Gets the s role.
	 *
	 * @return the sRole
	 */
	public RolePO getRole() {
		return this.role;
	}

	/**
	 * Sets the s resource.
	 *
	 * @param resource
	 *            the sResource to set
	 */
	public void setResource(ResourcePO resource) {
		this.resource = resource;
	}

	/**
	 * Sets the s right.
	 *
	 * @param right
	 *            the sRight to set
	 */
	public void setRight(RightPO right) {
		this.right = right;
	}

	/**
	 * Sets the s role.
	 *
	 * @param role
	 *            the sRole to set
	 */
	public void setRole(RolePO role) {
		this.role = role;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceRightPO other = (ResourceRightPO) obj;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}

}
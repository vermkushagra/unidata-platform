package com.unidata.mdm.backend.common.dto.security;

import java.io.Serializable;

import com.unidata.mdm.backend.common.integration.auth.SecurityLabelAttribute;


/**
 * Data transfer object for the security label attribute.
 * @author ilya.bykov
 *
 */
public class SecurityLabelAttributeDTO extends BaseSecurityDTO implements SecurityLabelAttribute, Serializable {
	/**
     * SVUID.
     */
    private static final long serialVersionUID = 2260998227384785946L;
    /** The id. */
	private int id;
	/**
	 * Name.
	 */
    private String name;
    /**
     * Path to the attribute.
     */
    private String path;
    /**
     * Allowed value.
     */
    private String value;
    /**
     * Attribute description.
     */
    private String description;

    public SecurityLabelAttributeDTO() {
    }

    public SecurityLabelAttributeDTO(int id, String name, String path, String value, String description) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.value = value;
        this.description = description;
    }

    /**
	 * Gets the name.
	 *
	 * @return the name
	 */
    @Override
    public String getName() {
        return name;
    }

    /**
	 * Sets the name.
	 *
	 * @param name
	 *            the name to set
	 */
    public void setName(String name) {
        this.name = name;
    }

    /**
	 * Gets the value.
	 *
	 * @return the value
	 */
    @Override
    public String getValue() {
        return value;
    }

    /**
	 * Sets the value.
	 *
	 * @param value
	 *            the value to set
	 */
    public void setValue(String value) {
        this.value = value;
    }

    /**
	 * Gets the path.
	 *
	 * @return the path
	 */
    @Override
    public String getPath() {
        return path;
    }

    /**
	 * Sets the path.
	 *
	 * @param path
	 *            the path to set
	 */
    public void setPath(String path) {
        this.path = path;
    }



	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Override
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
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Override
    public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
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
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        SecurityLabelAttributeDTO other = (SecurityLabelAttributeDTO) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}

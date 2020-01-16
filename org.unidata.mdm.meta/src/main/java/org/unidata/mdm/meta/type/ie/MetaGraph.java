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

package org.unidata.mdm.meta.type.ie;

import java.io.Serializable;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;

/**
 * The Class MetaGraph.
 */
public class MetaGraph extends DirectedPseudograph<MetaVertex, MetaEdge<MetaVertex>> implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    private String id;

    /** The file name. */
    private String fileName;
    
    /** The override. */
    private boolean override;

    /** The import users. */
    private boolean importUsers;

    /** The import roles. */
    private boolean importRoles;
    
    /** The security token. */
    private String securityToken;
    
 
    /** The redirected. */
    private boolean redirected;

    /**
     * Instantiates a new meta graph.
     *
     * @param ef
     *            the ef
     */
    public MetaGraph(EdgeFactory<MetaVertex, MetaEdge<MetaVertex>> ef) {
        super(ef);

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
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Checks if is override.
     *
     * @return true, if is override
     */
    public boolean isOverride() {
        return override;
    }

    /**
     * Sets the override.
     *
     * @param override the new override
     */
    public void setOverride(boolean override) {
        this.override = override;
    }

    /**
     * Checks if is import users.
     *
     * @return true, if is import users
     */
    public boolean isImportUsers() {
        return importUsers;
    }

    /**
     * Sets the import users.
     *
     * @param importUsers the new import users
     */
    public void setImportUsers(boolean importUsers) {
        this.importUsers = importUsers;
    }

    /**
     * Checks if is import roles.
     *
     * @return true, if is import roles
     */
    public boolean isImportRoles() {
        return importRoles;
    }

    /**
     * Sets the import roles.
     *
     * @param importRoles the new import roles
     */
    public void setImportRoles(boolean importRoles) {
        this.importRoles = importRoles;
    }

	/**
	 * Gets the security token.
	 *
	 * @return the security token
	 */
	public String getSecurityToken() {
		return securityToken;
	}

	/**
	 * Sets the security token.
	 *
	 * @param securityToken the new security token
	 */
	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	/**
	 * Checks if is redirected.
	 *
	 * @return true, if is redirected
	 */
	public boolean isRedirected() {
		return redirected;
	}

	/**
	 * Sets the redirected.
	 *
	 * @param redirected the new redirected
	 */
	public void setRedirected(boolean redirected) {
		this.redirected = redirected;
	}

}

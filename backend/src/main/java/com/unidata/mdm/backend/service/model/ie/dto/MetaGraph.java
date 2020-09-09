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

package com.unidata.mdm.backend.service.model.ie.dto;

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
    private boolean override;

    private boolean importUsers;

    private boolean importRoles;

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

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public boolean isImportUsers() {
        return importUsers;
    }

    public void setImportUsers(boolean importUsers) {
        this.importUsers = importUsers;
    }

    public boolean isImportRoles() {
        return importRoles;
    }

    public void setImportRoles(boolean importRoles) {
        this.importRoles = importRoles;
    }
}

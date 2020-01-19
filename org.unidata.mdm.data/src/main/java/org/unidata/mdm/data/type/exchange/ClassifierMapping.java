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

package org.unidata.mdm.data.type.exchange;

import java.io.Serializable;
import java.util.List;


/**
 * The Class ClassifierMapping.
 */
public class ClassifierMapping implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8695207624428893944L;

    /** The node id. */
    private ExchangeField nodeId;

    /** The fields. */
    private List<ExchangeField> fields;
    /**
     * The version range.
     */
    private VersionRange versionRange;
    /**
     * Gets the node id.
     *
     * @return the node id
     */
    public ExchangeField getNodeId() {
        return nodeId;
    }

    /**
     * Gets the fields.
     *
     * @return the fields
     */
    public List<ExchangeField> getFields() {
        return fields;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId
     *            the new node id
     */
    public void setNodeId(ExchangeField nodeId) {
        this.nodeId = nodeId;
    }
    /**
     * @return the versionRange
     */
    public VersionRange getVersionRange() {
        return versionRange;
    }

    /**
     * @param versionRange the versionRange to set
     */
    public void setVersionRange(VersionRange versionRange) {
        this.versionRange = versionRange;
    }

    /**
     * Sets the node id.
     *
     * @param nodeId
     *            the new node id
     */
    public ClassifierMapping withNodeId(ExchangeField nodeId) {
        this.nodeId = nodeId;
        return this;
    }
    /**
     * Sets the node id.
     *
     * @param nodeId
     *            the new node id
     */
    public ClassifierMapping withVersionRange(VersionRange range) {
        setVersionRange(range);
        return this;
    }
    /**
     * Sets the fields.
     *
     * @param fields
     *            the new fields
     */
    public void setFields(List<ExchangeField> fields) {
        this.fields = fields;
    }
    /**
     * Sets the fields.
     *
     * @param fields
     *            the new fields
     */
    public ClassifierMapping withFields(List<ExchangeField> fields) {
        this.fields = fields;
        return this;
    }
}

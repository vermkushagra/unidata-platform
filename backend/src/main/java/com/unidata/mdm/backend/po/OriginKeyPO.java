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

/**
 *
 */
package com.unidata.mdm.backend.po;

/**
 * @author Mikhail Mikhailov
 * A transient origin key object.
 */
public class OriginKeyPO {

    /**
     * ID. Not used in hash code and equals computations.
     */
    private String id;
    /**
     * Source system.
     */
    private String sourceSystem;
    /**
     * External id.
     */
    private String externalId;
    /**
     * Constructor.
     */
    public OriginKeyPO() {
        super();
    }
    /**
     * Initializing constructor.
     */
    public OriginKeyPO(String id, String sourceSystem, String externalId) {
        super();
        this.id = id;
        this.sourceSystem = sourceSystem;
        this.externalId = externalId;
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }
    /**
     * @param sourceSystem the sourceSystem to set
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
    /**
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }
    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append("sourceSystem: ")
                .append(sourceSystem)
                .append(", externalId: ")
                .append(externalId)
                .append(", id: ")
                .append(id)
                .toString();
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
        result = prime * result + ((sourceSystem == null) ? 0 : sourceSystem.hashCode());
        return result;
    }
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OriginKeyPO other = (OriginKeyPO) obj;
        if (externalId == null) {
            if (other.externalId != null) {
                return false;
            }
        } else if (!externalId.equals(other.externalId)) {
            return false;
        }
        if (sourceSystem == null) {
            if (other.sourceSystem != null) {
                return false;
            }
        } else if (!sourceSystem.equals(other.sourceSystem)) {
            return false;
        }
        return true;
    }

}

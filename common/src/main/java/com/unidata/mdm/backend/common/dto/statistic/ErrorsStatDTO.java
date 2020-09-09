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

package com.unidata.mdm.backend.common.dto.statistic;

import java.util.Map;

import com.unidata.mdm.backend.common.types.SeverityType;

/**
 * The Class ErrorsStat.
 */
public class ErrorsStatDTO {

    /** The total. */
    private int total;

    /** The entity name. */
    private String entityName;

    /** The source system name. */
    private String sourceSystemName;

    /** The data. */
    private Map<SeverityType, Integer> data;

    /**
     * Gets the total.
     *
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the total.
     *
     * @param total
     *            the new total
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Gets the entity name.
     *
     * @return the entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Sets the entity name.
     *
     * @param entityName
     *            the new entity name
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    public Map<SeverityType, Integer> getData() {
        return data;
    }

    /**
     * Sets the data.
     *
     * @param data
     *            the data
     */
    public void setData(Map<SeverityType, Integer> data) {
        this.data = data;
    }

    /**
     * Gets the source system name.
     *
     * @return the source system name
     */
    public String getSourceSystemName() {
        return sourceSystemName;
    }

    /**
     * Sets the source system name.
     *
     * @param sourceSystemName
     *            the new source system name
     */
    public void setSourceSystemName(String sourceSystemName) {
        this.sourceSystemName = sourceSystemName;
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
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((entityName == null) ? 0 : entityName.hashCode());
        result = prime * result + ((sourceSystemName == null) ? 0 : sourceSystemName.hashCode());
        result = prime * result + total;
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
        ErrorsStatDTO other = (ErrorsStatDTO) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (entityName == null) {
            if (other.entityName != null)
                return false;
        } else if (!entityName.equals(other.entityName))
            return false;
        if (sourceSystemName == null) {
            if (other.sourceSystemName != null)
                return false;
        } else if (!sourceSystemName.equals(other.sourceSystemName))
            return false;
        if (total != other.total)
            return false;
        return true;
    }
}

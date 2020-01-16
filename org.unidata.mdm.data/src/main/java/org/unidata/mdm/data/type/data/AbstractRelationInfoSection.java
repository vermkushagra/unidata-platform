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

package org.unidata.mdm.data.type.data;

import org.unidata.mdm.core.type.data.InfoSection;

/**
 * @author Mikhail Mikhailov
 * Common part for all relation info sections.
 */
public abstract class AbstractRelationInfoSection extends InfoSection {
    /**
     * The entity name.
     */
    protected String relationName;
    /**
     * Relation type.
     */
    protected RelationType type;
    /**
     * From entity name.
     */
    protected String fromEntityName;
    /**
     * To entity name.
     */
    protected String toEntityName;
    /**
     * Gets the entity name.
     * @return name
     */
    public String getRelationName() {
        return relationName;
    }
    /**
     * Sets entity name field.
     * @param relationName value to set
     */
    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
    /**
     * @return the type
     */
    public RelationType getRelationType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setRelationType(RelationType type) {
        this.type = type;
    }
    /**
     * @return the fromEntityName
     */
    public String getFromEntityName() {
        return fromEntityName;
    }
    /**
     * @param fromEntityName the fromEntityName to set
     */
    public void setFromEntityName(String fromEntityName) {
        this.fromEntityName = fromEntityName;
    }
    /**
     * @return the toEntityName
     */
    public String getToEntityName() {
        return toEntityName;
    }
    /**
     * @param toEntityName the toEntityName to set
     */
    public void setToEntityName(String toEntityName) {
        this.toEntityName = toEntityName;
    }
}

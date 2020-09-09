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

package com.unidata.mdm.backend.service.classifier.cache;

/**
 * @author Mikhail Mikhailov
 * Linkable attribute properties.
 */
public abstract class CachedClassifierNodeLinkableAttribute extends CachedClassifierNodeAttribute {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -7547488477973939273L;
    /**
     * Enum data type.
     */
    private String linkName;
    /**
     * State enum link.
     */
    public static final int ATTR_STATE_ENUM = 1 << 6;
    /**
     * State lookup link.
     */
    public static final int ATTR_STATE_LOOKUP = 1 << 7;
    /**
     * Constructor.
     */
    public CachedClassifierNodeLinkableAttribute() {
        super();
    }
    /**
     * @return the enumDataType
     */
    public String getEnumName() {
        return isEnumLink() ? linkName : null;
    }
    /**
     * @param enumName the enumDataType to set
     */
    public void setEnumName(String enumName) {
        this.linkName = enumName;
    }
    /**
     * @return the lookup name or null, if this is not a lookup link
     */
    public String getLookupName() {
        return isLookupLink() ? linkName : null;
    }
    /**
     * @param lookupName the enumDataType to set
     */
    public void setLookupName(String lookupName) {
        this.linkName = lookupName;
    }
    /**
     * @return the enum link
     */
    public boolean isEnumLink() {
        return (flags & ATTR_STATE_ENUM) != 0;
    }
    /**
     * @param enumLink the enum link to set
     */
    public void setEnumLink(boolean enumLink) {
        if (enumLink) {
            flags |= ATTR_STATE_ENUM;
        } else {
            flags &= ~(ATTR_STATE_ENUM);
        }
    }
    /**
     * @return the lookup link
     */
    public boolean isLookupLink() {
        return (flags & ATTR_STATE_LOOKUP) != 0;
    }
    /**
     * @param lookupLink the enum link to set
     */
    public void setLookupLink(boolean lookupLink) {
        if (lookupLink) {
            flags |= ATTR_STATE_LOOKUP;
        } else {
            flags &= ~(ATTR_STATE_LOOKUP);
        }
    }
}

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

import java.io.Serializable;

/**
 * @author Mikhail Mikhailov
 * Classifier node attribute with no superfluous information.
 */
public abstract class CachedClassifierNodeAttribute implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -3352420226349558115L;
    /**
     * State read only.
     */
    public static final int ATTR_STATE_READ_ONLY = 1 << 0;
    /**
     * State hidden.
     */
    public static final int ATTR_STATE_HIDDEN = 1 << 1;
    /**
     * State nullable.
     */
    public static final int ATTR_STATE_NULLABLE = 1 << 2;
    /**
     * State inherited.
     */
    public static final int ATTR_STATE_INHERITED = 1 << 3;
    /**
     * State unique.
     */
    public static final int ATTR_STATE_UNIQUE = 1 << 4;
    /**
     * State searchable.
     */
    public static final int ATTR_STATE_SEARCHABLE = 1 << 5;
    /**
     * The attr name.
     */
    private String name;
    /**
     * The display name.
     */
    private String displayName;
    /**
     * The description.
     */
    private String description;
    /**
     * Visual ordering.
     */
    private int order;
    /**
     * CPs.
     */
    private CachedClassifierCustomProperty[] customProperties;
    /**
     * Flags bit set.
     */
    protected int flags = 0;
    /**
     * Disabled constructor.
     */
    public CachedClassifierNodeAttribute() {
        super();
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return (flags & ATTR_STATE_READ_ONLY) != 0;
    }
    /**
     * @param readOnly the readOnly to set
     */
    public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            flags |= ATTR_STATE_READ_ONLY;
        } else {
            flags &= ~(ATTR_STATE_READ_ONLY);
        }
    }
    /**
     * @return the hidden
     */
    public boolean isHidden() {
        return (flags & ATTR_STATE_HIDDEN) != 0;
    }
    /**
     * @param hidden the hidden to set
     */
    public void setHidden(boolean hidden) {
        if (hidden) {
            flags |= ATTR_STATE_HIDDEN;
        } else {
            flags &= ~(ATTR_STATE_HIDDEN);
        }
    }
    /**
     * @return the nullable
     */
    public boolean isNullable() {
        return (flags & ATTR_STATE_NULLABLE) != 0;
    }
    /**
     * @param nullable the nullable to set
     */
    public void setNullable(boolean nullable) {
        if (nullable) {
            flags |= ATTR_STATE_NULLABLE;
        } else {
            flags &= ~(ATTR_STATE_NULLABLE);
        }
    }
    /**
     * @return the inherited
     */
    public boolean isInherited() {
        return (flags & ATTR_STATE_INHERITED) != 0;
    }
    /**
     * @param inherited the inherited to set
     */
    public void setInherited(boolean inherited) {
        if (inherited) {
            flags |= ATTR_STATE_INHERITED;
        } else {
            flags &= ~(ATTR_STATE_INHERITED);
        }
    }
    /**
     * @return the unique
     */
    public boolean isUnique() {
        return (flags & ATTR_STATE_UNIQUE) != 0;
    }
    /**
     * @param unique the unique to set
     */
    public void setUnique(boolean unique) {
        if (unique) {
            flags |= ATTR_STATE_UNIQUE;
        } else {
            flags &= ~(ATTR_STATE_UNIQUE);
        }
    }
    /**
     * @return the searchable
     */
    public boolean isSearchable() {
        return (flags & ATTR_STATE_SEARCHABLE) != 0;
    }
    /**
     * @param searchable the searchable to set
     */
    public void setSearchable(boolean searchable) {
        if (searchable) {
            flags |= ATTR_STATE_SEARCHABLE;
        } else {
            flags &= ~(ATTR_STATE_SEARCHABLE);
        }
    }
    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }
    /**
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }
    /**
     * @return the customProperties
     */
    public CachedClassifierCustomProperty[] getCustomProperties() {
        return customProperties;
    }
    /**
     * @param customProperties the customProperties to set
     */
    public void setCustomProperties(CachedClassifierCustomProperty[] customProperties) {
        this.customProperties = customProperties;
    }
    /**
     * Simple or not.
     * @return true, if simple, false otherwise
     */
    public boolean isSimple() {
        return false;
    }
    /**
     * Array or not.
     * @return true, if array, false otherwise
     */
    public boolean isArray() {
        return false;
    }
}
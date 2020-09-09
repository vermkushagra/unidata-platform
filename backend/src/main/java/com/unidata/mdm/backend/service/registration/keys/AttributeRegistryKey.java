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

package com.unidata.mdm.backend.service.registration.keys;

import javax.annotation.Nonnull;

/**
 * Attribute registry key
 */
public class AttributeRegistryKey implements UniqueRegistryKey {

    /**
     * Attribute name
     */
    @Nonnull
    private final String fullAttributeName;

    /**
     * Entity name
     */
    @Nonnull
    private final String entityName;

    /**
     * @param fullAttributeName - full attribute name
     * @param entityName        - entity name
     */
    public AttributeRegistryKey(@Nonnull String fullAttributeName, @Nonnull String entityName) {
        this.entityName = entityName;
        this.fullAttributeName = fullAttributeName;
    }

    @Override
    public Type keyType() {
        return Type.ATTRIBUTE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeRegistryKey)) return false;

        AttributeRegistryKey that = (AttributeRegistryKey) o;

        if (!fullAttributeName.equals(that.fullAttributeName)) return false;
        return entityName.equals(that.entityName);

    }

    @Override
    public int hashCode() {
        int result = fullAttributeName.hashCode();
        result = 31 * result + entityName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "fullAttributeName='" + fullAttributeName + '\'' +
                ", entityName='" + entityName + '\'' +
                '}';
    }

    @Nonnull
    public String getFullAttributeName() {
        return fullAttributeName;
    }

    @Nonnull
    public String getEntityName() {
        return entityName;
    }
}

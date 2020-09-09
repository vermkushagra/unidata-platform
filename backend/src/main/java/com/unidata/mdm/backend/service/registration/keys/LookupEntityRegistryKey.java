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
import java.io.Serializable;

/**
 * Unique key of lookup entity help identifying lookup entity
 */
public class LookupEntityRegistryKey implements UniqueRegistryKey {

    /**
     * lookup entity name
     */
    @Nonnull
    private final String entityName;

    /**
     * constructor
     *
     * @param entityName - lookup entity name
     */
    public LookupEntityRegistryKey(@Nonnull String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return entity name
     */
    @Nonnull
    public String getEntityName() {
        return entityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LookupEntityRegistryKey)) return false;

        LookupEntityRegistryKey that = (LookupEntityRegistryKey) o;

        return entityName.equals(that.entityName);

    }

    @Override
    public int hashCode() {
        return entityName.hashCode();
    }

    /**
     * @return type of key
     */
    @Override
    public Type keyType() {
        return Type.LOOKUP_ENTITY;
    }

    @Override
    public String toString() {
        return "{" +
                "entityName='" + entityName + '\'' +
                '}';
    }
}

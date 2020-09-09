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
 * Unique key of relation help identifying relation
 */
public class RelationRegistryKey implements UniqueRegistryKey {

    @Nonnull
    private final String relName;

    public RelationRegistryKey(@Nonnull String relName) {
        this.relName = relName;
    }

    @Nonnull
    public String getRelName() {
        return relName;
    }

    @Override
    public Type keyType() {
        return Type.RELATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RelationRegistryKey that = (RelationRegistryKey) o;

        return relName.equals(that.relName);

    }

    @Override
    public int hashCode() {
        return relName.hashCode();
    }

    @Override
    public String toString() {
        return "{" +
                "relName='" + relName + '\'' +
                '}';
    }
}

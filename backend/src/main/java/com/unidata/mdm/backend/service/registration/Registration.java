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

package com.unidata.mdm.backend.service.registration;

import javax.annotation.Nonnull;
import java.util.Set;

import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

public class Registration {

    @Nonnull
    private final UniqueRegistryKey key;

    @Nonnull
    private final Set<UniqueRegistryKey> references;

    @Nonnull
    private final Set<UniqueRegistryKey> contains;


    public Registration(@Nonnull UniqueRegistryKey key, @Nonnull Set<UniqueRegistryKey> references, @Nonnull Set<UniqueRegistryKey> contains) {
        this.key = key;
        this.references = references;
        this.contains = contains;
    }

    @Nonnull
    public UniqueRegistryKey getKey() {
        return key;
    }

    @Nonnull
    public Set<UniqueRegistryKey> getReferences() {
        return references;
    }

    @Nonnull
    public Set<UniqueRegistryKey> getContains() {
        return contains;
    }
}

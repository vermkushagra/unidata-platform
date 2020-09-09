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

package com.unidata.mdm.backend.service.registration.handlers;


import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

/**
 * Responsible for executing delete logic depend on keys
 *
 * @param <D> - key of deleting entity
 * @param <L> - key of linking entity
 */
public interface DeleteHandler<D extends UniqueRegistryKey, L extends UniqueRegistryKey> {

    /**
     * @param removingKey - removing key
     * @param linkingKey  - linking key
     */
    void onDelete(D removingKey, L linkingKey);

    /**
     * @return removed type of key(linked with D and can be got from L instance)
     */
    UniqueRegistryKey.Type getRemovedEntityType();

    /**
     * @return linked type of key (linked with L and can be got from L instance)
     */
    UniqueRegistryKey.Type getLinkedEntityType();

}

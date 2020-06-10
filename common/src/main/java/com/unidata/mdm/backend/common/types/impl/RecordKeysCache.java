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

package com.unidata.mdm.backend.common.types.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Keys cache for long running mass upsert operations.
 */
public class RecordKeysCache {

    /**
     * External ID keys.
     */
    private final Map<String, RecordKeys> externalIdKeys = new HashMap<>();
    /**
     * Origin ID keys.
     */
    private final Map<String, RecordKeys> originIdKeys = new HashMap<>();
    /**
     * Etalon ID keys.
     */
    private final Map<String, RecordKeys> etalonIdKeys = new HashMap<>();
    /**
     * The keys.
     */
    private final List<RecordKeys> keys = new ArrayList<>();
    /**
     * Lock for synchronized access.
     */
    private final Lock lock = new ReentrantLock(true);

    /**
     * Constructor.
     */
    public RecordKeysCache() {
        super();
    }


    /**
     * @return the keys
     */
    public List<RecordKeys> getKeys() {
        return keys;
    }

    /**
     * @return the externalIdKeys
     */
    public RecordKeys getExternalIdKeys(String externalId, String sourceSystem, String entityName) {
        return externalIdKeys.get(String.join("_", externalId, sourceSystem, entityName));
    }

    /**
     * @return the originIdKeys
     */
    public RecordKeys getOriginIdKeys(String originId) {
        return originIdKeys.get(originId);
    }

    /**
     * @return the etalonIdKeys
     */
    public RecordKeys getEtalonIdKeys(String etalonId) {
        return etalonIdKeys.get(etalonId);
    }

    /**
     * Add key.
     * @param key
     */
    public void add(RecordKeys key) {

        lock.lock();
        try {
            if (etalonIdKeys.containsKey(key.getEtalonKey().getId())) {
                return;
            }

            keys.add(key);
            externalIdKeys.put(
                    String.join("_",
                            key.getOriginKey().getExternalId(),
                            key.getOriginKey().getSourceSystem(),
                            key.getOriginKey().getEntityName()),
                    key);
            originIdKeys.put(key.getOriginKey().getId(), key);
            etalonIdKeys.put(key.getEtalonKey().getId(), key);
        } finally {
            lock.unlock();
        }
    }
}

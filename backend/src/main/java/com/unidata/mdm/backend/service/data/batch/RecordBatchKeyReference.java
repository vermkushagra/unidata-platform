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

package com.unidata.mdm.backend.service.data.batch;

import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Simple wrapper for the keys + current revision number for the ext. id being imported.
 * This schema implies, that same keys are always in the same partition block for historical/multiversion records.
 */
public class RecordBatchKeyReference implements BatchKeyReference<RecordKeys> {
    /**
     * Current revision of this OKey.
     */
    private int revision;
    /**
     * Keys.
     */
    private RecordKeys keys;
    /**
     * Constructor.
     * @param keys
     * @param revision
     */
    public RecordBatchKeyReference(RecordKeys keys) {
        super();
        this.keys = keys;
        this.revision = keys.getOriginKey() != null ? keys.getOriginKey().getRevision() : 0;
    }
    /**
     * @return the revision
     */
    @Override
    public int getRevision() {
        return revision;
    }
    /**
     * @param revision the revision to set
     */
    @Override
    public void setRevision(int revision) {
        if (revision > this.revision) {
            keys = RecordKeys.builder(keys)
                    .originKey(OriginKey
                            .builder(keys.getOriginKey())
                            .revision(revision)
                            .build())
                    .build();
        }

        this.revision = revision;
    }
    /**
     * @return the keys
     */
    @Override
    public RecordKeys getKeys() {
        return keys;
    }
    /**
     * @param keys the keys to set
     */
    public void setKeys(RecordKeys keys) {
        this.keys = keys;
    }
}

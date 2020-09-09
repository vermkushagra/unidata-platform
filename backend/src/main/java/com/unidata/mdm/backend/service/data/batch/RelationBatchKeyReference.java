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

import com.unidata.mdm.backend.common.keys.RelationKeys;

/**
 * @author Mikhail Mikhailov
 * Simple wrapper for the keys + current revision number for the ext. id being imported.
 * This schema implies, that same keys are always in the same partition block for historical/multiversion records.
 */
public class RelationBatchKeyReference implements BatchKeyReference<RelationKeys> {
    /**
     * Current revision of this OKey.
     */
    private int revision;
    /**
     * Keys.
     */
    private final RelationKeys keys;
    /**
     * Constructor.
     * @param keys
     * @param revision
     */
    public RelationBatchKeyReference(RelationKeys keys) {
        super();
        this.keys = keys;
        this.revision = keys.getOriginId() != null ? keys.getOriginRevision() : 0;
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
        this.revision = revision;
    }
    /**
     * @return the keys
     */
    @Override
    public RelationKeys getKeys() {
        return keys;
    }
}

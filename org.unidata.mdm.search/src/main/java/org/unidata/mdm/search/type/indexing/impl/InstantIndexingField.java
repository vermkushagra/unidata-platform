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

package org.unidata.mdm.search.type.indexing.impl;

import java.time.Instant;
import java.time.ZoneId;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 15, 2019
 */
public class InstantIndexingField extends AbstractValueIndexingField<Instant, InstantIndexingField> {
    /**
     * Specified zone id or system default.
     */
    private ZoneId zoneId;

    /**
     * Constructor.
     * @param name
     */
    public InstantIndexingField(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.INSTANT;
    }

    /**
     * @return the zoneId
     */
    public ZoneId getZoneId() {
        return zoneId == null ? ZoneId.systemDefault() : zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }
    /**
     * Sets zone id.
     * @param id the zone id
     * @return self
     */
    public InstantIndexingField withZoneId(ZoneId id) {
        setZoneId(id);
        return self();
    }
}

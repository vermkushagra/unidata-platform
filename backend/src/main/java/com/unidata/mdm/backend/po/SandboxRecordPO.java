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

package com.unidata.mdm.backend.po;

import java.util.Arrays;

public class SandboxRecordPO {

    private final Long id;

    private final String entityName;

    private final byte[] data;

    public SandboxRecordPO(final Long id, final String entityName, final byte[] data) {
        this.id = id;
        this.entityName = entityName;
        this.data = Arrays.copyOf(data, data.length);
    }

    public Long getId() {
        return id;
    }

    public String getEntityName() {
        return entityName;
    }

    public byte[] getData() {
        return data;
    }
}

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

package com.unidata.mdm.backend.api.rest.dto.meta;

import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

public class ReferenceInfo {

    private String sourceType;

    private String targetType;

    private UniqueRegistryKey sourceKey;

    private UniqueRegistryKey targetKey;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public UniqueRegistryKey getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(UniqueRegistryKey sourceKey) {
        this.sourceKey = sourceKey;
    }

    public UniqueRegistryKey getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(UniqueRegistryKey targetKey) {
        this.targetKey = targetKey;
    }
}

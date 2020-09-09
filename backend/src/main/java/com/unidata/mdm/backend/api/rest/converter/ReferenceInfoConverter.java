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

package com.unidata.mdm.backend.api.rest.converter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.api.rest.dto.meta.ReferenceInfo;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

public class ReferenceInfoConverter {

    @Nonnull
    public static List<ReferenceInfo> toRefInfos(@Nonnull Collection<UniqueRegistryKey> sources,
            @Nonnull UniqueRegistryKey target) {
        return sources.stream()
                      .filter(Objects::nonNull)
                      .map(source -> convertToRefInfo(source, target))
                      .collect(Collectors.toList());
    }

    @Nonnull
    public static List<ReferenceInfo> toRefInfos(@Nonnull UniqueRegistryKey source,
            @Nonnull Collection<UniqueRegistryKey> targets) {
        return targets.stream()
                      .filter(Objects::nonNull)
                      .map(target -> convertToRefInfo(source, target))
                      .collect(Collectors.toList());
    }

    @Nonnull
    public static ReferenceInfo convertToRefInfo(@Nonnull UniqueRegistryKey source, @Nonnull UniqueRegistryKey target) {
        ReferenceInfo result = new ReferenceInfo();
        result.setSourceType(source.keyType().name());
        result.setTargetType(target.keyType().name());
        result.setSourceKey(source);
        result.setTargetKey(target);
        return result;
    }
}

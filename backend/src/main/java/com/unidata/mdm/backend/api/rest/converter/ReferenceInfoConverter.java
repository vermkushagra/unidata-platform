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

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

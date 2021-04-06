package com.unidata.mdm.backend.service.registration;

import javax.annotation.Nonnull;
import java.util.Set;

import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

public class Registration {

    @Nonnull
    private final UniqueRegistryKey key;

    @Nonnull
    private final Set<UniqueRegistryKey> references;

    @Nonnull
    private final Set<UniqueRegistryKey> contains;


    public Registration(@Nonnull UniqueRegistryKey key, @Nonnull Set<UniqueRegistryKey> references, @Nonnull Set<UniqueRegistryKey> contains) {
        this.key = key;
        this.references = references;
        this.contains = contains;
    }

    @Nonnull
    public UniqueRegistryKey getKey() {
        return key;
    }

    @Nonnull
    public Set<UniqueRegistryKey> getReferences() {
        return references;
    }

    @Nonnull
    public Set<UniqueRegistryKey> getContains() {
        return contains;
    }
}

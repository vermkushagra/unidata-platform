package com.unidata.mdm.backend.service.registration;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

/**
 * Common service for handling relations between elements of system and resolve problem with consistency in system.
 */
public interface RegistrationService {

    /**
     * @param key        - key
     * @param references - linked keys
     * @param contains   - contains keys
     */
    //todo input object and batch upsert
    void registry(@Nonnull UniqueRegistryKey key, @Nonnull Set<UniqueRegistryKey> references, @Nonnull Set<UniqueRegistryKey> contains);

    /**
     *
     * @param registrations collection of
     */
    void batchRegistry(@Nonnull Collection<Registration> registrations);

    /**
     * After call this method all associated with key entities will be modify depends on resolving strategy
     *
     * @param key -key
     */
    void remove(@Nonnull UniqueRegistryKey key);

    /**
     * @param keys - keys of removing elements
     */
    void batchRemove(@Nonnull Collection<UniqueRegistryKey> keys);

    /**
     * @param key - unique key
     * @return revert links to key
     */
    @Nonnull
    Set<UniqueRegistryKey> getReferencesTo(@Nonnull UniqueRegistryKey key);

    /**
     * @param key - unique key
     * @return straight links from key
     */
    @Nonnull
    Set<UniqueRegistryKey> getReferencesFrom(UniqueRegistryKey key);

    /**
     *
     * @param key - unique key
     * @return revert contains
     */
    @Nonnull
    Set<UniqueRegistryKey> getContains(@Nonnull UniqueRegistryKey key);

    /**
     *
     * @param key - unique key
     * @return true if presented
     */
    boolean isKeyPresented(@Nonnull UniqueRegistryKey key);

    /**
     *
     * @param keys - keys
     * @return true, if all keys presented
     */
    boolean isAllKeysPresented(@Nonnull Collection<UniqueRegistryKey> keys);
    /**
     * Cleanup all.
     */
    void cleanup();
}

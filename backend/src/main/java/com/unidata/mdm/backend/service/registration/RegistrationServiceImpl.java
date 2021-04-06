package com.unidata.mdm.backend.service.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.unidata.mdm.backend.service.registration.handlers.DeleteHandler;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

/**
 * It is a simple implementation, there can be concurrency problems
 * Note^ best solution for this service is standalone graph DB.
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final static String REG = "$REG";

    //fill on start up!
    private final Table<UniqueRegistryKey.Type, UniqueRegistryKey.Type, DeleteHandler> deleteHandlerTable = HashBasedTable
            .create(UniqueRegistryKey.Type.values().length, UniqueRegistryKey.Type.values().length);

    /**
     * Contains map describe situation when , one entity(attribute, entity, matching rule anf so on) contains others
     */
    private IMap<UniqueRegistryKey, Set<UniqueRegistryKey>> contains;

    /**
     * Direct links map, describe situation when one entity(attribute, entity, matching rule anf so on) has reference to others
     */
    private IMap<UniqueRegistryKey, Set<UniqueRegistryKey>> directLinks;

    /**
     * Revert links map it is the same map as direct links but reverted.
     */
    private IMap<UniqueRegistryKey, Set<UniqueRegistryKey>> revertLinks;

    /**
     * In-memory data grid instance.
     */
    private HazelcastInstance hazelcastInstance;

    @Override
    public void registry(@Nonnull UniqueRegistryKey key, @Nonnull Set<UniqueRegistryKey> references,
            @Nonnull Set<UniqueRegistryKey> contains) {
        ILock lock = hazelcastInstance.getLock(REG);
        Registration registration = new Registration(key, references, contains);
        try {
            lock.lock();
            //can throw exception
            Collection<UniqueRegistryKey> removedContains = tryRemoveOldContains(registration);
            removedContains.forEach(this::cleanReferenceGraph);
            //there can be registration validation(links consistency)!
            createGraph(registration);
        } finally {
            lock.unlock();
        }
    }

    private void createGraph(Registration registration) {
        //put contains
        this.contains.put(registration.getKey(), registration.getContains());
        //put references and update revert references
        Collection<UniqueRegistryKey> removedReferences = putReferences(registration);
        //clean old state
        removedReferences.forEach(ref -> cleanRevertLinks(registration.getKey(), ref));
    }

    // can throw exception
    //return removed contains
    private Collection<UniqueRegistryKey> tryRemoveOldContains(Registration registration) {
        Collection<UniqueRegistryKey> prevContains = this.contains.get(registration.getKey());
        prevContains = prevContains == null ? Collections.emptyList() : new ArrayList<>(prevContains);
        prevContains.removeAll(registration.getContains());
        prevContains.forEach(this::tryRemove);
        return prevContains;
    }

    // return removed references
    private Collection<UniqueRegistryKey> putReferences(Registration registration) {
        //there can be registration validation(links consistency)!
        Collection<UniqueRegistryKey> prevDirectLinks = directLinks.put(registration.getKey(),
                registration.getReferences());
        prevDirectLinks = prevDirectLinks == null ? Collections.emptyList() : new ArrayList<>(prevDirectLinks);
        for (UniqueRegistryKey revertLink : registration.getReferences()) {
            boolean alreadyPresent = prevDirectLinks.remove(revertLink);
            if (alreadyPresent) {
                continue;
            }
            Set<UniqueRegistryKey> links = revertLinks.containsKey(revertLink) ?
                    revertLinks.get(revertLink) :
                    new HashSet<>();
            links.add(registration.getKey());
            revertLinks.put(revertLink, links);
        }
        return prevDirectLinks;
    }

    @Override
    public void batchRegistry(@Nonnull Collection<Registration> registrations) {
        ILock lock = hazelcastInstance.getLock(REG);
        lock.lock();
        try {
            Collection<UniqueRegistryKey> removedContains = new ArrayList<>();
            for (Registration registration : registrations) {
                removedContains.addAll(tryRemoveOldContains(registration));
            }
            removedContains.forEach(this::cleanReferenceGraph);
            registrations.forEach(this::createGraph);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(@Nonnull UniqueRegistryKey key) {
        ILock lock = hazelcastInstance.getLock(REG);
        try {
            lock.lock();
            tryRemove(key);
            cleanReferenceGraph(key);
        } finally {
            lock.unlock();
        }
    }

    private void tryRemove(@Nonnull UniqueRegistryKey key) {
        Collection<UniqueRegistryKey> references = revertLinks.get(key);
        references = references == null ? Collections.emptyList() : references;
        //todo made second type of delete handles!
        for (UniqueRegistryKey link : references) {
            DeleteHandler deleteHandler = deleteHandlerTable.get(key.keyType(), link.keyType());
            if (deleteHandler == null) {
                //it is a not strong reference
                continue;
            }
            //noinspection unchecked
            deleteHandler.onDelete(key, link);
        }
    }

    @Override
    public void batchRemove(@Nonnull Collection<UniqueRegistryKey> keys) {
        ILock lock = hazelcastInstance.getLock(REG);
        try {
            lock.lock();
            keys.forEach(this::tryRemove);
            keys.forEach(this::cleanReferenceGraph);
        } finally {
            lock.unlock();
        }
    }

    private void cleanReferenceGraph(@Nonnull UniqueRegistryKey registration) {
        //remove contains
        Collection<UniqueRegistryKey> contains = this.contains.remove(registration);
        contains = contains == null ? Collections.emptyList() : contains;
        contains.forEach(this::cleanReferenceGraph);
        //remove direct links
        Collection<UniqueRegistryKey> directReferences = this.directLinks.remove(registration);
        directReferences = directReferences == null ? Collections.emptyList() : directReferences;
        directReferences.forEach(key -> cleanRevertLinks(registration, key));
        //remove revert links
        Collection<UniqueRegistryKey> revertReferences = this.revertLinks.remove(registration);
        revertReferences = revertReferences == null ? Collections.emptyList() : revertReferences;
        revertReferences.forEach(key -> cleanDirectLinks(registration, key));
    }

    private void cleanRevertLinks(@Nonnull UniqueRegistryKey from, @Nonnull UniqueRegistryKey to) {
        Set<UniqueRegistryKey> references = this.revertLinks.get(to);
        references = references == null ? new HashSet<>() : references;
        references.remove(from);
        this.revertLinks.put(to, references);
    }

    private void cleanDirectLinks(@Nonnull UniqueRegistryKey from, @Nonnull UniqueRegistryKey to) {
        Set<UniqueRegistryKey> references = this.directLinks.get(to);
        references = references == null ? new HashSet<>() : references;
        references.remove(from);
        this.directLinks.put(to, references);
    }

    @Nonnull
    @Override
    public Set<UniqueRegistryKey> getReferencesTo(@Nonnull UniqueRegistryKey key) {
        Set<UniqueRegistryKey> result = revertLinks.get(key);
        return result == null ? Collections.emptySet() : result;
    }

    @Nonnull
    @Override
    public Set<UniqueRegistryKey> getReferencesFrom(UniqueRegistryKey key) {
        Set<UniqueRegistryKey> result = directLinks.get(key);
        return result == null ? Collections.emptySet() : result;
    }

    @Nonnull
    @Override
    public Set<UniqueRegistryKey> getContains(@Nonnull UniqueRegistryKey key) {
        Set<UniqueRegistryKey> result = contains.get(key);
        return result == null ? Collections.emptySet() : result;
    }

    @Autowired
    public void setHandlers(Collection<DeleteHandler> deleteHandlers) {
        for (DeleteHandler deleteHandler : deleteHandlers) {
            deleteHandlerTable.put(deleteHandler.getRemovedEntityType(), deleteHandler.getLinkedEntityType(),
                    deleteHandler);
        }
    }

    @Autowired
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        directLinks = hazelcastInstance.getMap("directLinks");
        revertLinks = hazelcastInstance.getMap("revertLinks");
        contains = hazelcastInstance.getMap("contains");
    }

    @Override
    public boolean isKeyPresented(@Nonnull UniqueRegistryKey key) {
        ILock lock = hazelcastInstance.getLock(REG);
        lock.lock();
        try {
            return directLinks.get(key) != null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isAllKeysPresented(@Nonnull Collection<UniqueRegistryKey> keys) {
        ILock lock = hazelcastInstance.getLock(REG);
        lock.lock();
        try {
            return keys.stream().allMatch(key -> directLinks.get(key) != null);
        } finally {
            lock.unlock();
        }
    }

	@Override
	public void cleanup() {
		contains.evictAll();
		directLinks.evictAll();
		revertLinks.evictAll();

	}
}

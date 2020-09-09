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

package com.unidata.mdm.backend.service.classifier.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mikhail Mikhailov
 * Classifiers nodes cache component.
 */
@Component
public class ClassifiersMetaModelCacheComponent implements InitializingBean {
    /**
     * Classifiers cache prefix.
     */
    public static final String CLASSIFIERS_NAMES_PREFIX = "$CLSF#";
    /**
     * Classifiers map lock name.
     */
    public static final String CLASSIFIERS_MAPS_LOCK_NAME = "classifiers_maps_lock";
    /**
     * CLSF cache.
     */
    private static final ConcurrentHashMap<String, CachedClassifier> CLASSIFIERS_CACHE = new ConcurrentHashMap<>();
    /**
     * HZ.
     */
    @Autowired
    private HazelcastInstance instance;
    /**
     * The clsf dao.
     */
    @Autowired
    private ClsfDao clsfDao;
    /**
     * Classifiers map lock.
     */
    private ILock classifiersLock;
    /**
     * Backup count.
     */
    @Value("${" + ConfigurationConstants.CLASSIFIER_CACHE_BACKUP_COUNT + ":1}")
    private int backupCount;
    /**
     * Cache entry TTL.
     */
    @Value("${" + ConfigurationConstants.CLASSIFIER_CACHE_ENTRY_TTL + ":1800}")
    private int entryTTL;
    /**
     * Create near cache or not. True is the default.
     */
    @Value("${" + ConfigurationConstants.CLASSIFIER_CACHE_USE_NEAR + ":true}")
    private boolean createNearCache;
    /**
     * Constructor.
     */
    public ClassifiersMetaModelCacheComponent() {
        super();
    }
    /**
     * Gets the cached classifier for a name.
     * @param classifierName the classifier name
     * @return
     */
    public CachedClassifier getClassifier(@Nonnull String classifierName) {
        return CLASSIFIERS_CACHE.computeIfAbsent(classifierName, name -> {

            ClsfPO clsf = clsfDao.getClassifierByName(classifierName);
            if (Objects.isNull(clsf)) {
                return null;
            }

            classifiersLock.lock();
            try {
                return new CachedClassifier(clsf, instance, backupCount, entryTTL, createNearCache);
            } finally {
                classifiersLock.unlock();
            }
        });
    }
    /**
     * Tells, if the classifier exists.
     * @param classifierName the name
     * @return true, if so, false otherwise
     */
    public boolean classifierExists(String classifierName) {
        return CLASSIFIERS_CACHE.containsKey(classifierName);
    }
    /**
     * Rereads the classifier data.
     * @param classifierName the name
     */
    public void refreshClassifier(String classifierName) {

        ClsfPO clsf = clsfDao.getClassifierByName(classifierName);
        if (Objects.isNull(clsf)) {
            return;
        }

        CachedClassifier cached = getClassifier(classifierName);
        if (Objects.isNull(cached)) {
            return;
        }

        cached.readClassifierProperties(clsf);
    }

    /**
     * Deletes the map entries only, removes the classifier object from map and destroys the underlaying map.
     * @param classifierName the name
     */
    public void destroyClassifier(String classifierName) {

        classifiersLock.lock();
        try {
            CachedClassifier clsf = getClassifier(classifierName);
            if (Objects.isNull(clsf)) {
                return;
            }

            clsf.getNodes().evictAll();
            clsf.getNodes().destroy();

            evictClassifier(classifierName);
        } finally {
            classifiersLock.unlock();
        }
    }

    /**
     * Deletes the map entries only and removes the classifier object from map.
     * This is called by the HZ event listener in response to delete event.
     * @param classifierName the name
     */
    public void evictClassifier(String classifierName) {

        CachedClassifier clsf = getClassifier(classifierName);
        if (Objects.isNull(clsf)) {
            return;
        }

        CLASSIFIERS_CACHE.remove(classifierName);
    }

//    public void clearClassifierCachedNodes(final String classifierName) {
//        CachedClassifier clsf = getClassifier(classifierName);
//        if (Objects.isNull(clsf)) {
//            return;
//        }
//
//        clsf.getNodes().evictAll();
//    }

    /**
     * Tells whether a node exists.
     * @param classifierName the CLSF name
     * @param nodeId the node did
     * @return true, if exists (in cache or can be loaded from store), false otherwise
     */
    public boolean nodeExists(@Nonnull String classifierName, @Nonnull String nodeId) {

        CachedClassifier clsf = getClassifier(classifierName);
        if (Objects.nonNull(clsf)) {
            return clsf.getNodes().containsKey(nodeId);
        }

        return false;
    }
    /**
     * Gets a node by id.
     * @param classifierName the classifier name
     * @param nodeId the node id
     * @return node or null
     */
    public CachedClassifierNode getNode(@Nonnull String classifierName, @Nonnull String nodeId) {

        CachedClassifier clsf = getClassifier(classifierName);
        if (Objects.isNull(clsf)) {
            return null;
        }

        CachedClassifierNode node = clsf.getNodes().get(nodeId);
        if (CachedClassifierNodeStore.NULL_NODE.sameNode(node)) {
            return null;
        }

        return node;
    }
    /**
     * Gets a node by id.
     * @param classifierName the classifier name
     * @param nodeId the node id
     * @return node or null
     */
    public List<CachedClassifierNode> getBranch(@Nonnull String classifierName, @Nonnull String nodeId) {

        CachedClassifier clsf = getClassifier(classifierName);
        if (Objects.isNull(clsf)) {
            return Collections.emptyList();
        }

        List<CachedClassifierNode> result = new ArrayList<>();
        String currentId = nodeId;
        do {

            CachedClassifierNode node = clsf.getNodes().get(currentId);
            if (CachedClassifierNodeStore.NULL_NODE.sameNode(node)) {
                return Collections.emptyList();
            }

            result.add(0, node);
            currentId = node.getParentNodeId();
        } while (Objects.nonNull(currentId));

        return result;
    }
    /**
     * Sets the node to store and cache.
     * @param classifierName the name of classifier to set the node to
     * @param node the node to set
     */
    public void setNode(@Nonnull String classifierName, @Nonnull CachedClassifierNode node) {

        CachedClassifier clsf = getClassifier(classifierName);
        if (Objects.isNull(clsf)) {
            return;
        }

        clsf.getNodes().lock(node.getNodeId());
        try {

            CachedClassifierNode updateCandidate = clsf.getNodes().get(node.getNodeId());
            boolean isNew = CachedClassifierNodeStore.NULL_NODE.sameNode(updateCandidate);

            if (!isNew) {
                node.setId(updateCandidate.getId());
            }

            clsf.getNodes().set(node.getNodeId(), node);

            if (isNew && Objects.nonNull(node.getParentNodeId())) {
                // Force parent node children re-read
                clsf.getNodes().evict(node.getParentNodeId());
            }

        } finally {
            clsf.getNodes().unlock(node.getNodeId());
        }
    }
    /**
     * De
     * @param classifierName
     * @param nodeId
     */
    public void removeNode(@Nonnull String classifierName, @Nonnull String nodeId) {

        CachedClassifier clsf = getClassifier(classifierName);
        if (Objects.isNull(clsf)) {
            return;
        }

        clsf.getNodes().lock(nodeId);
        try {

            CachedClassifierNode removeCandidate = clsf.getNodes().get(nodeId);
            if (CachedClassifierNodeStore.NULL_NODE.sameNode(removeCandidate)) {
                return;
            }

            // Remove the node itself
            clsf.getNodes().delete(nodeId);

            // Force re-read of the parent node
            if (Objects.nonNull(removeCandidate.getParentNodeId())) {
                clsf.getNodes().evict(removeCandidate.getParentNodeId());
            }

        } finally {
            clsf.getNodes().unlock(nodeId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        classifiersLock = instance.getLock(CLASSIFIERS_MAPS_LOCK_NAME);
    }
}

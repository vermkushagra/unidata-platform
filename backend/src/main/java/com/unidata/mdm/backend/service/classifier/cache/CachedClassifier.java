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

import com.hazelcast.config.*;
import com.hazelcast.config.EvictionConfig.MaxSizePolicy;
import com.hazelcast.config.MapStoreConfig.InitialLoadMode;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.merge.PassThroughMergePolicy;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;

import java.util.Objects;
/**
 * @author Mikhail Mikhailov
 * A cached classifier.
 */
public class CachedClassifier {
    /**
     * Nodes.
     */
    private final IMap<String, CachedClassifierNode> nodes;
    /**
     * Name of the cached object.
     */
    private final String cacheName;

    /**
     * The name.
     */
    private String name;
    /**
     * The display name.
     */
    private String displayName;
    /**
     * The description.
     */
    private String description;
    /**
     * The code pattern.
     */
    private String codePattern;
    /**
     * Validate code by level
     */
    private boolean validateCodeByLevel = true;
    /**
     * The root node id.
     */
    private final String rootNodeId;
    /**
     * Constructor.
     * @param clsf the name of the classifier
     * @param instance current HZ instance
     * @param backupCount number of backups per node
     * @param entryTTL entry's TTL
     * @param useNearCache create near cache or not
     */
    public CachedClassifier(ClsfPO clsf, HazelcastInstance instance, int backupCount, int entryTTL, boolean useNearCache) {

        super();

        this.cacheName = cachedClassifierName(clsf.getName());
        this.name = clsf.getName();
        this.rootNodeId = clsf.getName() + ".root";

        readClassifierProperties(clsf);

        MapConfig existing = instance.getConfig().getMapConfigOrNull(cacheName);
        if (Objects.isNull(existing)) {

            MapStoreConfig msc = new MapStoreConfig()
                    .setClassName(CachedClassifierNodeStore.class.getName())
                    .setEnabled(true)
                    .setProperty(CachedClassifierNodeStoreFactory.CLASSIFIER_NAME_PROPERTY, name)
                    .setFactoryClassName(CachedClassifierNodeStoreFactory.class.getName())
                    .setClassName(CachedClassifierNodeStore.class.getName())
                    .setInitialLoadMode(InitialLoadMode.LAZY);

            NearCacheConfig nc = null;
            if (useNearCache) {
                nc = new NearCacheConfig()
                        .setCacheLocalEntries(true)
                        .setInMemoryFormat(InMemoryFormat.OBJECT)
                        .setInvalidateOnChange(true)
                        .setTimeToLiveSeconds(0)
                        .setMaxIdleSeconds(entryTTL)
                        .setEvictionConfig(
                                new EvictionConfig(
                                        EvictionConfig.DEFAULT_MAX_ENTRY_COUNT,
                                        MaxSizePolicy.ENTRY_COUNT,
                                        EvictionPolicy.NONE
                                )
                        );
            }

            MapConfig config = new MapConfig(cacheName)
                    .setBackupCount(backupCount)
                    .setReadBackupData(true)
                    .setMaxSizeConfig(new MaxSizeConfig())
                    .setTimeToLiveSeconds(0)
                    .setMaxIdleSeconds(entryTTL)
                    .setEvictionPolicy(EvictionPolicy.NONE)
                    .setMergePolicyConfig(new MergePolicyConfig(PassThroughMergePolicy.class.getName(), 100))
                    .setNearCacheConfig(nc)
                    .setMapStoreConfig(msc);

            instance.getConfig().addMapConfig(config);
            nodes = instance.getMap(cacheName);
        } else {
            nodes = instance.getMap(cacheName);
        }
    }
    /**
     * Reads classifiers properties from PO object
     * @param clsf the PO object
     */
    public void readClassifierProperties(ClsfPO clsf) {
        this.validateCodeByLevel = clsf.isValidateCodeByLevel();
        this.codePattern = clsf.getCodePattern();
        this.description = clsf.getDescription();
        this.displayName = clsf.getDisplayName();
    }
    /**
     * Returns the nodes cache.
     * @return cache
     */
    public IMap<String, CachedClassifierNode> getNodes() {
        return nodes;
    }
    /**
     * Returns the name of the cache
     * @return name
     */
    public String getCacheName() {
        return cacheName;
    }
    /**
     * Returns the cache name of a classifier.
     * @param classifierName the name of the classifier
     * @return cache name
     */
    public static String cachedClassifierName(String classifierName) {
        return ClassifiersMetaModelCacheComponent.CLASSIFIERS_NAMES_PREFIX + classifierName;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * @return the codePattern
     */
    public String getCodePattern() {
        return codePattern;
    }
    /**
     * @param codePattern the codePattern to set
     */
    public void setCodePattern(String codePattern) {
        this.codePattern = codePattern;
    }
    /**
     * @return the validateCodeByLevel
     */
    public boolean isValidateCodeByLevel() {
        return validateCodeByLevel;
    }
    /**
     * @param validateCodeByLevel the validateCodeByLevel to set
     */
    public void setValidateCodeByLevel(boolean validateCodeByLevel) {
        this.validateCodeByLevel = validateCodeByLevel;
    }
    /**
     * @return the rootNodeId
     */
    public String getRootNodeId() {
        return rootNodeId;
    }
}
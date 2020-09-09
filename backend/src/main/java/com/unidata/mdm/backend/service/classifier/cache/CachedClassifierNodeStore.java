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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import com.hazelcast.core.MapStore;
import com.hazelcast.core.PostProcessingMapStore;
import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.service.classifier.converters.CachedClassifierNodeToClsfNodePOConverter;
import com.unidata.mdm.backend.service.classifier.converters.ClsfCustomPropertyPOToCachedClassifierPropertyConverter;
import com.unidata.mdm.backend.service.classifier.converters.ClsfNodeAttrPOToCachedClassifierNodeAttributeConverter;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Mikhail Mikhailov
 *
 */
public class CachedClassifierNodeStore implements MapStore<String, CachedClassifierNode>, PostProcessingMapStore {
    /**
     * The null node sentinel.
     */
    public static final CachedClassifierNode NULL_NODE = new CachedClassifierNode();
    /**
     * The DAO to manipulate CLSF objects.
     */
    private final ClsfDao clsfDao;
    /**
     * The name of the classifier.
     */
    private final String classifierName;

    /**
     * Constructor.
     */
    public CachedClassifierNodeStore(ClsfDao clsfDao, String classifierName) {
        super();
        this.clsfDao = clsfDao;
        this.classifierName = classifierName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CachedClassifierNode load(String nodeId) {
        Pair<ClsfNodePO, List<ClsfNodePO>> result = clsfDao.getNodeAndChildrenById(classifierName, nodeId);
        if (Objects.isNull(result)) {
            return NULL_NODE;
        }

        // Node
        CachedClassifierNode node = new CachedClassifierNode();
        node.setName(result.getKey().getName());
        node.setDescription(result.getKey().getDescription());
        node.setCode(result.getKey().getCode());
        node.setNodeId(result.getKey().getNodeId());
        node.setParentNodeId(result.getKey().getParentId());
        node.setId(result.getKey().getId());

        node.getChildren()
            .addAll(result.getValue().stream()
                .map(ClsfNodePO::getNodeId)
                .collect(Collectors.toList()));

        // Attrs
        node.getAttributes()
            .putAll(ClsfNodeAttrPOToCachedClassifierNodeAttributeConverter.convert(
                clsfDao.findOnlyNodeAttrs(classifierName, result.getKey().getId()),
                    node.getId()));

        // Custom properties
        node.setCustomProperties(
                ClsfCustomPropertyPOToCachedClassifierPropertyConverter.convert(
                        result.getKey().getCustomProperties()));

        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, CachedClassifierNode> loadAll(Collection<String> keys) {
        // Eager fetch not supported
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<String> loadAllKeys() {
        // Eager fetch not supported
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store(String key, CachedClassifierNode value) {
        ClsfNodePO toStore = CachedClassifierNodeToClsfNodePOConverter.convert(value);
        if (toStore.getId() == 0) {
            int id = clsfDao.create(classifierName, toStore);
            value.setId(id);
        } else {
            clsfDao.update(classifierName, toStore);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeAll(Map<String, CachedClassifierNode> map) {
        for (Entry<String, CachedClassifierNode> entry : map.entrySet()) {
            store(entry.getKey(), entry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String key) {
        clsfDao.remove(classifierName, key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(Collection<String> keys) {
        for (String nodeId : keys) {
            delete(nodeId);
        }
    }
}

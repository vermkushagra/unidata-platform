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

package com.unidata.mdm.backend.service.model.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.service.model.util.parsers.ModelParser;
import com.unidata.mdm.backend.service.model.util.wrappers.ValueWrapper;
import com.unidata.mdm.meta.Model;

/**
 * Meta model cache.
 * @author ilya.bykov
 */
public class ModelCache {
    /** The logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelCache.class);
    /** The model. */
    private Model model;

    /** The cache. */
    private final Map<Class<? extends ValueWrapper>, Map<String, ? extends ValueWrapper>> cache;
    /** Storage id of the model */
    private final String storageId;
    /**
     * Reversed source systems list.
     */
    private Map<String, Integer> reversedSourceSystemsMap;
    /**
     * Straight source systems list.
     */
    private Map<String, Integer> straightSourceSystemsMap;

    /**
     * Instantiates a new model cache.
     *
     * @param model
     *            metamodel.
     * @param parser
     *            metamodel parser.
     */
    @SafeVarargs
    public ModelCache(Model model, String storageId, ModelParser<? extends ValueWrapper>... parser){
        this.cache = new HashMap<>();
        for (ModelParser<? extends ValueWrapper> modelParser : parser) {
            if (modelParser == null) {
                // do nothing as it's normal situation
                continue;
            }
            LOGGER.debug("Parse model with [{}].", modelParser.getClass().getSimpleName());
            cache.put(modelParser.getValueType(), modelParser.parse(model));
        }
        this.model = model;
        this.storageId = storageId;
    }
    /**
     * Gets the cache.
     *
     * @return the cache
     */
    public Map<Class<? extends ValueWrapper>, Map<String, ? extends ValueWrapper>> getCache() {
        return cache;
    }

    /**
     * Gets the model.
     *
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * @return the storageId
     */
    public String getStorageId() {
        return storageId;
    }

    /**
     * @return the reversedSourceSystemsMap
     */
    public Map<String, Integer> getReversedSourceSystemsMap() {
        return reversedSourceSystemsMap;
    }

    /**
     * @param reversedSourceSystemsMap the reversedSourceSystemsMap to set
     */
    public void setReversedSourceSystemsMap(Map<String, Integer> reversedSourceSystems) {
        this.reversedSourceSystemsMap = reversedSourceSystems;
    }

    /**
     * @return the straightSourceSystemsMap
     */
    public Map<String, Integer> getStraightSourceSystemsMap() {
        return straightSourceSystemsMap;
    }

    /**
     * @param straightSourceSystemsMap the straightSourceSystemsMap to set
     */
    public void setStraightSourceSystemsMap(Map<String, Integer> straightSourceSystems) {
        this.straightSourceSystemsMap = straightSourceSystems;
    }

}

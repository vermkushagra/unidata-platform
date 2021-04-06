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

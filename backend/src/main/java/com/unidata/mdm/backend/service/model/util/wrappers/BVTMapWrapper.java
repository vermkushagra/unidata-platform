/**
 *
 */
package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Map;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;


/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class BVTMapWrapper extends SearchableElementsWrapper {

    /**
     * BVT attributes map.
     */
    protected Map<String, Map<String, Integer>> bvtMap;

    /**
     * Constructor.
     * @param id
     * @param attrs
     */
    public BVTMapWrapper(String id, Map<String, AttributeInfoHolder> attrs, Map<String, Map<String, Integer>> bvtMap) {
        super(id, attrs);
        this.bvtMap = bvtMap;
    }

    /**
     * @return the bvtMap
     */
    public Map<String, Map<String, Integer>> getBvtMap() {
        return bvtMap;
    }


    /**
     * @param bvtMap the bvtMap to set
     */
    public void setBvtMap(Map<String, Map<String, Integer>> bvtMap) {
        this.bvtMap = bvtMap;
    }
    /**
     * Tells whether this wrapper is a lookup entity.
     * @return true, if so, false otherwise
     */
    public abstract boolean isLookup();
    /**
     * Tells whether this wrapper is a regular entity.
     * @return true, if so, false otherwise
     */
    public abstract boolean isEntity();
}

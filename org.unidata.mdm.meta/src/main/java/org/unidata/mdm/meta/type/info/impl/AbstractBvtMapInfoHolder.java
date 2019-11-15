package org.unidata.mdm.meta.type.info.impl;

import java.util.Map;

import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.BvtMapModelElement;

/**
 * @author Mikhail Mikhailov
 * BVT map holder.
 */
public abstract class AbstractBvtMapInfoHolder extends AbstractAttributesInfoHolder implements BvtMapModelElement {

    /**
     * BVT attributes map.
     */
    protected Map<String, Map<String, Integer>> bvtMap;
    /**
     * Constructor.
     * @param attrs
     */
    public AbstractBvtMapInfoHolder(Map<String, AttributeModelElement> attrs, Map<String, Map<String, Integer>> bvtMap) {
        super(attrs);
        this.bvtMap = bvtMap;
    }
    /**
     * @return the bvtMap
     */
    @Override
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

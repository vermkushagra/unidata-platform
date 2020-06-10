package com.unidata.mdm.backend.common.search.id;

import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 * @author Mikhail Mikhailov
 * Managed index id common stuff.
 */
public interface ManagedIndexId {
    /**
     * Gets search type for this id object.
     * @return the search type
     */
    SearchType getSearchType();
    /**
     * Generates index id for this object.
     * @return full index id
     */
    String getIndexId();
    /**
     * Generates routing string for this object.
     * @return the routing string
     */
    String getRouting();
    /**
     * Gets the entity name, the object belongs to.
     * @return the name
     */
    String getEntityName();
}

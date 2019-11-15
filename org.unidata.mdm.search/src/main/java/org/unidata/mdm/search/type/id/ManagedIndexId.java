package org.unidata.mdm.search.type.id;

import org.unidata.mdm.search.type.IndexType;

/**
 * @author Mikhail Mikhailov
 * Managed index id common stuff.
 */
public interface ManagedIndexId {
    /**
     * Gets search type for this id object.
     * @return the search type
     */
    IndexType getSearchType();
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
     * TODO: Rename method
     * Gets the entity name, the object belongs to.
     * @return the name
     */
    String getEntityName();
}

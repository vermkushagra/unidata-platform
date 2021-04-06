package com.unidata.mdm.backend.common.context;

/**
 * @author Mikhail Mikhailov
 * Common properties for contexts from search/indexing subsystem.
 */
public interface SearchContext extends StorageContext {
    /**
     * Search entity (index name).
     *
     * @return entity name
     */
    String getEntity();
}

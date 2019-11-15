package org.unidata.mdm.search.context;

import org.unidata.mdm.system.context.StorageSpecificContext;

/**
 * @author Mikhail Mikhailov
 * Common properties for contexts from search/indexing subsystem.
 * TODO: Rename type, method as it has nothing to do with 'type' but with the name of the target index.
 */
public interface TypedSearchContext extends StorageSpecificContext {
    /**
     * Search entity (index name).
     *
     * @return entity name
     */
    String getEntity();
}

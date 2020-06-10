package com.unidata.mdm.backend.common.context;

import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Abstract from side request context.
 */
public abstract class AbstractRelationsFromRequestContext<T extends AbstractRelationToRequestContext>
    extends CommonRequestContext
    implements RecordIdentityContext {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8277274116336739520L;

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(StorageId.RELATIONS_FROM_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.RELATIONS_FROM_KEY;
    }
    /**
     * Gets the To side relations.
     * @return map of relations
     */
    public abstract Map<String, List<T>> getRelations();
}

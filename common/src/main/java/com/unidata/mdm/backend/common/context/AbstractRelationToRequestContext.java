/**
 *
 */
package com.unidata.mdm.backend.common.context;

import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class AbstractRelationToRequestContext
    extends CommonSendableContext implements RelationIdentityContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 7823433615112393227L;
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(StorageId.RELATIONS_TO_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.RELATIONS_TO_KEY;
    }
}

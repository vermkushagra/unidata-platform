package org.unidata.mdm.meta.type.event;

import org.unidata.mdm.system.type.event.AbstractForeignEvent;

/**
 * This event is sent when other nodes have to notified that they have to reload model.
 * @author Mikhail Mikhailov on Oct 28, 2019
 */
public class ModelReloadEvent extends AbstractForeignEvent {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = 807674465478762451L;
    /**
     * This type name.
     */
    private static final String TYPE_NAME = "MODEL_RELOAD_EVENT";
    /**
     * Constructor.
     * @param typeName
     * @param id
     */
    public ModelReloadEvent(String id, String storageId) {
        super(TYPE_NAME, id);
        this.storageId = storageId;
    }
}

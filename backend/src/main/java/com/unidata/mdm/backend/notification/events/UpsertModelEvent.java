package com.unidata.mdm.backend.notification.events;

import java.io.Serializable;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;

/**
 * Notification event
 */
public class UpsertModelEvent implements Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -3159734591100923791L;

    private final UpdateModelRequestContext updateModelRequestContext;

    public UpsertModelEvent(UpdateModelRequestContext updateModelRequestContext) {
        this.updateModelRequestContext = updateModelRequestContext;
    }

    public UpdateModelRequestContext getUpdateModelRequestContext() {
        return updateModelRequestContext;
    }
}

package com.unidata.mdm.backend.notification.events;


import java.io.Serializable;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;

/**
 * Notification event
 */
public class DeleteModelEvent implements Serializable {

    private final DeleteModelRequestContext deleteModelRequestContext;

    public DeleteModelEvent(DeleteModelRequestContext deleteModelRequestContext) {
        this.deleteModelRequestContext = deleteModelRequestContext;
    }

    public DeleteModelRequestContext getDeleteModelRequestContext() {
        return deleteModelRequestContext;
    }
}

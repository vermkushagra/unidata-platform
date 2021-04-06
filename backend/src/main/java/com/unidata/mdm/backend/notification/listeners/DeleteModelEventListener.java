package com.unidata.mdm.backend.notification.listeners;

import com.hazelcast.core.Message;
import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.notification.events.DeleteModelEvent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listener responsible for processing upsert model events.
 */
@Component
public class DeleteModelEventListener extends AbstractOwnRejectMessageListener<DeleteModelEvent> {


    @Autowired
    private MetaModelServiceExt metaModelService;

    @Override
    public void onForeignMessage(Message<DeleteModelEvent> message) {
        DeleteModelRequestContext deleteModelRequestContext = message.getMessageObject().getDeleteModelRequestContext();
        metaModelService.synchronizationDeleteModel(deleteModelRequestContext);
    }
}

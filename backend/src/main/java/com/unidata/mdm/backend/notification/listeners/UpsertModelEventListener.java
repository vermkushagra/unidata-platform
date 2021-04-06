package com.unidata.mdm.backend.notification.listeners;

import com.hazelcast.core.Message;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.notification.events.UpsertModelEvent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listener responsible for processing upsert model events.
 */
@Component
public class UpsertModelEventListener extends AbstractOwnRejectMessageListener<UpsertModelEvent> {

    @Autowired
    private MetaModelServiceExt metaModelService;

    @Override
    public void onForeignMessage(Message<UpsertModelEvent> message) {
        UpdateModelRequestContext updateModelRequestContext = message.getMessageObject().getUpdateModelRequestContext();
        metaModelService.synchronizationUpsertModel(updateModelRequestContext);
    }
}

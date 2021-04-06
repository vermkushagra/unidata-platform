package com.unidata.mdm.backend.notification.notifiers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.notification.events.DeleteModelEvent;
import com.unidata.mdm.backend.notification.events.UpsertModelEvent;
import com.unidata.mdm.backend.notification.listeners.DeleteModelEventListener;
import com.unidata.mdm.backend.notification.listeners.UpsertModelEventListener;

/**
 * Implementation of {@link ModelChangesNotifier} which work over Hazelcast topics.
 * {@inheritDoc}
 */
@Component
public class HazelcastModelChangesNotifier implements ModelChangesNotifier {

    /**
     * Hazelcast topic for delete model events
     */
    private ITopic<DeleteModelEvent> deleteTopic;

    /**
     * Hazelcast topic fot upsert model events
     */
    private ITopic<UpsertModelEvent> upsertTopic;

    /**
     * Hazelcast distributed cache
     */
    @Autowired
    private HazelcastInstance cache;

    /**
     * Listener fot Delete Model Events
     */
    @Autowired
    private DeleteModelEventListener deleteModelEventListener;

    /**
     * Listener fot Upsert Model Events
     */
    @Autowired
    private UpsertModelEventListener upsertModelEventListener;

    @Override
    public void afterContextRefresh() {
        deleteTopic = cache.getTopic("modelDeleteTopic");
        upsertTopic = cache.getTopic("modelUpsertTopic");
        cache.getCluster().getLocalMember().getUuid();
        deleteTopic.addMessageListener(deleteModelEventListener);
        upsertTopic.addMessageListener(upsertModelEventListener);
    }

    @Override
    public void notifyOtherNodesAboutDeleteModel(DeleteModelRequestContext context) {
        deleteTopic.publish(new DeleteModelEvent(context));
    }

    @Override
    public void notifyOtherNodesAboutUpsertModel(UpdateModelRequestContext ctx) {
        upsertTopic.publish(new UpsertModelEvent(ctx));
    }
}

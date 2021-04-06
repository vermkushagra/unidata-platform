package com.unidata.mdm.backend.notification.notifiers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.unidata.mdm.backend.notification.events.MatchingGroupsEvent;
import com.unidata.mdm.backend.notification.listeners.MatchingGroupsChangeEventListener;

/**
 * @author Mikhail Mikhailov
 * Matching groups notifier.
 */
@Component
public class HazelcastMatchingGroupsChangesNotifier implements MatchingGroupsChangesNotifier {
    /**
     * Matching groups topic name.
     */
    public static final String MATCHING_GROUPS_TOPIC_NAME = "matchingGroupsTopic";
    /**
     * Notifications topic.
     */
    private ITopic<MatchingGroupsEvent> matchingGroupsTopic;
    /**
     * Hazelcast distributed cache
     */
    @Autowired
    private HazelcastInstance instance;
    /**
     * Assignments listener.
     */
    @Autowired
    private MatchingGroupsChangeEventListener eventListener;
    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyMatchingGroupsChanged(Collection<String> entityNames) {
        matchingGroupsTopic.publish(new MatchingGroupsEvent(entityNames));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterContextRefresh() {
        matchingGroupsTopic = instance.getTopic(MATCHING_GROUPS_TOPIC_NAME);
        matchingGroupsTopic.addMessageListener(eventListener);
    }
}

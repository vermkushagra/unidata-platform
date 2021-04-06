package com.unidata.mdm.backend.notification.notifiers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.unidata.mdm.backend.notification.events.MatchingRulesEvent;
import com.unidata.mdm.backend.notification.listeners.MatchingRulesChangeEventListener;

/**
 * @author Mikhail Mikhailov
 * Matching rules notifier.
 */
@Component
public class HazelcastMatchingRulesChangesNotifier implements MatchingRulesChangesNotifier {
    /**
     * Matching rules topic name.
     */
    public static final String MATCHING_RULES_TOPIC_NAME = "matchingRulesTopic";
    /**
     * Notifications topic.
     */
    private ITopic<MatchingRulesEvent> matchingRulesTopic;
    /**
     * Hazelcast distributed cache
     */
    @Autowired
    private HazelcastInstance instance;
    /**
     * Assignments listener.
     */
    @Autowired
    private MatchingRulesChangeEventListener eventListener;
    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyMatchingRulesChanged(Collection<String> entityNames) {
        matchingRulesTopic.publish(new MatchingRulesEvent(entityNames));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterContextRefresh() {
        matchingRulesTopic = instance.getTopic(MATCHING_RULES_TOPIC_NAME);
        matchingRulesTopic.addMessageListener(eventListener);
    }
}

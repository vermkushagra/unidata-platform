package com.unidata.mdm.backend.notification.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.Message;
import com.unidata.mdm.backend.notification.events.MatchingRulesEvent;
import com.unidata.mdm.backend.service.matching.MatchingRulesService;

/**
 * @author Mikhail Mikhailov
 * Process matching rule change events.
 */
@Component
public class MatchingRulesChangeEventListener extends AbstractOwnRejectMessageListener<MatchingRulesEvent> {
    /**
     * The workflow service.
     */
    @Autowired
    private MatchingRulesService matchingRuleService;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onForeignMessage(Message<MatchingRulesEvent> message) {
        matchingRuleService.loadRules(message.getMessageObject().getEntityNames(), false);
    }
}

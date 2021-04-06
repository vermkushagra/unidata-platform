package com.unidata.mdm.backend.notification.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.Message;
import com.unidata.mdm.backend.notification.events.MatchingGroupsEvent;
import com.unidata.mdm.backend.service.matching.MatchingGroupsService;

/**
 * @author Mikhail Mikhailov
 * Process matching groups change events.
 */
@Component
public class MatchingGroupsChangeEventListener extends AbstractOwnRejectMessageListener<MatchingGroupsEvent> {
    /**
     * The groups service.
     */
    @Autowired
    private MatchingGroupsService matchingGroupsService;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onForeignMessage(Message<MatchingGroupsEvent> message) {
        matchingGroupsService.loadGroups(message.getMessageObject().getEntityNames(), false);
    }
}

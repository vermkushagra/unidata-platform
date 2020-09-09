/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

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

package com.unidata.mdm.backend.notification.notifiers.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.unidata.mdm.backend.notification.events.ClassifierChangeEvent;
import com.unidata.mdm.backend.notification.events.ClassifierChangeEvent.ClassifierChangesEventType;
import com.unidata.mdm.backend.notification.listeners.ClassifierChangeEventListener;
import com.unidata.mdm.backend.notification.notifiers.ClassifierChangeNotifier;

/**
 * @author Mikhail Mikhailov
 * Notifier implementation.
 */
@Component
public class HazelcastClassifierChangeNotifier implements ClassifierChangeNotifier {
    /**
     * Matching rules topic name.
     */
    public static final String CLASSIFIER_CHANGE_TOPIC_NAME = "classifiersMetaTopic";
    /**
     * Notifications topic.
     */
    private ITopic<ClassifierChangeEvent> classifiersMetaTopic;
    /**
     * Hazelcast distributed cache
     */
    @Autowired
    private HazelcastInstance instance;
    /**
     * The change listener.
     */
    @Autowired
    private ClassifierChangeEventListener listener;
    /**
     * Constructor.
     */
    public HazelcastClassifierChangeNotifier() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyClassifierChanged(ClassifierChangesEventType changeType, Collection<String> classifierNames) {
        classifiersMetaTopic.publish(new ClassifierChangeEvent(changeType, classifierNames));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterContextRefresh() {
        classifiersMetaTopic = instance.getTopic(CLASSIFIER_CHANGE_TOPIC_NAME);
        classifiersMetaTopic.addMessageListener(listener);
    }
}

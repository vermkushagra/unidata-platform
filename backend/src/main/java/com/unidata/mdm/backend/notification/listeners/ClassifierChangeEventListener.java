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

package com.unidata.mdm.backend.notification.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.Message;
import com.unidata.mdm.backend.notification.events.ClassifierChangeEvent;
import com.unidata.mdm.backend.notification.events.ClassifierChangeEvent.ClassifierChangesEventType;
import com.unidata.mdm.backend.service.classifier.cache.ClassifiersMetaModelCacheComponent;

/**
 * @author Mikhail Mikhailov
 * Process matching rule change events.
 */
@Component
public class ClassifierChangeEventListener extends AbstractOwnRejectMessageListener<ClassifierChangeEvent> {
    /**
     * The workflow service.
     */
    @Autowired
    private ClassifiersMetaModelCacheComponent cacheComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onForeignMessage(Message<ClassifierChangeEvent> message) {
        if (message.getMessageObject().getEventType() == ClassifierChangesEventType.REMOVE) {
            message.getMessageObject().getClassifierNames().forEach(cacheComponent::evictClassifier);
        } else if (message.getMessageObject().getEventType() == ClassifierChangesEventType.REFRESH) {
            message.getMessageObject().getClassifierNames().forEach(cacheComponent::refreshClassifier);
        }
    }
}

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

package org.unidata.mdm.core.service;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Abstract Hazelcast message listener which do nothing if message was generated on the same node.
 */
public abstract class AbstractOwnRejectMessageListener<T> implements MessageListener<T> {

    @Override
    public void onMessage(Message<T> message) {
        boolean isOwnMessage = message.getPublishingMember().localMember();
        if (!isOwnMessage) {
            onForeignMessage(message);
        }
    }

    public abstract void onForeignMessage(Message<T> message);
}

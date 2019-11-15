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

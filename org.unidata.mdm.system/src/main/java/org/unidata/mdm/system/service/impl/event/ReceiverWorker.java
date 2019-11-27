package org.unidata.mdm.system.service.impl.event;

import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.system.type.event.Event;
import org.unidata.mdm.system.type.event.EventReceiver;

import com.hazelcast.core.IAtomicLong;

/**
 * Simple subscription info holder.
 * @author Mikhail Mikhailov on Oct 30, 2019
 */
public class ReceiverWorker {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverWorker.class);
    /**
     * Known subscriber count.
     */
    private final IAtomicLong subscriberCount;
    /**
     * Receivers, registered locally.
     */
    private final CopyOnWriteArraySet<EventReceiver> receivers;
    /**
     * Constructor.
     * @param subscriberCount
     */
    public ReceiverWorker(IAtomicLong subscriberCount) {
        this.subscriberCount = subscriberCount;
        this.receivers = new CopyOnWriteArraySet<>();
    }
    /**
     * Returns the currently known number of subscribers cluster-wide.
     * @return count
     */
    public int knownCount() {
        return (int) subscriberCount.get();
    }

    public void subscribe(EventReceiver receiver) {
        receivers.add(receiver);
        subscriberCount.incrementAndGet();
    }

    public void unsubscribe(EventReceiver receiver) {
        receivers.remove(receiver);
        subscriberCount.decrementAndGet();
    }

    public int receive(Event event, boolean fromLocalMember) {

        switch (event.getBroadcastType()) {
        case FOREIGN:
            if (fromLocalMember) {
                return receivers.size();
            }
            break;
        case LOCAL:
            if (!fromLocalMember) {
                return receivers.size();
            }
            break;
        default:
            break;
        }

        int count = 0;
        for (EventReceiver receiver : receivers) {
            try {
                receiver.receive(event);
            } catch (Exception e) {
                LOGGER.warn("Exception caught, while receiving event [{}].", event, e);
            } finally {
                count++;
            }
        }

        return count;
    }
}

package org.unidata.mdm.core.service;

import org.unidata.mdm.core.type.event.Event;
import org.unidata.mdm.core.type.event.EventReceiver;
/**
 * The platform event service - simple wrapper around two HZ topics.
 */
public interface EventService {
    /**
     * Fire an event without reply.
     * @param event the event
     */
    void fire(Event event);
    /**
     * Fire an event and wait for 'receive' reply from all receivers or timeout.
     * @param event the event
     * @return true if the event was successfully processed by all receivers, false, if the timeout occured earlier
     */
    boolean fireAndWait(Event event);
    /**
     * Fire an event and wait for 'receive' reply from all receivers or timeout.
     * @param event the event
     * @param timeout the timeout in millis
     * @return true if the event was successfully processed by all receivers, false, if the timeout occured earlier
     */
    boolean fireAndWait(Event event, long timeout);
    /**
     * Receive ALL events. Not to be used by ordinary receivers but system ones (audit, distributed logging etc.)
     * @param subscriber the receiver
     */
    void register(EventReceiver subscriber);
    /**
     * Unregister system receiver to receive ALL events.
     * @param subscriber the receiver
     */
    void unregister(EventReceiver subscriber);
    /**
     * Receive only events of particular type. This should normally be used for everything.
     * @param <T> the event type
     * @param subscriber the receiver
     * @param eventType the event type class
     */
    <T extends Event> void register(EventReceiver subscriber, Class<T> eventType);
    /**
     * Unregister rceiver.
     * @param <T> the event type
     * @param subscriber the receiver
     * @param eventType the event type class
     */
    <T extends Event> void unregister(EventReceiver subscriber, Class<T> eventType);
}

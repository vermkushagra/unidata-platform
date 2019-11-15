package org.unidata.mdm.core.type.event;

/**
 * Named 'receiver' just to reduce autocomplete suggestions.
 * @author Mikhail Mikhailov on Oct 30, 2019
 */
public interface EventReceiver {
    /**
     * Receive an event.
     * @param event the event
     */
    void receive(Event event);
}

package org.unidata.mdm.system.type.event;

import java.io.Serializable;

public interface Event extends Serializable {
    /**
     * This might be useful for listeners / subscribers, that do not filetr on the event type.
     * @return type name
     */
    String getTypeName();
    /**
     * Gets the id.
     * @return the ebent id
     */
    String getId();
    /**
     * Gets the type of broadcasting.
     * @return type
     */
    BroadcastType getBroadcastType();
    /**
     * Returns true, if the event should be replied.
     * @return true for awaited replay, false otherwise
     */
    default boolean withReplay() {
        return false;
    }
}

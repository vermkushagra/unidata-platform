package org.unidata.mdm.system.type.event;

/**
 * Type of broadcasting.
 * @author Mikhail Mikhailov on Oct 28, 2019
 */
public enum BroadcastType {
    /**
     * The event will be delivered to local listeners only.
     */
    LOCAL,
    /**
     * The event will be delivered to foreign listeners only.
     */
    FOREIGN,
    /**
     * The event will be delivered to both local and foreign listeners.
     */
    BOTH
}

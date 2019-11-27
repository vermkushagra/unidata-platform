package org.unidata.mdm.system.service.impl.event;

import org.unidata.mdm.system.type.event.AbstractMulticastEvent;

/**
 * @author Mikhail Mikhailov on Oct 29, 2019
 */
public class SystemReplayEvent extends AbstractMulticastEvent {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = -6947329119790339516L;
    /**
     * This type name.
     */
    private static final String TYPE_NAME = "SYSTEM_REPLAY_EVENT";
    /**
     * The number of receivers to receive the original message.
     */
    private final int receiverCount;
    /**
     * Constructor.
     * @param typeName
     * @param id
     */
    public SystemReplayEvent(String id, int receiverCount) {
        super(TYPE_NAME, id);
        this.receiverCount = receiverCount;
    }
    /**
     * @return the receiverCount
     */
    public int getReceiverCount() {
        return receiverCount;
    }
}

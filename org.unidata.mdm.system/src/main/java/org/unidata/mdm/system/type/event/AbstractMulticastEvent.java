package org.unidata.mdm.system.type.event;

/**
 * @author Mikhail Mikhailov on Oct 28, 2019
 */
public abstract class AbstractMulticastEvent extends AbstractEvent {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = -3807384836300688428L;
    /**
     * Constructor.
     * @param typeName
     * @param id
     */
    public AbstractMulticastEvent(String typeName, String id) {
        super(typeName, id);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BroadcastType getBroadcastType() {
        return BroadcastType.BOTH;
    }
}

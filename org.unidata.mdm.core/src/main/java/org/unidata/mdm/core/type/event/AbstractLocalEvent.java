package org.unidata.mdm.core.type.event;

/**
 * @author Mikhail Mikhailov on Oct 28, 2019
 */
public abstract class AbstractLocalEvent extends AbstractEvent {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = 8687345182326379107L;
    /**
     * Constructor.
     * @param typeName
     * @param id
     */
    public AbstractLocalEvent(String typeName, String id) {
        super(typeName, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BroadcastType getBroadcastType() {
        return BroadcastType.LOCAL;
    }
}

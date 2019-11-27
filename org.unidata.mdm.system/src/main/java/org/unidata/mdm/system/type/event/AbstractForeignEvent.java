package org.unidata.mdm.system.type.event;

/**
 * @author Mikhail Mikhailov on Oct 28, 2019
 */
public abstract class AbstractForeignEvent extends AbstractEvent {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = 3508410130810320797L;
    /**
     * Constructor.
     * @param typeName
     * @param id
     */
    public AbstractForeignEvent(String typeName, String id) {
        super(typeName, id);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public BroadcastType getBroadcastType() {
        return BroadcastType.FOREIGN;
    }
}

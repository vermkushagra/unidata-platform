package org.unidata.mdm.core.type.event;

/**
 * @author Mikhail Mikhailov on Oct 28, 2019
 */
public abstract class AbstractEvent implements Event {
    /**
     * GSVUID.
     */
    private static final long serialVersionUID = 6935407258627041109L;
    /**
     * The type name.
     */
    private final String typeName;
    /**
     * The id.
     */
    private final String id;
    /**
     * The storage id.
     */
    protected String storageId;
    /**
     * Sets internal replay flag.
     */
    private boolean withReplay;
    /**
     * Constructor.
     */
    public AbstractEvent(String typeName, String id) {
        super();
        this.typeName = typeName;
        this.id = id;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName() {
        return typeName;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }
    /**
     * @return the storageId
     */
    public String getStorageId() {
        return storageId;
    }
    /**
     * @return the withReplay
     */
    @Override
    public boolean withReplay() {
        return withReplay;
    }
    /**
     * @param withReplay the withReplay to set
     */
    public void setReplay(boolean withReplay) {
        this.withReplay = withReplay;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Event: id [" + id + "], type [" + typeName + "], broadcast [" + getBroadcastType().name() + "], replay [" + withReplay + "]";
    }
}

package org.unidata.mdm.search.type.id;

/**
 * @author Mikhail Mikhailov
 * Common part of managed index id.
 */
public abstract class AbstractManagedIndexId implements ManagedIndexId {
    /**
     * Entity name.
     */
    protected String entityName;
    /**
     * Generated index id.
     */
    protected String indexId;
    /**
     * Routing.
     */
    protected String routing;
    /**
     * Constructor.
     */
    protected AbstractManagedIndexId() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getIndexId() {
        return indexId;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRouting() {
        return routing;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityName() {
        return entityName;
    }
}

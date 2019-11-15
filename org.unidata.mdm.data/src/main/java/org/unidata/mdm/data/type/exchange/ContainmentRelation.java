package org.unidata.mdm.data.type.exchange;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ContainmentRelation extends ExchangeRelation {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 5402408234136849707L;
    /**
     * Containment entity.
     */
    private ExchangeEntity entity;

    /**
     * Constructor.
     */
    public ContainmentRelation() {
        super();
    }

    /**
     * @return the entity
     */
    public ExchangeEntity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(ExchangeEntity entity) {
        this.entity = entity;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public boolean isContainment() {
        return true;
    }
}

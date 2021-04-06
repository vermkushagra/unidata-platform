package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Exchange definition JSON root.
 */
public class ExchangeDefinition implements Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -1326611614773865540L;

    /**
     * Lookup entities.
     */
    private List<ExchangeEntity> lookupEntities;

    /**
     * Lookup entities.
     */
    private List<ExchangeEntity> entities;

    /**
     * Constructor.
     */
    public ExchangeDefinition() {
        super();
    }

    /**
     * @return the lookupEntities
     */
    public List<ExchangeEntity> getLookupEntities() {
        if (lookupEntities == null) {
            lookupEntities = new ArrayList<>();
        }
        return lookupEntities;
    }

    /**
     * @param lookupEntities the lookupEntities to set
     */
    public void setLookupEntities(List<ExchangeEntity> lookupEntities) {
        this.lookupEntities = lookupEntities;
    }

    /**
     * @return the entities
     */
    public List<ExchangeEntity> getEntities() {
        if (entities == null) {
            entities = new ArrayList<>();
        }
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(List<ExchangeEntity> entities) {
        this.entities = entities;
    }

}

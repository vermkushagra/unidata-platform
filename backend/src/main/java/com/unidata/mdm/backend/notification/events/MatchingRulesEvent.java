package com.unidata.mdm.backend.notification.events;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Mikhail Mikhailov
 * Matching rules changed event.
 */
public class MatchingRulesEvent implements Serializable {
    /**
     * Make sonar happy.
     */
    private static final long serialVersionUID = 8111668085152290454L;
    /**
     * The entity names.
     */
    private final Collection<String> entityNames;
    /**
     * Constructor.
     * @param entityNames the names to reload rules for
     */
    public MatchingRulesEvent(Collection<String> entityNames) {
        super();
        this.entityNames = entityNames;
    }
    /**
     * @return the entityNames
     */
    public Collection<String> getEntityNames() {
        return entityNames;
    }
}

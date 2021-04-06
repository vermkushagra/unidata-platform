package com.unidata.mdm.backend.notification.events;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Mikhail Mikhailov
 * Matching groups changed event.
 */
public class MatchingGroupsEvent implements Serializable {
    /**
     * Make sonar happy.
     */
    private static final long serialVersionUID = -9113047037837132694L;
    /**
     * The entity names.
     */
    private final Collection<String> entityNames;
    /**
     * Constructor.
     * @param entityNames the names to reload groups for
     */
    public MatchingGroupsEvent(Collection<String> entityNames) {
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

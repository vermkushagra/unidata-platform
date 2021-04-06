package com.unidata.mdm.backend.notification.notifiers;

import java.util.Collection;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

/**
 * @author Mikhail Mikhailov
 * Matching groups change notifier.
 */
public interface MatchingGroupsChangesNotifier extends AfterContextRefresh {
    /**
     * Notify neighborhood about groups changes.
     * @param entityNames the entity names to reload groups for
     */
    void notifyMatchingGroupsChanged(Collection<String> entityNames);
}

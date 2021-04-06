package com.unidata.mdm.backend.notification.notifiers;

import java.util.Collection;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

/**
 * @author Mikhail Mikhailov
 * Matching rules change notifier.
 */
public interface MatchingRulesChangesNotifier extends AfterContextRefresh {
    /**
     * Notify neighborhood about rules changes.
     * @param entityNames the entity names to reload rules for
     */
    void notifyMatchingRulesChanged(Collection<String> entityNames);
}

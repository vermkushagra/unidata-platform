package com.unidata.mdm.backend.notification.notifiers;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

/**
 * @author Mikhail Mikhailov
 * WF assignments notifier.
 */
public interface WorkflowAssignmentsNotifier extends AfterContextRefresh {
    /**
     * Trigger workflow assignments re-read ob other nodes.
     */
    void notifyAssignmetsChanged();
}

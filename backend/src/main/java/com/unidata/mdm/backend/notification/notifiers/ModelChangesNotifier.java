package com.unidata.mdm.backend.notification.notifiers;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

/**
 * Implementations of this interface responsible for notifying other nodes about changes in a model structure
 */
public interface ModelChangesNotifier extends AfterContextRefresh {

    /**
     * Notify other nodes about delete model
     *
     * @param context contains all information about deleted model.
     */
    void notifyOtherNodesAboutDeleteModel(DeleteModelRequestContext context);

    /**
     * Notify other nodes about upsert model
     *
     * @param context contains all information about upserted model.
     */
    void notifyOtherNodesAboutUpsertModel(UpdateModelRequestContext context);
}

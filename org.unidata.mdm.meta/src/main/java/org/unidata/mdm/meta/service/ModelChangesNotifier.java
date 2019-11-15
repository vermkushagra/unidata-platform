package org.unidata.mdm.meta.service;

import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;

/**
 * Implementations of this interface responsible for notifying other nodes about changes in a model structure
 */
public interface ModelChangesNotifier {
    /**
     * Notify other nodes about delete model
     *
     * @param context contains all information about deleted model.
     */
    void fireModelUpdate(DeleteModelRequestContext context);

    /**
     * Notify other nodes about upsert model
     *
     * @param context contains all information about upserted model.
     */
    void fireModelUpdate(UpdateModelRequestContext context);
}

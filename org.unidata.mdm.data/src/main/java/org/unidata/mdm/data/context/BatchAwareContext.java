package org.unidata.mdm.data.context;

import org.unidata.mdm.system.context.BooleanFlagsContext;

/**
 * Batch aware context.
 * @author Mikhail Mikhailov on Nov 21, 2019
 */
public interface BatchAwareContext extends BooleanFlagsContext {
    /**
     * Adds batch awareness to a context.
     * @return true for batched operation, false otherwise
     */
    default boolean isBatchOperation() {
        return getFlag(DataContextFlags.FLAG_BATCH_OPERATION);
    }
}

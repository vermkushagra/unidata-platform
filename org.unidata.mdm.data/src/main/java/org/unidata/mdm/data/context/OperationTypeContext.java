package org.unidata.mdm.data.context;

import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;

/**
 * @author Mikhail Mikhailov
 * Operation type indicator.
 */
public interface OperationTypeContext extends StorageCapableContext {
    /**
     * The operation type.
     */
    StorageId SID_OPERATION_TYPE = new StorageId("OPERATION_TYPE");
    /**
     * Get operation type
     * @return action
     */
    default OperationType operationType() {
        return getFromStorage(SID_OPERATION_TYPE);
    }
    /**
     * Put operation type
     * @param operationType the action
     */
    default void operationType(OperationType operationType) {
        putToStorage(SID_OPERATION_TYPE, operationType);
    }
}

package com.unidata.mdm.backend.common.context;

/**
 * @author Mikhail Mikhailov
 * Storage ID marker.
 */
public interface ModelStorageSpecificContext {
    /**
     * Returns specific storate id for operation.
     * @return storage id
     */
    String getStorageId();
}

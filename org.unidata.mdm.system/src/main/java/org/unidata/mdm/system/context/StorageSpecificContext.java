package org.unidata.mdm.system.context;

/**
 * @author Mikhail Mikhailov
 * Storage ID marker.
 */
public interface StorageSpecificContext {
    /**
     * Returns specific storate id for operation.
     * @return storage id
     */
    String getStorageId();
}

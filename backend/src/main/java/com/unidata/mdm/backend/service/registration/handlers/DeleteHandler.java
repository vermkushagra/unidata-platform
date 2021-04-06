package com.unidata.mdm.backend.service.registration.handlers;


import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

/**
 * Responsible for executing delete logic depend on keys
 *
 * @param <D> - key of deleting entity
 * @param <L> - key of linking entity
 */
public interface DeleteHandler<D extends UniqueRegistryKey, L extends UniqueRegistryKey> {

    /**
     * @param removingKey - removing key
     * @param linkingKey  - linking key
     */
    void onDelete(D removingKey, L linkingKey);

    /**
     * @return removed type of key(linked with D and can be got from L instance)
     */
    UniqueRegistryKey.Type getRemovedEntityType();

    /**
     * @return linked type of key (linked with L and can be got from L instance)
     */
    UniqueRegistryKey.Type getLinkedEntityType();

}

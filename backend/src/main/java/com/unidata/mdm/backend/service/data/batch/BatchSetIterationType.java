package com.unidata.mdm.backend.service.data.batch;

/**
 * @author Mikhail Mikhailov
 * Type of batch set iteration.
 */
public enum BatchSetIterationType {
    /**
     * Origins pass for either records, classifiers or relations.
     */
    UPSERT_ORIGINS,
    /**
     * Etalons pass for either records, classifiers or relations.
     */
    UPSERT_ETALONS,
    /**
     * Delete DB data pass for either records, classifiers or relations.
     */
    DELETE_ORIGINS,
    /**
     * Delete index data pass for either records, classifiers or relations.
     */
    DELETE_ETALONS
}

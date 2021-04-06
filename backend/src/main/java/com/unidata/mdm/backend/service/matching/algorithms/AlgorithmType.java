package com.unidata.mdm.backend.service.matching.algorithms;

/**
 * @author Mikhail Mikhailov
 * Type of the algorithm.
 * The type prescribes, which field is used for indexing and also for other things.
 */
public enum AlgorithmType {
    /**
     * Exact with no variations.
     */
    EXACT_STRICT_MATCH(1),
    /**
     * Exact with null, matching everything.
     */
    EXACT_NULL_MATCH_EVERYTHING(2),
    /**
     * Exact with null, matching nothing.
     */
    EXACT_NULL_MATCH_NOTHING(3),
    /**
     * Inexact (normalized, with fixed length).
     */
    INEXACT_NORMALIZED_LENGTH(4),
    /**
     * Excluded value match nothing.
     */
    EXACT_EXCLUDED_VALUE_MATCH_NOTHING(5);
    /**
     * Constructor.
     * @param id algorithm id.
     */
    private AlgorithmType(int id) {
        this.id = id;
    }
    /**
     * Algorithm id.
     */
    private final int id;
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
}

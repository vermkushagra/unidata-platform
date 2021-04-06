package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Type of the record the data is actually of.
 */
public enum RecordType {
    /**
     * Main data.
     */
    DATA_RECORD,
    /**
     * Relation.
     */
    RELATION_RECORD,
    /**
     * Classifier.
     */
    CLASSIFIER_RECORD
}

package com.unidata.mdm.backend.common.keys;

/**
 * @author Mikhail Mikhailov
 * Keys of data of some sort.
 */
public interface Keys {
    /**
     * @author Mikhail Mikhailov
     * Type of the keys.
     */
    public enum KeysType {
        /**
         * Record keys.
         */
        RECORD_KEYS,
        /**
         * Relation keys.
         */
        RELATION_KEYS,
        /**
         * Classifier data keys.
         */
        CLASSIFIER_KEYS
    }
    /**
     * Gets the type of this keys.
     * @return type
     */
    KeysType getType();
}

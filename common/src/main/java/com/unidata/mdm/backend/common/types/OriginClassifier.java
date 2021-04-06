package com.unidata.mdm.backend.common.types;
/**
 * @author Mikhail Mikhailov
 * Classifier entry for origin records.
 */
public interface OriginClassifier extends DataRecord {
    /**
     * Get the classifier info section.
     * @return info section or null
     */
    OriginClassifierInfoSection getInfoSection();
}

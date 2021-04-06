package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Classifier entry for etalon records.
 */
public interface EtalonClassifier extends DataRecord {
    /**
     * Gets etalon classifier info section.
     * @return info section or null
     */
    EtalonClassifierInfoSection getInfoSection();
}

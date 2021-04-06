package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.types.EtalonClassifier;

/**
 * @author Mikhail Mikhailov
 * Upsert classifiers DTO.
 */
public class UpsertClassifierDTO implements ClassifierDTO, EtalonClassifierDTO {

    /**
     * The keys.
     */
    private ClassifierKeys classifierKeys;
    /**
     * Etalon classifier record.
     */
    private EtalonClassifier etalon;
    /**
     * Constructor.
     */
    public UpsertClassifierDTO() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonClassifier getEtalon() {
        return etalon;
    }
    /**
     * @param classifier the classifier to set
     */
    public void setEtalon(EtalonClassifier classifier) {
        this.etalon = classifier;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeys getClassifierKeys() {
        return classifierKeys;
    }
    /**
     * @param classifierKeys the classifierKeys to set
     */
    public void setClassifierKeys(ClassifierKeys classifierKeys) {
        this.classifierKeys = classifierKeys;
    }
}

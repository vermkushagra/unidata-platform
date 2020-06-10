package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.UpsertAction;

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
     * Upsert action.
     */
    private UpsertAction action;
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
    /**
     * @return the action
     */
    public UpsertAction getAction() {
        return action;
    }
    /**
     * @param action the action to set
     */
    public void setAction(UpsertAction action) {
        this.action = action;
    }
}

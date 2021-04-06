package com.unidata.mdm.backend.service.job.importJob.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.RecordStatus;

public class ImportRecordSet {

    /**
     * The data of the main record to import.
     */
    private DataRecord data;
    /**
     * Classifiers to import.
     */
    private Collection<OriginClassifier> classifiers;
    /**
     * The origin key.
     */
    private OriginKey originKey;
    /**
     * Etalon key.
     */
    private EtalonKey etalonKey;
    /**
     * The status to set.
     */
    private RecordStatus status;
    /**
     * Valid from field.
     */
    private Date validFrom;
    /**
     * Valid to field.
     */
    private Date validTo;
    /**
     * Import row num
     */
    private int importRowNum;
    /**
     * Constructor.
     */
    public ImportRecordSet() {
        super();
    }

    /**
     * Constructor.
     * @param data the data
     */
    public ImportRecordSet(DataRecord data) {
        this.data = data;
    }

    @Nonnull
    public Collection<OriginClassifier> getClassifiers() {
        if(classifiers == null){
            classifiers =  new ArrayList<>();
        }
        return classifiers;
    }

    /**
     * @return the data
     */
    public DataRecord getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(DataRecord data) {
        this.data = data;
    }

    /**
     * @return the originKey
     */
    public OriginKey getOriginKey() {
        return originKey;
    }

    /**
     * @param originKey the originKey to set
     */
    public void setOriginKey(OriginKey originKey) {
        this.originKey = originKey;
    }

    /**
     * @return the etalonKey
     */
    public EtalonKey getEtalonKey() {
        return etalonKey;
    }

    /**
     * @param etalonKey the etalonKey to set
     */
    public void setEtalonKey(EtalonKey etalonKey) {
        this.etalonKey = etalonKey;
    }

    /**
     * @return the status
     */
    public RecordStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    /**
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * @param importRowNum - import row number
     */
    public void setImportRowNum(int importRowNum) {
        this.importRowNum = importRowNum;
    }

    /**
     * @return import row number
     */
    public int getImportRowNum() {
        return importRowNum;
    }
}

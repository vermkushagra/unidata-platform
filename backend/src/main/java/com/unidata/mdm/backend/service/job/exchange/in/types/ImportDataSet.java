package com.unidata.mdm.backend.service.job.exchange.in.types;

import java.util.Date;

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Base class for import data set.
 */
public class ImportDataSet {
    /**
     * The data of the main record to import.
     */
    private DataRecord data;
    /**
     * Valid from field.
     */
    private Date validFrom;
    /**
     * Valid to field.
     */
    private Date validTo;
    /**
     * The status to set.
     */
    private RecordStatus status;
    /**
     * Import row num
     */
    private int importRowNum;
    /**
     * Constructor.
     * @param data the data
     */
    public ImportDataSet(DataRecord data) {
        this.data = data;
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
    /**
     * Is record or not.
     * @return true, if relation, false otherwise
     */
    public boolean isRelation() {
        return false;
    }
    /**
     * Is record or not.
     * @return true, if record, false otherwise
     */
    public boolean isRecord() {
        return false;
    }
}

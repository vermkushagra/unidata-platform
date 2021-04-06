package com.unidata.mdm.backend.service.job.importJob.types;

import java.util.Date;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Relation import.
 */
public class ImportRelationSet {

    /**
     * The data of the containment or relation itself to import.
     */
    private DataRecord data;
    /**
     * The origin key.
     */
    private OriginKey fromOriginKey;
    /**
     * Etalon key.
     */
    private EtalonKey fromEtalonKey;
    /**
     * The origin key.
     */
    private OriginKey toOriginKey;
    /**
     * Etalon key.
     */
    private EtalonKey toEtalonKey;
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
     * Relation name.
     */
    private String relationName;
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
     * @return the fromOriginKey
     */
    public OriginKey getFromOriginKey() {
        return fromOriginKey;
    }
    /**
     * @param fromOriginKey the fromOriginKey to set
     */
    public void setFromOriginKey(OriginKey fromOriginKey) {
        this.fromOriginKey = fromOriginKey;
    }
    /**
     * @return the fromEtalonKey
     */
    public EtalonKey getFromEtalonKey() {
        return fromEtalonKey;
    }
    /**
     * @param fromEtalonKey the fromEtalonKey to set
     */
    public void setFromEtalonKey(EtalonKey fromEtalonKey) {
        this.fromEtalonKey = fromEtalonKey;
    }
    /**
     * @return the toOriginKey
     */
    public OriginKey getToOriginKey() {
        return toOriginKey;
    }
    /**
     * @param toOriginKey the toOriginKey to set
     */
    public void setToOriginKey(OriginKey toOriginKey) {
        this.toOriginKey = toOriginKey;
    }
    /**
     * @return the toEtalonKey
     */
    public EtalonKey getToEtalonKey() {
        return toEtalonKey;
    }
    /**
     * @param toEtalonKey the toEtalonKey to set
     */
    public void setToEtalonKey(EtalonKey toEtalonKey) {
        this.toEtalonKey = toEtalonKey;
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
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }
    /**
     * @param relationName the relationName to set
     */
    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
}

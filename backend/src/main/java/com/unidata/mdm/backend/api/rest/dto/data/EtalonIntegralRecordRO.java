/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 * Etalon integral record REST type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtalonIntegralRecordRO extends AbstractIntegralRecordRO {

    /**
     * Etalon id of the RELATION.
     */
    private String etalonId;
    /**
     * Containment etalon record.
     */
    private EtalonRecordRO etalonRecord;

    /**
     * Constructor.
     */
    public EtalonIntegralRecordRO() {
        super();
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    /**
     * @return the etalonRecord
     */
    public EtalonRecordRO getEtalonRecord() {
        return etalonRecord;
    }

    /**
     * @param etalonRecord the etalonRecord to set
     */
    public void setEtalonRecord(EtalonRecordRO etalonRecord) {
        this.etalonRecord = etalonRecord;
    }
}

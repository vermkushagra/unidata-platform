/**
 *
 */
package com.unidata.mdm.backend.api.rest.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 * Origin integral record REST type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OriginIntegralRecordRO extends AbstractIntegralRecordRO {

    /**
     * Etalon id of the RELATION.
     */
    private String oiginId;
    /**
     * Containment origin record.
     */
    private OriginRecordRO originRecord;
    /**
     * Revision of the to record.
     */
    private int revision;
    /**
     * Constructor.
     */
    public OriginIntegralRecordRO() {
        super();
    }

    /**
     * @return the oiginId
     */
    public String getOiginId() {
        return oiginId;
    }

    /**
     * @param oiginId the oiginId to set
     */
    public void setOiginId(String oiginId) {
        this.oiginId = oiginId;
    }

    /**
     * @return the originRecord
     */
    public OriginRecordRO getOriginRecord() {
        return originRecord;
    }

    /**
     * @param originRecord the originRecord to set
     */
    public void setOriginRecord(OriginRecordRO originRecord) {
        this.originRecord = originRecord;
    }

    /**
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(int revision) {
        this.revision = revision;
    }

}

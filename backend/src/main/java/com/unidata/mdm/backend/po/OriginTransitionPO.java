/**
 *
 */
package com.unidata.mdm.backend.po;


/**
 * @author Mikhail Mikhailov
 * Origins transitions log
 */
public class OriginTransitionPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "origins_transitions";
    /**
     * Etalon transition point id.
     */
    public static final String FIELD_ETALON_TRANSITION_ID = "etalon_transition_id";
    /**
     * Origin id.
     */
    public static final String FIELD_ORIGIN_ID = "origin_id";
    /**
     * Etalon merge point id.
     */
    private String etalonTransitionId;
    /**
     * Origin id.
     */
    private String originId;
    /**
     * @return the etalonsMergePointsId
     */
    public String getEtalonTransitionId() {
        return etalonTransitionId;
    }

    /**
     * @param etalonsMergePointsId the etalonsMergePointsId to set
     */
    public void setEtalonTransitionId(String etalonsMergePointsId) {
        this.etalonTransitionId = etalonsMergePointsId;
    }

    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * @param originId the originId to set
     */
    public void setOriginId(String originId) {
        this.originId = originId;
    }
}

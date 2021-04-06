/**
 *
 */
package com.unidata.mdm.backend.po;


/**
 * @author Mikhail Mikhailov
 * Merge duplicates record for transition log.
 */
public class DuplicatePO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "duplicates";
    /**
     * Transition id.
     */
    public static final String FIELD_TRANSITION_ID = "etalon_transition_id";
    /**
     * Duplicate id.
     */
    public static final String FIELD_DUPLICATE_ID = "duplicate_id";
    /**
     * Merge type.
     */
    public static final String FIELD_IS_AUTO = "is_auto";
    /**
     * Etalon merge point id.
     */
    private String etalonTransitionId;
    /**
     * Origin id.
     */
    private String duplicateId;
    /**
     * Merge type.
     */
    private boolean auto;
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
    public String getDuplicateId() {
        return duplicateId;
    }

    /**
     * @param originId the originId to set
     */
    public void setDuplicateId(String originId) {
        this.duplicateId = originId;
    }


    /**
     * @return the auto
     */
    public boolean isAuto() {
        return auto;
    }


    /**
     * @param auto the auto to set
     */
    public void setAuto(boolean auto) {
        this.auto = auto;
    }

}

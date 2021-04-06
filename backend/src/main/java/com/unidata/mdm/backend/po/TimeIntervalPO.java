/**
 *
 */
package com.unidata.mdm.backend.po;

import java.util.Date;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Time interval on the contributing time line.
 */
public class TimeIntervalPO extends AbstractPO {

    /**
     * Records Time line function name.
     */
    public static final String FUNCTION_NAME_RECORDS_TIMELINE = "fetch_records_timeline_intervals";
    /**
     * Etalon boundary function name.
     */
    public static final String FUNCTION_NAME_RECORD_ETALON_BOUNDARY = "fetch_records_etalon_boundary";
    /**
     * Relations time line function name.
     */
    public static final String FUNCTION_NAME_RELATIONS_TIMELINE = "fetch_relations_timeline_intervals";
    /**
     * Relations etalon boundary function name.
     */
    public static final String FUNCTION_NAME_RELATION_ETALON_BOUNDARY = "fetch_relations_etalon_boundary";
    /**
     * Relation etalon id.
     */
    public static final String FIELD_RELATION_ETALON_ID = "relation_etalon_id";
    /**
     * Record etalon id.
     */
    public static final String FIELD_RECORD_ETALON_ID = "record_etalon_id";
    /**
     * Relation name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Period id.
     */
    public static final String FIELD_PERIOD_ID = "period_id";
    /**
     * Valid from.
     */
    public static final String FIELD_VALID_FROM = "vf";
    /**
     * Valid to.
     */
    public static final String FIELD_VALID_TO = "vt";
    /**
     * Contributors.
     */
    public static final String FIELD_CONTRIBUTORS = "contributors";
    /**
     * Status.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Approval state.
     */
    public static final String FIELD_STATE = "approval";
    /**
     * Etalon Gsn
     */
    public static final String FIELD_ETALON_GSN = "etalon_gsn";
    /**
     * Relation etalon id.
     */
    private String relationEtalonId;
    /**
     * Record etalon id.
     */
    private String recordEtalonId;
    /**
     * The period id.
     */
    private long periodId;
    /**
     * From date.
     */
    private Date from;
    /**
     * To date.
     */
    private Date to;
    /**
     * Entity or relation name. Set for boundary queries only!.
     */
    private String name;
    /**
     * Etalon status. Set for boundary queries only!.
     */
    private RecordStatus status;
    /**
     * Etalon approval state. Set for boundary queries only.
     */
    private ApprovalState state;
    /**
     * Etalon Global sequence number.
     */
    private long etalonGsn;
    /**
     * Contributors - source system and its revision.
     */
    private ContributorPO[] contributors = null;

    /**
     * Constructor.
     */
    public TimeIntervalPO() {
        super();
    }

    /**
     * @return the relationEtalonId
     */
    public String getRelationEtalonId() {
        return relationEtalonId;
    }

    /**
     * @param relationEtalonId the relationEtalonId to set
     */
    public void setRelationEtalonId(String relationEtalonId) {
        this.relationEtalonId = relationEtalonId;
    }

    /**
     * @return the recordEtalonId
     */
    public String getRecordEtalonId() {
        return recordEtalonId;
    }

    /**
     * @param recordEtalonId the recordEtalonId to set
     */
    public void setRecordEtalonId(String recordEtalonId) {
        this.recordEtalonId = recordEtalonId;
    }

    /**
     * @return the periodId
     */
    public long getPeriodId() {
        return periodId;
    }

    /**
     * @param periodId the periodId to set
     */
    public void setPeriodId(long periodId) {
        this.periodId = periodId;
    }

    /**
     * @return the from
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public Date getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Date to) {
        this.to = to;
    }

    /**
     * @return the contributors
     */
    public ContributorPO[] getContributors() {
        return contributors;
    }

    /**
     * @param contributors the contributors to set
     */
    public void setContributors(ContributorPO[] contributors) {
        this.contributors = contributors;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the state
     */
    public ApprovalState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(ApprovalState state) {
        this.state = state;
    }

    /**
     * @return the etalonGsn
     */
    public long getEtalonGsn() {
        return etalonGsn;
    }

    /**
     * @param etalonGsn the etalonGsn to set
     */
    public void setEtalonGsn(long etalonGsn) {
        this.etalonGsn = etalonGsn;
    }
}

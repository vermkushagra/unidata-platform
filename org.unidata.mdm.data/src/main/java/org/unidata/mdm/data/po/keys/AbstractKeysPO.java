package org.unidata.mdm.data.po.keys;

import org.unidata.mdm.core.po.AbstractObjectPO;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Base class for keys.
 * Common part:
 * shard integer,
 * lsn bigint,
 * id uuid,
 * name varchar(256),
 * status record_status,
 * state approval_state,
 * approved boolean,
 * create_date timestamptz,
 * created_by varchar(256),
 * update_date timestamptz,
 * updated_by varchar(256),
 */
public class AbstractKeysPO extends AbstractObjectPO {
    /**
     * Etalon ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * Etalon status {@link RecordStatus}.
     */
    public static final String FIELD_STATUS = "status";
    /**
     * Etalon state {@link ApprovalState}.
     */
    public static final String FIELD_STATE = "state";
    /**
     * Etalon LSN.
     */
    public static final String FIELD_LSN = "lsn";
    /**
     * Etalon name.
     */
    public static final String FIELD_NAME = "name";
    /**
     * Whether this record has approved revisions or not.
     */
    public static final String FIELD_APPROVED = "approved";
    /**
     * Record's shard.
     */
    public static final String FIELD_SHARD = "shard";
    /**
     * The shard where the record reside.
     */
    private int shard;
    /**
     * Etalon local sequence number (unique within a shard).
     */
    private long lsn;
    /**
     * Record etalon id.
     */
    private String id;
    /**
     * Type name as set by entity definition.
     */
    private String name;
    /**
     * Etalon status of the record.
     */
    private RecordStatus status;
    /**
     * Etalon approval state.
     */
    private ApprovalState state;
    /**
     * Record was already published.
     */
    private Boolean approved;

    /**
     * Constructor.
     */
    public AbstractKeysPO() {
        super();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String etalonId) {
        this.id = etalonId;
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
    public void setName(String etalonName) {
        this.name = etalonName;
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
    public void setStatus(RecordStatus etalonStatus) {
        this.status = etalonStatus;
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
    public void setState(ApprovalState etalonState) {
        this.state = etalonState;
    }

    /**
     * @return the isPublished
     */
    public Boolean isApproved() {
        return approved;
    }

    /**
     * @param approved the isPublished to set
     */
    public void setApproved(Boolean hasApprovedRevisions) {
        this.approved = hasApprovedRevisions;
    }

    /**
     * @return the lsn
     */
    public long getLsn() {
        return lsn;
    }

    /**
     * @param lsn the lsn to set
     */
    public void setLsn(long etalonGsn) {
        this.lsn = etalonGsn;
    }

    /**
     * @return the shard
     */
    public int getShard() {
        return shard;
    }

    /**
     * @param shard the shard to set
     */
    public void setShard(int shard) {
        this.shard = shard;
    }

}

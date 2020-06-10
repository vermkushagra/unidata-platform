package com.unidata.mdm.backend.common.data;

import java.util.Date;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.TimeIntervalContributorInfo;

/**
 * @author mikhail
 * Time line contributor holder.
 */
public class TimeIntervalContributorHolder
    implements CalculableHolder<TimeIntervalContributorInfo> {

    /**
     * The value.
     */
    private final TimeIntervalContributorInfo value;

    /**
     * Constructor.
     * @param v the value
     */
    public TimeIntervalContributorHolder(TimeIntervalContributorInfo v) {
        super();
        this.value = v;
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getValue()
     */
    @Override
    public TimeIntervalContributorInfo getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        // Not currently supported, but may be of use later
        return null;
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getTypeName()
     */
    @Override
    public String getTypeName() {
        // Not currently supported, but may be of use later
        return null;
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getSourceSystem()
     */
    @Override
    public String getSourceSystem() {
        return value.getSourceSystem();
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getStatus()
     */
    @Override
    public RecordStatus getStatus() {
        return value.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApprovalState getApproval() {
        return value.getApproval();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastUpdate() {
        return value.getCreateDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRevision() {
        return value.getRevision();
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getCalculableType()
     */
    @Override
    public CalculableType getCalculableType() {
        return CalculableType.INFO;
    }
}

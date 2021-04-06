package com.unidata.mdm.backend.service.data.driver;

import java.util.Date;

import com.unidata.mdm.backend.common.dto.ContributorDTO;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author mikhail
 * Time line contributor holder.
 */
public class TimeIntervalContributorHolder
    implements CalculableHolder<ContributorDTO> {

    /**
     * The value.
     */
    private final ContributorDTO value;

    /**
     * Constructor.
     * @param v the value
     */
    public TimeIntervalContributorHolder(ContributorDTO v) {
        super();
        this.value = v;
    }

    /**
     * @see com.unidata.mdm.backend.service.data.driver.CalculableHolder#getValue()
     */
    @Override
    public ContributorDTO getValue() {
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
     * @see com.unidata.mdm.backend.service.data.driver.CalculableHolder#getTypeName()
     */
    @Override
    public String getTypeName() {
        return value == null ? null : value.getTypeName();
    }

    /**
     * @see com.unidata.mdm.backend.service.data.driver.CalculableHolder#getSourceSystem()
     */
    @Override
    public String getSourceSystem() {
        return value == null ? null : value.getSourceSystem();
    }

    /**
     * @see com.unidata.mdm.backend.service.data.driver.CalculableHolder#getStatus()
     */
    @Override
    public RecordStatus getStatus() {
        return value == null ? null : value.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastUpdate() {
        return value == null ? null : value.getLastUpdate();
    }

    /**
     * @see com.unidata.mdm.backend.service.data.driver.CalculableHolder#getCalculableType()
     */
    @Override
    public CalculableType getCalculableType() {
        return CalculableType.TIME_INTERVAL;
    }

}

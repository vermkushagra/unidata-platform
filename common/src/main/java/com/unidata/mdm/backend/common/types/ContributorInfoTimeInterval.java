package com.unidata.mdm.backend.common.types;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.data.TimeIntervalContributorHolder;

/**
 * @author Mikhail Mikhailov
 * Time interval for records timelines.
 */
public class ContributorInfoTimeInterval extends TimeInterval<TimeIntervalContributorInfo> {
    /**
     * Constructor.
     */
    public ContributorInfoTimeInterval() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalType getType() {
        return TimeIntervalType.INFO;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(TimeIntervalContributorInfo v) {
        super.add(CalculableHolder.of(v));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CalculableHolder<TimeIntervalContributorInfo>[] toArray() {
        return contributors.stream().toArray(size -> new TimeIntervalContributorHolder[size]);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalContributorInfo[] toValueArray() {
        return contributors.stream().map(CalculableHolder::getValue).toArray(size -> new TimeIntervalContributorInfo[size]);
    }
    /**
     * @param validFrom the validFrom to set
     */
    public ContributorInfoTimeInterval withValidFrom(Date validFrom) {
        setValidFrom(validFrom);
        return this;
    }
    /**
     * @param validTo the validTo to set
     */
    public ContributorInfoTimeInterval withValidTo(Date validTo) {
        setValidTo(validTo);
        return this;
    }
    /**
     * @param active the active to set
     */
    public ContributorInfoTimeInterval withActive(boolean active) {
        setActive(active);
        return this;
    }
    /**
     * @param records the records to set
     */
    public ContributorInfoTimeInterval withContributors(TimeIntervalContributorInfo... records) {
        for (int i = 0; records != null && i < records.length; i++) {
            add(records[i]);
        }
        return this;
    }
    /**
     * @param records the records to set
     */
    public ContributorInfoTimeInterval withContributors(Collection<CalculableHolder<TimeIntervalContributorInfo>> records) {
        if (CollectionUtils.isNotEmpty(records)) {
            addAll(records);
        }
        return this;
    }
}

package com.unidata.mdm.backend.common.types;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.data.RelationRecordHolder;

/**
 * @author Mikhail Mikhailov
 * Time interval for relations timelines.
 */
public class RelationTimeInterval extends TimeInterval<OriginRelation> {
    /**
     * Constructor.
     */
    public RelationTimeInterval() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalType getType() {
        return TimeIntervalType.RELATION;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(OriginRelation v) {
        super.add(CalculableHolder.of(v));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CalculableHolder<OriginRelation>[] toArray() {
        return contributors.stream().toArray(size -> new RelationRecordHolder[size]);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRelation[] toValueArray() {
        return contributors.stream().map(CalculableHolder::getValue).toArray(size -> new OriginRelation[size]);
    }
    /**
     * @param validFrom the validFrom to set
     */
    public RelationTimeInterval withValidFrom(Date validFrom) {
        setValidFrom(validFrom);
        return this;
    }
    /**
     * @param validTo the validTo to set
     */
    public RelationTimeInterval withValidTo(Date validTo) {
        setValidTo(validTo);
        return this;
    }
    /**
     * @param active the active to set
     */
    public RelationTimeInterval withActive(boolean active) {
        setActive(active);
        return this;
    }
    /**
     * @param records the records to set
     */
    public RelationTimeInterval withContributors(OriginRelation... records) {
        for (int i = 0; records != null && i < records.length; i++) {
            add(records[i]);
        }
        return this;
    }
    /**
     * @param records the records to set
     */
    public RelationTimeInterval withContributors(Collection<CalculableHolder<OriginRelation>> records) {
        if (CollectionUtils.isNotEmpty(records)) {
            addAll(records);
        }
        return this;
    }
    /**
     * Sets etalon record.
     * @param etalon the etalon record
     * @return etalon
     */
    public RelationTimeInterval withCalculationResult(EtalonRelation etalon) {
        setCalculationResult(etalon);
        return this;
    }
}

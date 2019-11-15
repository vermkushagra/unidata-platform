package org.unidata.mdm.data.type.timeline;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.timeline.impl.AbstractMutableTimeInterval;
import org.unidata.mdm.data.type.calculables.impl.RelationRecordHolder;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.OriginRelation;

/**
 * @author Mikhail Mikhailov
 * Time interval for relation data timelines.
 */
public class RelationTimeInterval extends AbstractMutableTimeInterval<OriginRelation> {
    /**
     * Constructor.
     */
    public RelationTimeInterval(OriginRelation contributor) {
        this(Collections.singletonList(new RelationRecordHolder(contributor)));
    }

    /**
     * Constructor.
     */
    public RelationTimeInterval(Collection<CalculableHolder<OriginRelation>> contributors) {
        this(null, null, contributors);
    }

    /**
     * Constructor.
     */
    public RelationTimeInterval(Date validFrom, Date validTo, Collection<CalculableHolder<OriginRelation>> contributors) {
        super();
        super.validFrom = validFrom;
        super.validTo = validTo;
        super.push(contributors);
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
     * @param pending the active to set
     */
    public RelationTimeInterval withPending(boolean pending) {
        setPending(pending);
        return this;
    }

    /**
     * Sets etalon record.
     *
     * @param etalon the etalon record
     * @return etalon
     */
    public RelationTimeInterval withCalculationResult(EtalonRelation etalon) {
        setCalculationResult(etalon);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculableHolder<OriginRelation>[] toArray() {
        List<CalculableHolder<OriginRelation>> calculables = toCalculables();
        return calculables.stream().toArray(size -> new RelationRecordHolder[size]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRelation[] toValueArray() {
        List<CalculableHolder<OriginRelation>> calculables = toCalculables();
        return calculables.stream().map(CalculableHolder::getValue).toArray(size -> new OriginRelation[size]);
    }
}

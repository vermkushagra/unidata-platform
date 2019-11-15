package org.unidata.mdm.data.type.timeline;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.timeline.impl.AbstractMutableTimeInterval;
import org.unidata.mdm.data.type.calculables.impl.DataRecordHolder;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;


/**
 * @author Mikhail Mikhailov
 * Time interval for records timelines.
 */
public class RecordTimeInterval extends AbstractMutableTimeInterval<OriginRecord> {
    /**
     * Constructor.
     */
    public RecordTimeInterval(OriginRecord contributor) {
        this(Collections.singletonList(new DataRecordHolder(contributor)));
    }

    /**
     * Constructor.
     */
    public RecordTimeInterval(Collection<CalculableHolder<OriginRecord>> contributors) {
        this(null, null, contributors);
    }

    /**
     * Constructor.
     */
    public RecordTimeInterval(Date validFrom, Date validTo, Collection<CalculableHolder<OriginRecord>> contributors) {
        super();
        super.validFrom = validFrom;
        super.validTo = validTo;
        super.push(contributors);
    }

    /**
     * @param validFrom the validFrom to set
     */
    public RecordTimeInterval withValidFrom(Date validFrom) {
        setValidFrom(validFrom);
        return this;
    }

    /**
     * @param validTo the validTo to set
     */
    public RecordTimeInterval withValidTo(Date validTo) {
        setValidTo(validTo);
        return this;
    }

    /**
     * @param active the active to set
     */
    public RecordTimeInterval withActive(boolean active) {
        setActive(active);
        return this;
    }

    /**
     * @param pending the active to set
     */
    public RecordTimeInterval withPending(boolean pending) {
        setPending(pending);
        return this;
    }

    /**
     * Sets etalon record.
     *
     * @param etalon the etalon record
     * @return etalon
     */
    public RecordTimeInterval withCalculationResult(EtalonRecord etalon) {
        setCalculationResult(etalon);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculableHolder<OriginRecord>[] toArray() {
        List<CalculableHolder<OriginRecord>> calculables = toCalculables();
        return calculables.stream().toArray(size -> new DataRecordHolder[size]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRecord[] toValueArray() {
        List<CalculableHolder<OriginRecord>> calculables = toCalculables();
        return calculables.stream().map(CalculableHolder::getValue).toArray(size -> new OriginRecord[size]);
    }
}

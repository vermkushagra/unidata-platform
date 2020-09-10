/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.common.types;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.data.DataRecordHolder;

/**
 * @author Mikhail Mikhailov
 * Time interval for records timelines.
 */
public class RecordTimeInterval extends TimeInterval<OriginRecord> {
    /**
     * Constructor.
     */
    public RecordTimeInterval() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalType getType() {
        return TimeIntervalType.RECORD;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void add(OriginRecord v) {
        super.add(CalculableHolder.of(v));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public CalculableHolder<OriginRecord>[] toArray() {
        return contributors.stream().toArray(size -> new DataRecordHolder[size]);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRecord[] toValueArray() {
        return contributors.stream().map(CalculableHolder::getValue).toArray(size -> new OriginRecord[size]);
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
     * @param records the records to set
     */
    public RecordTimeInterval withContributors(OriginRecord... records) {
        for (int i = 0; records != null && i < records.length; i++) {
            add(records[i]);
        }
        return this;
    }
    /**
     * @param records the records to set
     */
    public RecordTimeInterval withContributors(Collection<CalculableHolder<OriginRecord>> records) {
        if (CollectionUtils.isNotEmpty(records)) {
            super.addAll(records);
        }
        return this;
    }
    /**
     * Sets etalon record.
     * @param etalon the etalon record
     * @return etalon
     */
    public RecordTimeInterval withCalculationResult(EtalonRecord etalon) {
        setCalculationResult(etalon);
        return this;
    }
}

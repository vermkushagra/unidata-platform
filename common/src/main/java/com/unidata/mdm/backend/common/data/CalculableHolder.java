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

package com.unidata.mdm.backend.common.data;

import java.util.Date;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Calculable;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.TimeIntervalContributorInfo;

/**
 * @author Mikhail Mikhailov
 * Holder for calculation objects.
 */
public interface CalculableHolder<T> {
    /**
     * @return the relation
     */
    T getValue();
    /**
     * @return the name
     */
    String getTypeName();
    /**
     * @return the sourceSystem
     */
    String getSourceSystem();
    /**
     * @return the external id (if present)
     */
    String getExternalId();
    /**
     * @return the status
     */
    RecordStatus getStatus();
    /**
     * @return the approval
     */
    ApprovalState getApproval();
    /**
     * @return the calculable type
     */
    CalculableType getCalculableType();
    /**
     * @return the last update date
     */
    Date getLastUpdate();
    /**
     * Gets the revision of the object hold, if applicable.
     * @return revision (&gt; 0), -1 if not applicable or 0 for new objects
     */
    int getRevision();
    /**
     * CH of an origin data record.
     * @param record the record
     * @return CH
     */
    @SuppressWarnings("unchecked")
    static<T extends DataRecord> CalculableHolder<T> of(OriginRecord record) {
        return (CalculableHolder<T>) new DataRecordHolder(record);
    }
    /**
     * CH of an origin classifier record.
     * @param record the record
     * @return CH
     */
    @SuppressWarnings("unchecked")
    static<T extends DataRecord> CalculableHolder<T> of(OriginClassifier record) {
        return (CalculableHolder<T>) new ClassifierRecordHolder(record);
    }
    /**
     * CH of an origin relation record.
     * @param record the record
     * @return CH
     */
    @SuppressWarnings("unchecked")
    static<T extends DataRecord> CalculableHolder<T> of(OriginRelation record) {
        return (CalculableHolder<T>) new RelationRecordHolder(record);
    }
    /**
     * CH of a time interval contributor info record.
     * @param record the record
     * @return CH
     */
    @SuppressWarnings("unchecked")
    static<T extends Calculable> CalculableHolder<T> of(TimeIntervalContributorInfo record) {
        return (CalculableHolder<T>) new TimeIntervalContributorHolder(record);
    }
    /**
     * CH of an attribute.
     * @param value attribute value
     * @param path attribute path
     * @param source data record
     * @return CH
     */
    @SuppressWarnings("unchecked")
    static<T> CalculableHolder<T> of(Attribute value, String path, CalculableHolder<DataRecord> source) {
        return (CalculableHolder<T>) new RecordAttributeHolder(
                value, path, source.getSourceSystem(), source.getExternalId(), source.getLastUpdate(), source.getRevision());
    }
}

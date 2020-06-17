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

import com.unidata.mdm.backend.common.dto.ContributorDTO;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Holder for calculation objects.
 */
public interface CalculableHolder<T> {
    /**
     * @return the relation
     */
    public T getValue();
    /**
     * @return the name
     */
    public String getTypeName();
    /**
     * @return the sourceSystem
     */
    public String getSourceSystem();
    /**
     * @return the external id (if present)
     */
    public String getExternalId();
    /**
     * @return the status
     */
    public RecordStatus getStatus();
    /**
     * @return the calculable type
     */
    public CalculableType getCalculableType();
    /**
     * @return the last update date
     */
    public Date getLastUpdate();
    /**
     * Gets the revision of the object hold, if applicable.
     * @return revision (> 0), -1 if not applicable or 0 for new objects
     */
    public int getRevision();

    @SuppressWarnings("unchecked")
    public static<T extends DataRecord> CalculableHolder<T> of(OriginRecord record) {
        return (CalculableHolder<T>) new DataRecordHolder(record);
    }

    @SuppressWarnings("unchecked")
    public static<T extends DataRecord> CalculableHolder<T> of(OriginClassifier record) {
        return (CalculableHolder<T>) new ClassifierRecordHolder(record);
    }

    @SuppressWarnings("unchecked")
    public static<T extends DataRecord> CalculableHolder<T> of(OriginRelation record) {
        return (CalculableHolder<T>) new RelationRecordHolder(record);
    }

    @SuppressWarnings("unchecked")
    public static<T> CalculableHolder<T> of(ContributorDTO record) {
        return (CalculableHolder<T>) new TimeIntervalContributorHolder(record);
    }

    @SuppressWarnings("unchecked")
    public static<T> CalculableHolder<T> of(Attribute value, String path, CalculableHolder<DataRecord> source) {
        return (CalculableHolder<T>) new RecordAttributeHolder(
                value, path, source.getSourceSystem(), source.getExternalId(), source.getLastUpdate(), source.getRevision());
    }
}

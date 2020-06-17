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

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author mikhail
 * Holder for {@link DataRecord} objects.
 */
public class DataRecordHolder
    implements CalculableHolder<OriginRecord> {
    /**
     * The record.
     */
    private final OriginRecord value;
    /**
     * Constructor.
     * @param data the data
     * @param name type name
     * @param sourceSystem the source system
     * @param externalId object's external id
     * @param status the status
     * @param lastUpdate the last update
     */
    public DataRecordHolder(OriginRecord data) {
        super();
        this.value = data;
    }
    /**
     * @return the value
     */
    @Override
    public OriginRecord getValue() {
        return value;
    }
    /**
     * @return the type name
     */
    @Override
    public String getTypeName() {
        return value.getInfoSection().getOriginKey().getEntityName();
    }
    /**
     * @return the source system
     */
    @Override
    public String getSourceSystem() {
        return value.getInfoSection().getOriginKey().getSourceSystem();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        return value.getInfoSection().getOriginKey().getExternalId();
    }
    /**
     * @return the status
     */
    @Override
    public RecordStatus getStatus() {
        return value.getInfoSection().getStatus();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastUpdate() {
        return value.getInfoSection().getUpdateDate();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getRevision() {
        return value.getInfoSection().getRevision();
    }
    /**
     * @return the calculable type
     */
    @Override
    public CalculableType getCalculableType() {
        return CalculableType.RECORD;
    }
}

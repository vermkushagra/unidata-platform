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

package org.unidata.mdm.data.type.calculables.impl;

import java.util.Date;

import org.unidata.mdm.core.type.calculables.impl.AbstractCalculableHolder;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.keys.OriginKey;
import org.unidata.mdm.data.type.data.OriginRecord;

/**
 * @author mikhail
 * Holder for {@link DataRecord} objects.
 */
public class DataRecordHolder extends AbstractCalculableHolder<OriginRecord> {
    /**
     * The record.
     */
    private final OriginRecord value;

    /**
     * Constructor.
     *
     * @param data the data
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
    public ApprovalState getApproval() {
        return value.getInfoSection().getApproval();
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
     * {@inheritDoc}
     */
    @Override
    public Date getValidFrom() {
        return value.getInfoSection().getValidFrom();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getValidTo() {
        return value.getInfoSection().getValidTo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnrichment() {
        return value.getInfoSection().getOriginKey().isEnrichment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginKey getOriginKey() {
        return value.getInfoSection().getOriginKey();
    }
}

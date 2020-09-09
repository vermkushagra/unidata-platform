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
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author mikhail
 *         Holder for {@link OriginClassifier} objects.
 */
public class ClassifierRecordHolder implements CalculableHolder<OriginClassifier> {

    /**
     * origin classifier record
     */
    private final OriginClassifier value;
    /**
     * Constructor
     *
     * @param value              - origin classifier record
     */
    public ClassifierRecordHolder(OriginClassifier value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginClassifier getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName() {
        return value.getInfoSection().getClassifierName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSourceSystem() {
        return value.getInfoSection().getClassifierSourceSystem();
    }

    /**
     * {@inheritDoc}
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
    public CalculableType getCalculableType() {
        return CalculableType.CLASSIFIER;
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
    public String getExternalId() {
        return value.getInfoSection().getRecordOriginKey().getExternalId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRevision() {
        return value.getInfoSection().getRevision();
    }
}

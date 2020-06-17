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
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author mikhail
 * Time line contributor holder.
 */
public class TimeIntervalContributorHolder
    implements CalculableHolder<ContributorDTO> {

    /**
     * The value.
     */
    private final ContributorDTO value;

    /**
     * Constructor.
     * @param v the value
     */
    public TimeIntervalContributorHolder(ContributorDTO v) {
        super();
        this.value = v;
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getValue()
     */
    @Override
    public ContributorDTO getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalId() {
        // Not currently supported, but may be of use later
        return null;
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getTypeName()
     */
    @Override
    public String getTypeName() {
        return value == null ? null : value.getTypeName();
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getSourceSystem()
     */
    @Override
    public String getSourceSystem() {
        return value == null ? null : value.getSourceSystem();
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getStatus()
     */
    @Override
    public RecordStatus getStatus() {
        return value == null ? null : value.getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getLastUpdate() {
        return value == null ? null : value.getLastUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRevision() {
        return value == null ? -1 : value.getRevision();
    }

    /**
     * @see com.unidata.mdm.backend.common.data.CalculableHolder#getCalculableType()
     */
    @Override
    public CalculableType getCalculableType() {
        return CalculableType.TIME_INTERVAL;
    }

}

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

package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;

/**
 * @author Mikhail Mikhailov
 * Origin record data container.
 */
public class OriginRecordImpl extends AbstractDataRecord implements OriginRecord {

    /**
     * Origin info section.
     */
    private OriginRecordInfoSection infoSection;

    /**
     * Constructor.
     */
    public OriginRecordImpl() {
        super();
    }

    /**
     * Constructor.
     * @param data the view to set
     */
    public OriginRecordImpl(DataRecord data) {
        super(data);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRecordInfoSection getInfoSection() {
        return infoSection;
    }

    /**
     * @param infoSection the infoSection to set
     */
    public void setInfoSection(OriginRecordInfoSection infoSection) {
        this.infoSection = infoSection;
    }

    /**
     * Fluent info section setter.
     * @param infoSection the info section to set
     * @return self
     */
    public OriginRecordImpl withInfoSection(OriginRecordInfoSection infoSection) {
        setInfoSection(infoSection);
        return this;
    }

    /**
     * Fluent data setter.
     * @param data the data to set
     * @return self
     */
    public OriginRecordImpl withDataRecord(DataRecord data) {
        internalSet(data);
        return this;
    }
}

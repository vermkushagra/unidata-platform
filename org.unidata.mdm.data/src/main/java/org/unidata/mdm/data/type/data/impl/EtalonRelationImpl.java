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

package org.unidata.mdm.data.type.data.impl;

import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.impl.AbstractDataRecord;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.EtalonRelationInfoSection;

/**
 * @author Mikhail Mikhailov
 * Etalon relation data container/ facade.
 */
public class EtalonRelationImpl extends AbstractDataRecord implements EtalonRelation {

    /**
     * Info section.
     */
    private EtalonRelationInfoSection infoSection;

    /**
     * Constructor.
     */
    public EtalonRelationImpl() {
        super();
    }

    /**
     * Constructor.
     * @param data
     */
    public EtalonRelationImpl(DataRecord data) {
        super(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRelationInfoSection getInfoSection() {
        return infoSection;
    }

    /**
     * @param infoSection the infoSection to set
     */
    public void setInfoSection(EtalonRelationInfoSection infoSection) {
        this.infoSection = infoSection;
    }

    /**
     * Fluent info section setter.
     * @param infoSection the info section to set
     * @return self
     */
    public EtalonRelationImpl withInfoSection(EtalonRelationInfoSection infoSection) {
        setInfoSection(infoSection);
        return this;
    }

    /**
     * Fluent data setter.
     * @param data the data to set
     * @return self
     */
    public EtalonRelationImpl withDataRecord(DataRecord data) {
        internalSet(data);
        return this;
    }
}

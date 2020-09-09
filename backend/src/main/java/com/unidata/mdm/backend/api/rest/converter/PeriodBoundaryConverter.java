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

/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.Objects;

import com.unidata.mdm.backend.api.rest.dto.meta.PeriodBoundaryDefinition;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.PeriodBoundaryDef;

/**
 * @author Mikhail Mikhailov
 * Validity period boundary converter.
 */
public class PeriodBoundaryConverter {

    /**
     * Constructor.
     */
    private PeriodBoundaryConverter() {
        super();
    }

    /**
     * The 'To' converting method.
     * @param source the source
     * @return REST target
     */
    public static PeriodBoundaryDefinition to(PeriodBoundaryDef source) {

        if (Objects.isNull(source)) {
            return null;
        }

        PeriodBoundaryDefinition target = new PeriodBoundaryDefinition();
        
        target.setEnd(ConvertUtils.date2LocalDateTime(JaxbUtils.xmlGregorianCalendarToDate(source.getEnd())));
        target.setStart(ConvertUtils.date2LocalDateTime(JaxbUtils.xmlGregorianCalendarToDate(source.getStart())));

        return target;
    }

    /**
     * The 'From' converting method.
     * @param source the source
     * @return XML target
     */
    public static PeriodBoundaryDef from(PeriodBoundaryDefinition source) {

        if (Objects.isNull(source)) {
            return null;
        }

        PeriodBoundaryDef target = JaxbUtils.getMetaObjectFactory().createPeriodBoundaryDef();
        target.setEnd(JaxbUtils.dateToXMGregorianCalendar(ConvertUtils.localDateTime2Date(source.getEnd())));
        target.setStart(JaxbUtils.dateToXMGregorianCalendar(ConvertUtils.localDateTime2Date(source.getStart())));

        return target;
    }
}

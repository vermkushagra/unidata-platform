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

package com.unidata.mdm.backend.service.data.listener;

import java.util.Date;
import java.util.Objects;

import com.unidata.mdm.backend.common.context.MutableValidityRangeContext;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;

public abstract class AbstractValidityRangeCheckExecutor <T extends MutableValidityRangeContext> {

    /**
     * Entry point.
     * @param t the context
     * @param factoryFrom factory from
     * @param factoryTo factory to
     */
    protected void execute(T t, Date factoryFrom, Date factoryTo) {

        Date from = ensureFrom(t.getValidFrom(), factoryFrom);
        Date to = ensureTo(t.getValidTo(), factoryTo);

        if(from != null && to != null && from.after(to)){
            throw new DataProcessingException("Upserted validity period incorrect",
                    ExceptionId.EX_DATA_VALIDITY_PERIOD_INCORRECT,
                    ValidityPeriodUtils.asString(t.getValidFrom()),
                    ValidityPeriodUtils.asString(t.getValidTo()));
        }

        // From changed.
        if (from != null && !from.equals(t.getValidFrom())) {
            t.setValidFrom(from);
        }

        // To changed.
        if (to != null && !to.equals(t.getValidTo())) {
            t.setValidTo(to);
        }
    }

    /**
     * Checks from boundary.
     * @param validFrom original validFrom value
     * @param  factoryValidFrom validFrom value from factory method
     * @return possibly adjusted date
     */
    protected Date ensureFrom(Date validFrom, Date factoryValidFrom) {

        // 1. Check boundaries, which might be defined on the entity directly
        if (Objects.nonNull(factoryValidFrom) && (validFrom == null || validFrom.before(factoryValidFrom))) {
            return factoryValidFrom;
        }

        // 2. Check against global settings
        if (Objects.nonNull(ValidityPeriodUtils.getGlobalValidityPeriodStart())
         && (validFrom == null || validFrom.before(ValidityPeriodUtils.getGlobalValidityPeriodStart()))) {
            return ValidityPeriodUtils.getGlobalValidityPeriodStart();
        }

        return validFrom;
    }

    /**
     * Checks to boundary.
     * @param validTo original validTo value
     * @param  factoryValidTo validTo value from factory method
     * @return possibly adjusted date
     */
    protected Date ensureTo(Date validTo, Date factoryValidTo) {

        // 1. Check boundaries, which might be defined on the entity directly
        if (Objects.nonNull(factoryValidTo) && (validTo == null || validTo.after(factoryValidTo))) {
            return factoryValidTo;
        }

        // 2. Check against global settings
        if (Objects.nonNull(ValidityPeriodUtils.getGlobalValidityPeriodEnd())
         && (validTo == null || validTo.after(ValidityPeriodUtils.getGlobalValidityPeriodEnd()))) {
            return ValidityPeriodUtils.getGlobalValidityPeriodEnd();
        }

        return validTo;
    }
}

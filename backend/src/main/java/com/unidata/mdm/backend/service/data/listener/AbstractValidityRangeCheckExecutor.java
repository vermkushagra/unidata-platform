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

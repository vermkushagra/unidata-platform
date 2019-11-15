package org.unidata.mdm.data.service.segments;

import java.util.Date;
import java.util.Objects;

import org.unidata.mdm.core.context.MutableValidityRangeContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.meta.util.ValidityPeriodUtils;

public interface ValidityRangeCheckSupport <T extends MutableValidityRangeContext> {

    /**
     * Entry point.
     * @param t the context
     * @param factoryFrom factory from
     * @param factoryTo factory to
     */
    default void execute(T t, Date factoryFrom, Date factoryTo) {

        Date from = ensureFrom(t.getValidFrom(), factoryFrom);
        Date to = ensureTo(t.getValidTo(), factoryTo);

        if(from != null && to != null && from.after(to)){
            throw new DataProcessingException("Upserted validity period incorrect. From: [{}], To: [{}].",
                    DataExceptionIds.EX_DATA_VALIDITY_PERIOD_INCORRECT,
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
    default Date ensureFrom(Date validFrom, Date factoryValidFrom) {

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
    default Date ensureTo(Date validTo, Date factoryValidTo) {

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

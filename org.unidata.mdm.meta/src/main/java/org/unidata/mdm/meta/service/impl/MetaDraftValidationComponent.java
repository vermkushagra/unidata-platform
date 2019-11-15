package org.unidata.mdm.meta.service.impl;

import java.util.List;

import javax.annotation.Nonnull;

import org.unidata.mdm.core.type.measurement.MeasurementValue;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;

/**
 * Class responsible for validating model's contexts.
 */
public interface MetaDraftValidationComponent {
    /**
     * Method check a consistency of input param.
     *
     * @param ctx update model request context
     *
     * @param isApply is validation executed for apply draft?
     */
    void validateUpdateModelContext(UpdateModelRequestContext ctx, boolean isApply);

    /**
     * Check references to measurement values, if references find throw exception
     * @param measureValueIds measurement values ids list
     */
    void checkReferencesToMeasurementValues(@Nonnull final List<String> measureValueIds);

    /**
     * Check measureValue for miss measurement units
     * @param ,measureValue measurement value for validate
     */
    void checkReferencesToMeasurementUnits(@Nonnull final MeasurementValue measureValue);

}

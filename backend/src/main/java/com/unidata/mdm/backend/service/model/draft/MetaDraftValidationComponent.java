package com.unidata.mdm.backend.service.model.draft;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import javax.annotation.Nonnull;
import java.util.List;

/**
 * Class responsible for validating model's contexts.
 */
public interface MetaDraftValidationComponent {


    /**
     * Method check a consistency of input param.
     *
     * @param ctx
     */
    void validateUpdateModelContext(UpdateModelRequestContext ctx);

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

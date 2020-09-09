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

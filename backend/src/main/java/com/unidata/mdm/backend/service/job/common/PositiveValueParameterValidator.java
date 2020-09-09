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

package com.unidata.mdm.backend.service.job.common;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.service.job.JobParameterValidator;
import com.unidata.mdm.backend.util.MessageUtils;
import org.apache.commons.collections.CollectionUtils;

/**
 * Job parameter validator for positive values
 *  Error if value <= 0 or null
 * @author Dmitry Kopin on 09.08.2018.
 */
public class PositiveValueParameterValidator implements JobParameterValidator {

    private static final String NOT_POSITIVE_PARAMETER_VALUE = "app.job.parameters.value.notpositive";
    @Override
    public List<String> validate(String paramName, Collection<JobParameterDTO> jobParameters) {
        if (CollectionUtils.isNotEmpty(jobParameters)) {
            JobParameterDTO jobParameter = jobParameters.stream()
                    .filter(param -> param.getName().equals(paramName))
                    .findFirst()
                    .orElse(null);
            if (jobParameter == null) {
                return Collections.emptyList();
            }
            return jobParameter.getLongValue() != null && jobParameter.getLongValue() > 0
                    ? Collections.emptyList()
                    : Collections.singletonList(MessageUtils.getMessage(NOT_POSITIVE_PARAMETER_VALUE, jobParameter.getName()));

        }
        return Collections.emptyList();
    }
}

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

package com.unidata.mdm.backend.service.job.sample;

import com.unidata.mdm.backend.common.job.JobEnumParamExtractor;
import com.unidata.mdm.backend.common.job.JobEnumType;
import com.unidata.mdm.backend.common.job.JobParameterType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Kostovarov
 */
public class SampleItemStringEnumParameterExtractor implements JobEnumParamExtractor{
    @Override
    public JobEnumType extractParameters() {
        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.STRING);
        final List<String> stringParams = new ArrayList<>();
        stringParams.add("Значение 1");
        stringParams.add("Значение 2");
        stringParams.add("Значение 3");

        params.setParameters(stringParams);

        return params;
    }
}
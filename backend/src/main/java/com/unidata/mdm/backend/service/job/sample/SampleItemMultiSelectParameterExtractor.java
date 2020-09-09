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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.common.job.JobEnumParamExtractor;
import com.unidata.mdm.backend.common.job.JobEnumType;
import com.unidata.mdm.backend.common.job.JobParameterType;

/**
 * @author Aleksandr Magdenko
 */
public class SampleItemMultiSelectParameterExtractor implements JobEnumParamExtractor {
    private static final String EMPTY_PARAMS = "No suitable items for importing";

    @Override
        public JobEnumType extractParameters() {
            final JobEnumType params = new JobEnumType();
            params.setParameterType(JobParameterType.STRING);
            params.setMultiSelect(true);

            List<String> values = new ArrayList<>();
            values.add("Value1");
            values.add("Value2");
            values.add("Value3");
            values.add("Value4");
            values.add("Value5");

            if (!values.isEmpty()) {
                params.setParameters(values);
            } else {
                params.setParameters(Collections.singletonList(EMPTY_PARAMS));
            }
            return params;
        }
}

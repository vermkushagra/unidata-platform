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

package com.unidata.mdm.backend.service.job.extractors;

import java.util.Arrays;

import com.unidata.mdm.backend.common.job.JobEnumParamExtractor;
import com.unidata.mdm.backend.common.job.JobEnumType;
import com.unidata.mdm.backend.common.job.JobParameterType;

/**
 * @author Mikhail Mikhailov
 * Audit level as enum.
 */
public class AuditLevelParameterExtractor implements JobEnumParamExtractor {

    /**
     * {@inheritDoc}
     */
    @Override
    public JobEnumType extractParameters() {

        final JobEnumType params = new JobEnumType();
        params.setParameterType(JobParameterType.LONG);
        params.setParameters(Arrays.asList(0L, 1L, 2L));
        return params;
    }

}

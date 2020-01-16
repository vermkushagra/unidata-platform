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

package org.unidata.mdm.core.service.job;

import org.springframework.batch.core.JobParametersBuilder;
import org.unidata.mdm.core.dto.job.JobParameterDTO;

/**
 * @author Mikhail Mikhailov
 * Parameter preprocessor.
 */
public interface JobParameterProcessor {
    /**
     * Does parameters preprocessing.
     * @param param the parameter DTO
     * @param the builder to add parameter to
     * @return null for default processing or
     */
    void process(JobParameterDTO param, JobParametersBuilder builder);
    /**
     * Filters (effectively hides) some parameters if needed.
     * @param parameters the parameters
     * @return parameters filtered parameters
     */
    default JobTemplateParameters filter(JobTemplateParameters parameters) {
        return parameters;
    }
}

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

/**
 * Date: 16.03.2016
 */

package org.unidata.mdm.core.service.job;

import java.util.Set;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public interface JobWithParamsRegistry extends JobRegistry {
    void register(JobTemplateParameters parameters) throws DuplicateJobException;

   	void unregister(String jobName);

    Set<String> getJobParameterNames();

    JobTemplateParameters getJobTemplateParameters(String name);

    void registerTriggerListener(Job job);
}

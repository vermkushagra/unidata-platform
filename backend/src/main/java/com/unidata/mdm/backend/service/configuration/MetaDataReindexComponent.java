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

package com.unidata.mdm.backend.service.configuration;

import static java.util.Arrays.asList;

import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.service.job.JobServiceExt;

@Component
public class MetaDataReindexComponent implements AfterContextRefresh {

    @Autowired
    private JobServiceExt jobServiceExt;

    @Override
    public void afterContextRefresh() {
        JobParameterDTO reindexModel = new JobParameterDTO("reindexModelMeta", true);
        JobParameterDTO reindexClassifiers = new JobParameterDTO("reindexClassifiersMeta", true);
        JobDTO job = new JobDTO();
        job.setDescription("Reindex meta data if it need");
        job.setName("Reindex meta Job");
        job.setEnabled(true);
        job.setJobNameReference("reindexMetaJob");
        job.setParameters(asList(reindexModel, reindexClassifiers));
        JobExecution execution = jobServiceExt.startSystemJob(job);
    }
}

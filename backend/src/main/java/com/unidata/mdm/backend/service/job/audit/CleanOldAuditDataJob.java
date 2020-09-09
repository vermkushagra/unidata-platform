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

package com.unidata.mdm.backend.service.job.audit;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.dao.LargeObjectsDao;
import com.unidata.mdm.backend.service.search.impl.AdminAgentComponent;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.meta.SimpleDataType;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static com.unidata.mdm.backend.common.search.FormField.FormType.POSITIVE;

/**
 * Quartz job for clean unused clob/blob data
 *
 * @author Dmitry Kopin on 10.04.2017
 */
@DisallowConcurrentExecution
public class CleanOldAuditDataJob extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanOldAuditDataJob.class);

    @Autowired
    private AdminAgentComponent adminAgentComponent;

    private void execute(boolean disableJob, Long lifetimeInMinutes) {
        if (!disableJob) {
            LOGGER.info("Started process clean old audit data");
            FormFieldsGroup fields = FormFieldsGroup.createAndGroup();
            Timestamp dateForDelete = new Timestamp(Instant.now().minus(lifetimeInMinutes, ChronoUnit.MINUTES).toEpochMilli());
            fields.addFormField(
                    FormField.range(SimpleDataType.TIMESTAMP, AuditHeaderField.DATE.getField(),
                            POSITIVE, null, dateForDelete));

            SearchRequestContext searchRequestContext = SearchRequestContext.forAuditEvents()
                    .form(Collections.singletonList(fields))
                    .build();
            boolean success = adminAgentComponent.delete(searchRequestContext, true);
            LOGGER.info("Finished process clean old audit data.");
        }
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Process @Autowired injection for the given target object, based on the current web application context.
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        JobDataMap jobDataMap = context.getMergedJobDataMap();

        execute(Boolean.parseBoolean((String)jobDataMap.get("disableJob")),
            Long.parseLong((String)jobDataMap.get("lifetimeInMinutes")));
    }
}

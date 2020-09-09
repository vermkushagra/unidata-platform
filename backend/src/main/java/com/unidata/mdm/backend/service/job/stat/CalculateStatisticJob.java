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

package com.unidata.mdm.backend.service.job.stat;

import com.unidata.mdm.backend.service.statistic.StatServiceExt;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Date;

/**
 * @author Dmitry Kopin on 13.06.2017.
 * Calculate statistic for period from previous fire time to current
 */
public class CalculateStatisticJob  extends QuartzJobBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateStatisticJob.class);

    /** Statistic service */
    @Autowired
    private StatServiceExt statService;

    private void execute(boolean disableJob, Date fromDate, Date toDate) {

        if (!disableJob) {
            LOGGER.info("Started process calculate statistic");

            statService.persistStatistic(fromDate, toDate);
        }
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Process @Autowired injection for the given target object, based on the current web application context.
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        execute(Boolean.parseBoolean((String)jobDataMap.get("disableJob")), context.getPreviousFireTime(), context.getFireTime());
    }
}
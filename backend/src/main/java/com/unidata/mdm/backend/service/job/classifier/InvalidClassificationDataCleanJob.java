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

package com.unidata.mdm.backend.service.job.classifier;

import java.util.List;

import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class InvalidClassificationDataCleanJob extends QuartzJobBean {

    private ClsfDao clsfDao;

    @Autowired
    public void setClsfDao(ClsfDao clsfDao) {
        this.clsfDao = clsfDao;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        if (jobDisabled(context)) {
            return;
        }
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        final List<ClsfPO> allClassifiers = clsfDao.findAllClassifiers();
        allClassifiers.forEach(classifier -> {
            clsfDao.removeOriginsLinksToClassifierNotExistsNodes(classifier);
            clsfDao.removeEtalonLinksToClassifierNotExistsNodes(classifier);
        });
    }

    private boolean jobDisabled(JobExecutionContext context) {
        final JobDataMap jobDataMap = context.getMergedJobDataMap();
        return !jobDataMap.getBooleanValue("jobEnabled");
    }
}

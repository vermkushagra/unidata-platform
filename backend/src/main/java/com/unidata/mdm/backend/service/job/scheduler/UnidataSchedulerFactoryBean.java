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

package com.unidata.mdm.backend.service.job.scheduler;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Mikhail Mikhailov
 * Thin wrapper around {@link SchedulerFactoryBean} to postpone initialization.
 */
public class UnidataSchedulerFactoryBean extends SchedulerFactoryBean implements AfterContextRefresh {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UnidataSchedulerFactoryBean.class);

    public static final String MEMORY_GROUP = "memoryGroup";

    /**
     * Constructor.
     */
    public UnidataSchedulerFactoryBean() {
        super();
    }

    @Override
    public void afterContextRefresh() {
        try {
            super.afterPropertiesSet();
            start();
        } catch (Exception e) {
            LOGGER.error("Scheduler factory afterContextRefresh failed!", e);
        }
    }

    /**
     * Restore previous fire time for jobs with group in memoryGroup
     * @throws SchedulerException
     */
    @Override
    protected void registerJobsAndTriggers() throws SchedulerException {
        Set<TriggerKey> triggersForRestore = getScheduler().getTriggerKeys(GroupMatcher.groupEquals(MEMORY_GROUP));
        Map<TriggerKey, Date> lastFireDatesMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(triggersForRestore)){
            for(TriggerKey triggerKey : triggersForRestore){
                lastFireDatesMap.put(triggerKey, getScheduler().getTrigger(triggerKey).getPreviousFireTime());
            }
        }

        super.registerJobsAndTriggers();

        if(CollectionUtils.isNotEmpty(triggersForRestore)){
            for(TriggerKey triggerKey : triggersForRestore){
                Trigger trigger = getScheduler().getTrigger(triggerKey);
                if(trigger instanceof CronTriggerImpl){
                    ((CronTriggerImpl) trigger).setPreviousFireTime(lastFireDatesMap.get(triggerKey));
                    getScheduler().rescheduleJob(triggerKey, trigger);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Quartz scheduler factory started.");
    }
}

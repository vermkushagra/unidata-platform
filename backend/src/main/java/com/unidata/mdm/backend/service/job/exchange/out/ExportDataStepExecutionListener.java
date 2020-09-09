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

package com.unidata.mdm.backend.service.job.exchange.out;

import javax.sql.DataSource;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.unidata.mdm.backend.exchange.def.ExchangeObject;
import com.unidata.mdm.backend.jdbc.DataSourceUtil;

/**
 * @author Mikhail Mikhailov
 * Step execution listener.
 */
public class ExportDataStepExecutionListener extends ExportDataStepChainMember implements StepExecutionListener {
    /**
     * HZ innstance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {

        if (getJdbcTemplate() == null) {
            String databaseUrl = stepExecution.getJobParameters().getString("databaseUrl");
            DataSource dataSource = DataSourceUtil.initPooledDataSource(databaseUrl, 3);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.afterPropertiesSet();
            setJdbcTemplate(jdbcTemplate);
        }

        String runId = stepExecution.getExecutionContext().getString("runId");
        String partition = stepExecution.getExecutionContext().get("partition").toString();
        if (getExchangeObject() == null) {

            // Provoke NPE, if something went wrong.
            final String objectId = new StringBuilder()
                    .append(ExportDataConstants.EXCHANGE_OBJECTS_PREFIX)
                    .append("_")
                    .append(runId)
                    .append("_")
                    .append(partition)
                    .toString();

            Object obj = hazelcastInstance.getMap(ExportDataConstants.EXCHANGE_OBJECTS_MAP_NAME).get(objectId);
            setExchangeObject((ExchangeObject) obj);
        }

        statisticCountStorage.set(new ExportDataStatisticPage());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        if (jdbcTemplate != null) {
            DataSource dataSource = jdbcTemplate.getDataSource();
            ((ComboPooledDataSource) dataSource).close();
        }

        String runId = stepExecution.getExecutionContext().getString("runId");

        ExportDataStatisticPage statPage = getStatisticPage();
        if (statPage.getFailed() > 0) {

            IAtomicLong fCounter = hazelcastInstance.getAtomicLong(new StringBuilder()
                    .append(ExportDataConstants.EXPORT_JOB_FAIL_RESULT)
                    .append("_")
                    .append(runId)
                    .toString());
            fCounter.addAndGet(statPage.getFailed());
        }

        if (statPage.getSkept() > 0) {

            IAtomicLong sCounter = hazelcastInstance.getAtomicLong(new StringBuilder()
                    .append(ExportDataConstants.EXPORT_JOB_SKIP_RESULT)
                    .append("_")
                    .append(runId)
                    .toString());
            sCounter.addAndGet(statPage.getSkept());
        }

        if (statPage.getInserted() > 0) {

            IAtomicLong iCounter = hazelcastInstance.getAtomicLong(new StringBuilder()
                    .append(ExportDataConstants.EXPORT_JOB_INSERT_RESULT)
                    .append("_")
                    .append(runId)
                    .toString());
            iCounter.addAndGet(statPage.getInserted());
        }

        if (statPage.getUpdated() > 0) {

            IAtomicLong uCounter = hazelcastInstance.getAtomicLong(new StringBuilder()
                    .append(ExportDataConstants.EXPORT_JOB_UPDATE_RESULT)
                    .append("_")
                    .append(runId)
                    .toString());
            uCounter.addAndGet(statPage.getUpdated());
        }

        removeJdbcTemplate();
        removeExchangeObject();
        removeStatisticPage();

        return stepExecution.getExitStatus();
    }

}

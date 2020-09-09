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

package com.unidata.mdm.backend.service.job.matching;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.service.job.reports.CvsReportGenerator;
import com.unidata.mdm.backend.util.reports.ReportUtil;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;
import com.unidata.mdm.backend.util.reports.string.FailedSuccessReport;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Aleksandr Magdenko
 */
public class MatchingJobReportGenerator extends CvsReportGenerator<MatchingReportItemDTO> {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingJobReportGenerator.class);
    private static final int MAX_CLUSTER_RECORDS_NUMBER = 1000;

    /**
     * Success message
     */
    private final static String SUCCESS_MESSAGE = "Сопоставлены";

    /**
     * Empty result
     */
    private final static String EMPTY_RESULT = "Нет записей для сопоставления.";

    /**
     * Failed message
     */
    private final static String ERROR_MESSAGE = "Не сопоставлены";

    /**
     * Cvs headers
     */
    private static final MatchingJobCvsHeaders[] HEADERS = MatchingJobCvsHeaders.values();

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private JobUtil jobUtil;

    /**
     * Matching rule name.
     */
    private String matchingName;
    /**
     * Entity name.
     */
    private String entityName;

    private List<MatchingReportItemDTO> result = null;

    private Long resultCount = null;

    @Override
    public void afterJob(JobExecution jobExecution) {
        try {
            String jobRunTimestamp = jobExecution.getJobParameters()
                    .getString(JobCommonParameters.PARAM_START_TIMESTAMP);

            Date matchingDate = new Date(Long.parseLong(jobRunTimestamp));
            ClusterMetaData clusterMetaData = jobUtil.getMatchingSettings(entityName, matchingName);

            resultCount = clusterService.getUniqueEtalonsCount(clusterMetaData, matchingDate);
            Collection<Cluster> clusters = clusterService.getClusters(clusterMetaData,
                    matchingDate,
                    MAX_CLUSTER_RECORDS_NUMBER,
                    0,
                    false);

            if (CollectionUtils.isNotEmpty(clusters)) {
                result = new ArrayList<>();

                for (Cluster cluster : clusters) {
                    for (ClusterRecord clusterRecord : cluster.getClusterRecords()) {
                        MatchingReportItemDTO reportItem = new MatchingReportItemDTO();

                        reportItem.setClusterId(cluster.getClusterId());
                        reportItem.setRuleId(cluster.getMetaData().getRuleId());
                        reportItem.setRuleName(matchingName);
                        reportItem.setEtalonId(clusterRecord.getEtalonId());

                        result.add(reportItem);
                    }
                }
            }

            super.afterJob(jobExecution);
        } catch (Exception e) {
            LOGGER.error("Error during report creation, some one interrupted thread {}", e);
        } finally {
            result = null;
            resultCount = null;
        }
    }

    @Override
    protected Collection<? extends MatchingReportItemDTO> getInfo() {
        return result;
    }

    @Override
    protected CvsElementExtractor<MatchingReportItemDTO>[] getCvsHeaders() {
        return HEADERS;
    }

    @Nonnull
    @Override
    protected String getAdditionMessage(JobExecution jobExecution) {
        return FailedSuccessReport.builder()
                                  .setSuccessCount(resultCount.intValue())
                                  .setFailedCount(0)
                                  .setEmptyMessage(EMPTY_RESULT)
                                  .setSuccessMessage(SUCCESS_MESSAGE)
                                  .setFailedMessage(ERROR_MESSAGE)
                                  .setMapper(ReportUtil::mapToRecords)
                                  .createFailedSuccessReport()
                                  .generateReport();
    }


    @Required
    public void setMatchingName(String matchingName) {
        this.matchingName = matchingName;
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}

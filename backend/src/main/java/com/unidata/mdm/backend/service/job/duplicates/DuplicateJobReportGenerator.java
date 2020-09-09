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

package com.unidata.mdm.backend.service.job.duplicates;

import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.OPERATION_ID;
import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.SUCCESS;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.job.reports.CvsReportGenerator;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.ReportUtil;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;
import com.unidata.mdm.backend.util.reports.string.FailedSuccessReport;

/**
 * Duplicate job listener, which generate report about results of work.
 */
public class DuplicateJobReportGenerator extends CvsReportGenerator<SearchResultHitDTO> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateJobReportGenerator.class);

    /**
     * Headers
     */
    private static final DuplicateJobCvsHeaders[] HEADERS = DuplicateJobCvsHeaders.values();

    /**
     * Message in case when result is empty.
     */
    private static final String EMPTY_RESULT = "app.job.merge.duplicates.no.clusters.available";

    /**
     * Success message
     */
    private static final String SUCCESS_MESSAGE = "app.job.merge.duplicates.merged";

    /**
     * Failed message
     */
    private static final String ERROR_MESSAGE = "app.job.merge.duplicates.skept";

    /**
     * Search service
     */
    @Autowired
    private SearchService searchService;

    /**
     * Delay for async audit operations.
     */
    @Value("${unidata.batch.report.delay:2}")
    private Integer delay;

    /**
     * Operation id
     */
    private String operationId;

    /**
     * result of job
     */
    private SearchResultDTO result;

    @Override
    public void afterJob(JobExecution jobExecution) {
        try {
            initResult();
            super.afterJob(jobExecution);
        } catch (Exception e) {
            LOGGER.error("Error during report creation, some one interrupted thread {}", e);
        } finally {
            result = null;
        }
    }

    private void initResult() {
        FormField formField = FormField.strictString(OPERATION_ID.getField(), operationId);
        List<String> returnField = Arrays.stream(HEADERS)
                                         .map(DuplicateJobCvsHeaders::getLinkedAuditField)
                                         .map(AuditHeaderField::getField)
                                         .distinct()
                                         .collect(Collectors.toList());
        SearchRequestContext searchRequestContext = SearchRequestContext.forAuditEvents()
                                                                        .form(FormFieldsGroup.createAndGroup(formField))
                                                                        .returnFields(returnField)
                                                                        .totalCount(true)
                                                                        .count(Integer.MAX_VALUE)
                                                                        .build();
        result = searchService.search(searchRequestContext);
    }

    @Override
    protected Collection<? extends SearchResultHitDTO> getInfo() {
        return result.getHits();
    }

    @Override
    protected CvsElementExtractor<SearchResultHitDTO>[] getCvsHeaders() {
        return HEADERS;
    }

    @Nonnull
    @Override
    protected String getAdditionMessage(JobExecution jobExecution) {
        final AtomicInteger successMerges = new AtomicInteger(0);
        final AtomicInteger failed = new AtomicInteger(0);
        result.getHits()
              .stream()
              .map(hit -> hit.getFieldValue(SUCCESS.getField()))
              .filter(Objects::nonNull)
              .filter(SearchResultHitFieldDTO::isSingleValue)
              .map(field -> (Boolean) field.getFirstValue())
              .forEach(value -> {
                  if (value) {
                      successMerges.incrementAndGet();
                  } else {
                      failed.incrementAndGet();
                  }
              });

        return generateMessage(successMerges.get(), failed.get());
    }

    // TODO: 04.06.2018 merge with conflicts as MERGE_MESSAGE, currently it is ERROR_MESSAGE 
    private String generateMessage(int success, int failed) {
        return FailedSuccessReport.builder()
                                  .setSuccessCount(success)
                                  .setFailedCount(failed)
                                  .setEmptyMessage(MessageUtils.getMessage(EMPTY_RESULT))
                                  .setSuccessMessage(MessageUtils.getMessage(SUCCESS_MESSAGE))
                                  .setFailedMessage(MessageUtils.getMessage(ERROR_MESSAGE))
                                  .setMapper(ReportUtil::mapToClusters)
                                  .createFailedSuccessReport()
                                  .generateReport();
    }

    @Required
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
}

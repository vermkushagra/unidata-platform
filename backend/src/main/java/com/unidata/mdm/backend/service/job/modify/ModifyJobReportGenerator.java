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

package com.unidata.mdm.backend.service.job.modify;

import static com.unidata.mdm.backend.common.search.FormField.strictString;
import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.OPERATION_ID;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobConstants;
import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.job.reports.CvsReportGenerator;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;


/**
 * Modify job report generator
 */
public class ModifyJobReportGenerator extends CvsReportGenerator<SearchResultHitDTO> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyJobReportGenerator.class);

    /**
     * Cvs headers
     */
    private final static ModifyJobCvsHeaders[] HEADERS = ModifyJobCvsHeaders.values();

    /**
     * audit service
     */
    @Autowired
    private SearchService searchService;

    /**
     * Delay for async audit operations.
     */
    @Value("${unidata.batch.report.delay:2}")
    private Integer delay;

    /**
     * Result of job
     */
    private SearchResultDTO result;

    /**
     * Operation id
     */
    private String operationId;

    /**
     * This run id.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_RUN_ID + "]}")
    private String runId;

    /**
     * Hazelcast instance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;

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
        FormField formField = strictString(OPERATION_ID.getField(), operationId);
        List<String> returnField = Arrays.stream(HEADERS)
                                         .map(ModifyJobCvsHeaders::getLinkedAuditField)
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

    @Nonnull
    @Override
    protected String getAdditionMessage(JobExecution jobExecution) {

        StringBuilder sb = new StringBuilder();

        // Records
        IAtomicLong counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RECORDS_COUNTER));
        long records = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RECORDS_FAILED_COUNTER));
        long recordsFailed = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RECORDS_SKEPT_COUNTER));
        long recordsSkept = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_CLASSIFIERS_COUNTER));
        long classifiers = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_CLASSIFIERS_DELETED_COUNTER));
        long classifiersDeleted = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_CLASSIFIERS_SKEPT_COUNTER));
        long classifiersSkept= counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_CLASSIFIERS_FAILED_COUNTER));
        long classifiersFailed = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RELATIONS_COUNTER));
        long relations = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RELATIONS_DELETED_COUNTER));
        long relationsDeleted = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RELATIONS_FAILED_COUNTER));
        long relationsFailed = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ModifyItemJobConstants.MODIFY_ITEM_JOB_RELATIONS_SKEPT_COUNTER));
        long relationsSkept = counter.get();

        sb.append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_RECORDS_TOTAL))
                .append(' ')
                .append(records + recordsFailed + recordsSkept)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_UPDATED))
                .append(' ')
                .append(records)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_SKEPT))
                .append(' ')
                .append(recordsSkept)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_FAILED))
                .append(' ')
                .append(recordsFailed)
                .append(".\n");

        sb.append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_CLASSIFIERS_TOTAL))
                .append(' ')
                .append(classifiers + classifiersFailed + classifiersDeleted + classifiersSkept)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_UPDATED))
                .append(' ')
                .append(classifiers)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_DELETED))
                .append(' ')
                .append(classifiersDeleted)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_SKEPT))
                .append(' ')
                .append(classifiersSkept)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_FAILED))
                .append(' ')
                .append(classifiersFailed)
                .append(".\n");

        sb.append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_RELATIONS_TOTAL))
                .append(' ')
                .append(relations + relationsDeleted + relationsFailed + relationsSkept)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_UPDATED))
                .append(' ')
                .append(relations)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_DELETED))
                .append(' ')
                .append(relationsDeleted)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_SKEPT))
                .append(' ')
                .append(relationsSkept)
                .append(".\n")
                .append(MessageUtils.getMessage(ModifyItemJobConstants.MSG_REPORT_FAILED))
                .append(' ')
                .append(relationsFailed)
                .append(".\n");

        return sb.toString();

    }

    @Override
    protected Collection<? extends SearchResultHitDTO> getInfo() {
        return result.getHits();
    }

    @Override
    protected CvsElementExtractor<SearchResultHitDTO>[] getCvsHeaders() {
        return HEADERS;
    }

    @Required
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
}

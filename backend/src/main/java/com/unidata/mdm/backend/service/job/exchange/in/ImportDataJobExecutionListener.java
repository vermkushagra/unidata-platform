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

package com.unidata.mdm.backend.service.job.exchange.in;

import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.jdbc.DataSourceUtil.initSingleDataSource;
import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.ACTION;
import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.OPERATION_ID;
import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.SUCCESS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import com.unidata.mdm.backend.common.types.UpsertAction;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IMap;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.actions.impl.data.UpsertDataAuditAction;
import com.unidata.mdm.backend.service.audit.actions.impl.data.UpsertRelationAuditAction;
import com.unidata.mdm.backend.service.data.batch.BatchSetSize;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.job.reports.CvsReportGenerator;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * Report builder for results of jobs.
 */
@JobScope
public class ImportDataJobExecutionListener extends CvsReportGenerator<SearchResultHitDTO> {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDataJobConstants.IMPORT_JOB_LOGGER_NAME);
    /**
     * Headers
     */
    private static final ImportDataJobCvsHeaders[] RECORD_HEADERS = ImportDataJobCvsHeaders.values();

    private static final String RECORDS_REPORT_FILE_NAME = "records.csv";

    private static final String RELATIONS_REPORT_FILE_NAME = "relations.csv";

    private static final int COUNT_REQUEST_DELAY = 5000;
    /**
     * Unidata data source.
     */
    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;
    /**
     * DDL statements.
     */
    @Qualifier("import-data-job-sql")
    @Autowired
    private Properties sql;
    /**
     * This run id.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_RUN_ID + "]}")
    private String runId;
    /**
     * The set size hint.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_DATA_SET_SIZE + "] ?: 'SMALL'}")
    private BatchSetSize dataSetSize;
    /**
     * New objects only.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_INITIAL_LOAD + "] ?: false}")
    private boolean initialLoad;
    /**
     * Operation id
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_OPERATION_ID + "]}")
    private String operationId;
    /**
     * Move to persistent storage disabled.
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_SKIP_MOVE +  "] ?: false}")
    protected boolean skipMove;
    /**
     * audit service
     * un-1759
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * Complex parameters holder.
     */
    @Autowired
    private ComplexJobParameterHolder jobParameterHolder;
    /**
     * Hazelcast instance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * Scheduler factory to put suspend all triggered jobs.
     */
    @Autowired
    private SchedulerFactoryBean quartzSchedulerFactory;
    /**
     * Delay for async audit operations.
     */
    @Value("${unidata.batch.report.delay:2}")
    private Integer delay;

    @Value("${unidata.job.import_data.report_size:50000}")
    private int reportSize;

    /**
     * Job records result
     */
    private SearchResultDTO result;

    /**
     * Job relation result
     */
    private SearchResultDTO relationResult;

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        init(jobExecution);
        super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        try {
            initResult();
            super.afterJob(jobExecution);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Error during report creation, some one interrupted thread {}", e);
        } finally {

            cleanup(jobExecution);
            this.result = null;

            IMap<Object, Object> map = hazelcastInstance.getMap(ImportDataJobConstants.EXCHANGE_OBJECTS_MAP_NAME);
            for (Object k : map.keySet()) {
                if (k.toString().startsWith(runId)) {
                    map.remove(k);
                }
            }
        }
    }

    /**
     * Do job- global init.
     * @param jobExecution the execution
     */
    private void init(JobExecution jobExecution) {
        try {
            quartzSchedulerFactory.getScheduler().standby();
        } catch (SchedulerException e) {
            LOGGER.warn("Cannot pause scheduled jobs.", e);
        }
    }

    /**
     * Cleanup leftovers (temp tables and so on)
     *
     * @param jobExecution job execution.
     */
    private void cleanup(JobExecution jobExecution) {

        // 1. Drop temp tables
        cleanupTempTables(jobExecution);

        // 2. Cleanup foreign tables if requested
        cleanupForeignTables(jobExecution);

        // 3. Resume scheduler
        try {
            quartzSchedulerFactory.getScheduler().start();
        } catch (SchedulerException e) {
            LOGGER.warn("Cannot resume scheduled jobs.", e);
        }
    }
    /**
     * Drops temp tables.
     */
    private void cleanupTempTables(JobExecution jobExecution) {

        if (dataSetSize == BatchSetSize.SMALL || skipMove) {
            LOGGER.info("Data set is small or 'skipMove' flag is active. No temp tables delete.");
            return;
        }

        String[] dropStatements = new String[] {

                // Records
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsInsertsEtalonsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsUpdatesEtalonsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsInsertsOriginsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsUpdatesOriginsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRecordsOriginsVistoryTableSQL")),

                // Classifiers
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersInsertsEtalonsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersUpdatesEtalonsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersInsertsOriginsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersUpdatesOriginsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropClassifiersOriginsVistoryTableSQL")),

                // Relations
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsInsertsEtalonsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsUpdatesEtalonsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsInsertsOriginsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsUpdatesOriginsTableSQL")),
                ImportDataJobUtils.prepareTaggedQuery(runId, sql.getProperty("dropRelationsOriginsVistoryTableSQL")),
        };

        for (String dropStatement : dropStatements) {
            try (Connection connection = unidataDataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate(dropStatement);
            } catch (SQLException e) {
                LOGGER.warn("SQL exception caught, while dropping temp tables.", e);
            }
        }
    }
    /**
     * Cleanup foreign tables, if requested.
     */
    private void cleanupForeignTables(JobExecution jobExecution) {

        final String definitionKey = jobExecution.getJobParameters().getString(JobCommonParameters.PARAM_DEFINITION);
        final String databaseUrl = jobExecution.getJobParameters().getString(JobCommonParameters.PARAM_DATABASE_URL);

        if (Objects.isNull(definitionKey) || Objects.isNull(databaseUrl)) {
            return;
        }

        ExchangeDefinition def = jobParameterHolder.getComplexParameterAndRemove(definitionKey);
        if (Objects.nonNull(def)) {

            // 2. Do foreign DB cleanup, if requested
            DataSource dataSource = initSingleDataSource(databaseUrl);
            try (Connection connection = dataSource.getConnection()) {

                for (ExchangeEntity ee : def.getEntities()) {

                    if (ee instanceof DbExchangeEntity) {
                        DbExchangeEntity dbe = (DbExchangeEntity) ee;

                        // Truncate, if requested
                        if (dbe.isCleanAfter()) {
                            try (Statement stmt = connection.createStatement()) {
                                for (int i = 0; dbe.getTables() != null && i < dbe.getTables().size(); i++) {
                                    stmt.executeUpdate("truncate " + dbe.getTables().get(i) + " cascade");
                                }
                            }
                        }

                        // Drop, if requested
                        if (dbe.isDropAfter()) {
                            try (Statement stmt = connection.createStatement()) {
                                for (int i = 0; dbe.getTables() != null && i < dbe.getTables().size(); i++) {
                                    stmt.executeUpdate("drop table if exists " + dbe.getTables().get(i));
                                }
                            }
                        }
                    }

                }
            } catch (SQLException e) {
                LOGGER.warn("SQL exception while cleanup / drop of foreign tables was caught.", e);
            }
        }
    }

    private void initResult() {
        final FormField recordsActionField = FormField.strictValues(
                SimpleDataType.STRING,
                ACTION.getField(),
                Arrays.asList(
                        UpsertDataAuditAction.DATA_INSERT_ACTION_NAME,
                        UpsertDataAuditAction.DATA_UPDATE_ACTION_NAME,
                        UpsertAction.NO_ACTION.name(),
                        AuditActions.DATA_DELETE.name()
                )
        );
        this.result = searchResultsForReportResults(recordsActionField);


        final FormField relationsActionField = FormField.strictValues(
                SimpleDataType.STRING,
                ACTION.getField(),
                Arrays.asList(
                        UpsertRelationAuditAction.RELATION_INSERT_ACTION_NAME,
                        UpsertRelationAuditAction.RELATION_UPDATE_ACTION_NAME,
                        AuditActions.DATA_DELETE_RELATION.name()
                )
        );
        this.relationResult = searchResultsForReportResults(relationsActionField);
    }

    private SearchResultDTO searchResultsForReportResults(final FormField filter) {
        final FormField formField = FormField.strictString(OPERATION_ID.getField(), operationId);
        final List<String> returnField = Arrays.stream(RECORD_HEADERS)
                .map(ImportDataJobCvsHeaders::getLinkedAuditField)
                .map(AuditHeaderField::getField)
                .distinct()
                .collect(Collectors.toList());
        returnField.add(SUCCESS.getField());
        final List<SortField> sortFields =
                Collections.singletonList(new SortField(SUCCESS.getField(), SortField.SortOrder.ASC, false));

        final SearchRequestContext.SearchRequestContextBuilder searchRequestContextBuilder = SearchRequestContext.forAuditEvents()
                .form(createAndGroup(formField, filter))
                .returnFields(returnField)
                .totalCount(true)
                .addSorting(sortFields)
                .count(Integer.MAX_VALUE)
                .scrollScan(true)
                .countOnly(true);

        // spin lock while we writing audit messages
        long prevCount = -1;
        while (true) {
            final long currentCount = searchService.search(searchRequestContextBuilder.build()).getTotalCount();
            if (prevCount == currentCount) {
                break;
            }
            prevCount = currentCount;
            try {
                Thread.sleep(COUNT_REQUEST_DELAY);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (reportSize >= 0) {
            searchRequestContextBuilder
                    .count(reportSize)
                    .scrollScan(false);
        }

        searchRequestContextBuilder.countOnly(false);
        return searchService.search(searchRequestContextBuilder.build());
    }

    @Nonnull
    @Override
    protected String getAdditionMessage(JobExecution jobExecution) {

        StringBuilder sb = new StringBuilder();

        // Records
        IAtomicLong fCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_FAIL_COUNTER));
        long failed = fCounter.get();

        IAtomicLong sCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_SKIP_COUNTER));
        long skept = sCounter.get();

        IAtomicLong iCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_INSERT_COUNTER));
        long inserted = iCounter.get();

        IAtomicLong uCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_UPDATE_COUNTER));
        long updated = uCounter.get();

        IAtomicLong dCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_DELETE_COUNTER));
        long deleted = dCounter.get();

        sb.append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_RECORDS_TOTAL))
                .append(' ')
                .append(failed + skept + inserted + updated)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_INSERTED))
                .append(' ')
                .append(inserted)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_UPDATED))
                .append(' ')
                .append(updated)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_SKEPT))
                .append(' ')
                .append(skept)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_DELETED))
                .append(' ')
                .append(deleted)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_FAILED))
                .append(' ')
                .append(failed)
                .append(".\n");

        // Relations
        fCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_FAIL_COUNTER));
        failed = fCounter.get();

        sCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_SKIP_COUNTER));
        skept = sCounter.get();

        iCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_INSERT_COUNTER));
        inserted = iCounter.get();

        uCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_UPDATE_COUNTER));
        updated = uCounter.get();

        dCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_DELETE_COUNTER));
        deleted = dCounter.get();

        sb.append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_RELATIONS_TOTAL))
                .append(' ')
                .append(failed + skept + inserted + updated)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_INSERTED))
                .append(' ')
                .append(inserted)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_UPDATED))
                .append(' ')
                .append(updated)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_SKEPT))
                .append(' ')
                .append(skept)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_DELETED))
                .append(' ')
                .append(deleted)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_FAILED))
                .append(' ')
                .append(failed)
                .append(".\n");

        // Classifiers
        fCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_CLASSIFIERS_FAIL_COUNTER));
        failed = fCounter.get();

        sCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_CLASSIFIERS_SKIP_COUNTER));
        skept = sCounter.get();

        iCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_CLASSIFIERS_INSERT_COUNTER));
        inserted = iCounter.get();

        uCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_CLASSIFIERS_UPDATE_COUNTER));
        updated = uCounter.get();

        dCounter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_CLASSIFIERS_DELETE_COUNTER));
        deleted = dCounter.get();

        sb.append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_CLASSIFIERS_TOTAL))
                .append(' ')
                .append(failed + skept + inserted + updated)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_INSERTED))
                .append(' ')
                .append(inserted)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_UPDATED))
                .append(' ')
                .append(updated)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_SKEPT))
                .append(' ')
                .append(skept)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_DELETED))
                .append(' ')
                .append(deleted)
                .append(".\n")
                .append(MessageUtils.getMessage(ImportDataJobConstants.MSG_REPORT_FAILED))
                .append(' ')
                .append(failed)
                .append(".\n");

        return sb.toString();
    }

    @Override
    protected byte[] getReportBytes(JobExecution jobExecution) {
        final byte[] records = super.getReportBytes(jobExecution);
        final byte[] relations = getRelationsReportBytes(jobExecution);

        if (relations == null || relations.length == 0) {
            return records;
        }

        if (records == null || records.length == 0) {
            return relations;
        }

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (final ZipOutputStream archive = new ZipOutputStream(byteArrayOutputStream)) {
            archive.putNextEntry(new ZipEntry(RECORDS_REPORT_FILE_NAME));
            archive.write(records);

            archive.putNextEntry(new ZipEntry(RELATIONS_REPORT_FILE_NAME));
            archive.write(relations);

            archive.finish();
        } catch (IOException e) {
            LOGGER.error("Can't add zip entry to report archive", e);
        }
        setReportName(StringUtils.substringBeforeLast(getReportName(), ".") + ".zip");
        setReportType("application/zip");
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] getRelationsReportBytes(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            return new byte[0];
        }
        else {
            return getReport(jobExecution, relationResult.getHits());
        }
    }

    @Override
    protected Collection<? extends SearchResultHitDTO> getInfo() {
        return result.getHits();
    }

    @Override
    protected CvsElementExtractor<SearchResultHitDTO>[] getCvsHeaders() {
        return RECORD_HEADERS;
    }

    @Required
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    /**
     * @param runId the runId to set
     */
    public void setRunId(String runId) {
        this.runId = runId;
    }
}

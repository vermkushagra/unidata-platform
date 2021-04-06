package com.unidata.mdm.backend.service.job.reindex;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.common.context.CardinalityAggregationRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext.SearchRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.AggregationResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.service.job.reports.NotificationGenerator;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.ClassifierDataHeaderField;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.ReportUtil;
import com.unidata.mdm.backend.util.reports.string.FailedSuccessReport;

/**
 * Reindex job result listener
 */
@JobScope
public class ReindexDataJobListener extends NotificationGenerator {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobListener.class);

    /**
     * 'was' message
     */
    private static final String WAS = "app.job.reindex.previous.state";

    private static final String RECORDS = "app.job.reindex.records";
    private static final String CLASSIFIERS = "app.job.reindex.classifiers";
    private static final String CLASSIFIED_RECORDS = "app.job.reindex.classified.records";
    private static final String RELATIONS = "app.job.reindex.relations";
    /**
     * 'now' message
     */
    private static final String NOW = "app.job.reindex.now";
    /**
     * Empty result
     */
    private static final String EMPTY_RESULT = "app.job.reindex.empty";


    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchService;

    /**
     * Job util
     */
    @Autowired
    private JobUtil jobUtil;

    /**
     * MMS.
     */
    @Autowired
    protected MetaModelServiceExt metaModelService;

    /**
     * Reindex types.
     */
    private String reindexTypes;

    /**
     * Suppress standard report, if the job is run as part of another complex process.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_SUPPRESS_DEFAULT_REPORT + "] ?: false}")
    private Boolean suppressDefaultReport;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Triple<Long, Long, Long> prevTotalCount;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        try {

            if (!suppressDefaultReport) {
                prevTotalCount = getTotalCount();
            }

            prepareClusterBeforeJob(jobUtil.getEntityList(reindexTypes));
            super.beforeJob(jobExecution);
        } catch (Exception e) {
            LOGGER.warn("Before job caught an exception.", e);
            jobExecution.addFailureException(e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        try {
            resetClusterAfterJob(jobUtil.getEntityList(reindexTypes));
            super.afterJob(jobExecution);
        } catch (Exception e) {
            LOGGER.warn("After job caught an exception.", e);
            jobExecution.addFailureException(e);
        }
    }


    public void setReindexTypes(String reindexTypes) {
        this.reindexTypes = reindexTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String getGeneralMessage(JobExecution jobExecution) {

        final String userReportMessage = extractUserReportMessage(jobExecution);
        if (StringUtils.isNotBlank(userReportMessage)) {
            return userReportMessage;
        }

        return super.getGeneralMessage(jobExecution);
    }

    @Nonnull
    @Override
    protected String getAdditionMessage(JobExecution jobExecution) {

        if (suppressDefaultReport) {
            return StringUtils.EMPTY;
        }

        final Triple<Long, Long, Long> currentTotalCount = getTotalCount();
        final StringBuilder rb = new StringBuilder();

        // 1. Records
        String report = FailedSuccessReport.builder()
                .setSuccessCount(prevTotalCount.getLeft().intValue())
                .setFailedCount(currentTotalCount.getLeft().intValue())
                .setEmptyMessage(MessageUtils.getMessage(EMPTY_RESULT))
                .setSuccessMessage(MessageUtils.getMessage(WAS))
                .setFailedMessage(MessageUtils.getMessage(NOW))
                .noTrailingSpace(true)
                .noTrailingDot(true)
                .setMapper(s -> String.valueOf(ReportUtil.SEMI_COLON))
                .createFailedSuccessReport()
                .generateReport();
        rb
            .append(ReportUtil.SPACE)
            .append(MessageUtils.getMessage(RECORDS))
            .append(ReportUtil.SPACE)
            .append("(")
            .append(report.charAt(0) == ReportUtil.SPACE ? report.substring(1) : report)
            .append(")")
            .append(StringUtils.LF);

        // 2. Classifiers
        report = FailedSuccessReport.builder()
                .setSuccessCount(prevTotalCount.getMiddle().intValue())
                .setFailedCount(currentTotalCount.getMiddle().intValue())
                .setEmptyMessage(MessageUtils.getMessage(EMPTY_RESULT))
                .setSuccessMessage(MessageUtils.getMessage(WAS))
                .setFailedMessage(MessageUtils.getMessage(NOW))
                .noTrailingSpace(true)
                .noTrailingDot(true)
                .setMapper(s -> String.valueOf(ReportUtil.SEMI_COLON))
                .createFailedSuccessReport()
                .generateReport();
        rb
            .append(ReportUtil.SPACE)
            .append(MessageUtils.getMessage(CLASSIFIERS))
            .append(ReportUtil.SPACE)
            .append("(")
            .append(report.charAt(0) == ReportUtil.SPACE ? report.substring(1) : report)
            .append(")")
            .append(StringUtils.LF);

        // 3. Classified records
        report = FailedSuccessReport.builder()
                .setSuccessCount(prevTotalCount.getRight().intValue())
                .setFailedCount(currentTotalCount.getRight().intValue())
                .setEmptyMessage(MessageUtils.getMessage(EMPTY_RESULT))
                .setSuccessMessage(MessageUtils.getMessage(WAS))
                .setFailedMessage(MessageUtils.getMessage(NOW))
                .noTrailingSpace(true)
                .noTrailingDot(true)
                .setMapper(s -> String.valueOf(ReportUtil.SEMI_COLON))
                .createFailedSuccessReport()
                .generateReport();
        rb
            .append(ReportUtil.SPACE)
            .append(MessageUtils.getMessage(CLASSIFIED_RECORDS))
            .append(ReportUtil.SPACE)
            .append("(")
            .append(report.charAt(0) == ReportUtil.SPACE ? report.substring(1) : report)
            .append(")")
            .append(StringUtils.LF);

        return rb.toString();
    }

    private String extractUserReportMessage(JobExecution jobExecution) {

        final String userReport = jobExecution.getJobParameters().getString(ReindexDataJobConstants.USER_REPORT_PARAM);;
        if (StringUtils.isNotBlank(userReport)) {

            final String messageType = jobExecution.getExitStatus().equals(ExitStatus.COMPLETED) ?
                    ReindexDataJobConstants.USER_REPORT_MESSAGE_PARAM :
                    ReindexDataJobConstants.USER_REPORT_FAIL_MESSAGE_PARAM;

            try {
                final Map<?, ?> params = objectMapper.readValue(userReport, Map.class);
                return params.get(messageType).toString();
            } catch (Exception e) {
                LOGGER.error("Cannot create report file due to an exception", e);
            }
        }

        return StringUtils.EMPTY;
    }

    private Triple<Long, Long, Long> getTotalCount() {
        Long totalCount = 0L;
        Long totalClassifierDataCount = 0L;
        Long totalClassifiedRecordsCount = 0L;

        for (String type : jobUtil.getEntityList(reindexTypes)) {
            try {
                // 1. Records
                SearchRequestContext sCtx = SearchRequestContext.builder(EntitySearchType.ETALON, type)
                        .totalCount(true)
                        .countOnly(true)
                        .onlyQuery(true)
                        .fetchAll(true)
                        .build();

                SearchRequestContext subRequest = SearchRequestContext.forEtalonData(type)
                        .totalCount(true)
                        .countOnly(true)
                        .onlyQuery(true)
                        .fetchAll(true)
                        .runExits(false)
                        .build();

                ComplexSearchRequestContext ctx = ComplexSearchRequestContext.hierarchical(sCtx, subRequest);

                SearchResultDTO result = searchService.search(ctx).get(sCtx);
                Long count = result != null ? result.getTotalCount() : 0L;

                totalCount += count;

                // 2. Classifiers
                if (CollectionUtils.isNotEmpty(metaModelService.getClassifiersForEntity(type))) {

                    SearchRequestContextBuilder sCtxb = SearchRequestContext.builder(EntitySearchType.CLASSIFIER, type)
                        .totalCount(true)
                        .countOnly(true)
                        .onlyQuery(true)
                        .fetchAll(true);

                    result = searchService.search(sCtxb.build());
                    totalClassifierDataCount += result.getTotalCount();

                    sCtxb
                        .countOnly(false)
                        .count(0)
                        .aggregations(Collections.singletonList(
                            CardinalityAggregationRequestContext.builder()
                                .entity(type)
                                .name("total_classified_records")
                                .path(ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD.getField())
                                .build()
                    ));

                    result = searchService.search(sCtxb.build());
                    AggregationResultDTO cr = result.getAggregates()
                        .stream()
                        .filter(ar -> "total_classified_records".equals(ar.getAggregationName()))
                        .findFirst()
                        .orElse(null);

                    if (Objects.nonNull(cr)) {
                        totalClassifiedRecordsCount += cr.getCountMap().get(ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD.getField());
                    }
                }

                // 3. Relations TODO

            } catch (Exception e) {
                LOGGER.info("Index doesn't exist for entity {}. {}", type, e);
            }
        }

        return new ImmutableTriple<>(totalCount, totalClassifierDataCount, totalClassifiedRecordsCount);
    }

    /**
     * Do before mass indexing.
     * @param entities the entities to process
     */
    private void prepareClusterBeforeJob(List<String> entities) {

        LOGGER.info("Setting bulk-optimized cluster options.");
        /*
         * ("indices.memory.index_buffer_size", "40%"); // Increase indexing buffer size
         * ("indices.store.throttle.type", "none"); // None throttling.
         */
        Map<String, Object> clusterParams = Collections.singletonMap("indices.store.throttle.type", "none");
        searchService.setClusterSettings(clusterParams, false);
    }
    /**
     * Do after mass indexing.
     * @param entities the entities to process
     */
    private void resetClusterAfterJob(List<String> entities) {

        LOGGER.info("Setting default cluster options.");
        /*
         * ("indices.memory.index_buffer_size", "10%"); // Decrease indexing buffer size
         * ("indices.store.throttle.type", "merge"); // Merge throttling.
         */
        Map<String, Object> clusterParams = Collections.singletonMap("indices.store.throttle.type", "merge");
        searchService.setClusterSettings(clusterParams, false);
    }
}

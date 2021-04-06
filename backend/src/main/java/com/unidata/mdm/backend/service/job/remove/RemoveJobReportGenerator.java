package com.unidata.mdm.backend.service.job.remove;

import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.OPERATION_ID;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.search.SearchRequestType;
import org.apache.commons.lang3.StringUtils;
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
import com.unidata.mdm.backend.util.reports.ReportUtil;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;
import com.unidata.mdm.backend.util.reports.string.FailedSuccessReport;

/**
 * Remove job report generator
 */
public class RemoveJobReportGenerator extends CvsReportGenerator<SearchResultHitDTO> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveJobReportGenerator.class);

    /**
     * Cvs headers
     */
    private static final RemoveJobCvsHeaders[] HEADERS = RemoveJobCvsHeaders.values();

    /**
     * Failed message
     */
    private final static String ERROR_MESSAGE = "app.job.batch.delete.untouched";

    /**
     * Success message
     */
    private final static String SUCCESS_MESSAGE = "app.job.batch.delete.touched";

    /**
     * Success message
     */
    private final static String FIRST_RECORDS_MESSAGE = "app.job.batch.delete.first.records";

    /**
     * Empty message
     */
    private final static String EMPTY_RESULT = "app.job.batch.delete.nothing.done";

    private static final int MAX_REPORT_SEARCH_SIZE = 1000;
    /**
     * Operation id
     */
    private String operationId;

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
     * Result of job
     */
    private SearchResultDTO result;
    private Long successCount = null;
    private Long failedCount = null;

    @Required
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        try {
            initResult();
            super.afterJob(jobExecution);
        } catch (Exception e) {
            LOGGER.error("Error during report creation, some one interrupted thread {}", e);
        } finally {
            result = null;
            successCount = null;
            failedCount = null;
        }
    }

    private void initResult() {
        FormField formField = FormField.strictString(OPERATION_ID.getField(), operationId);

        // Make search for total count with success result.
        SearchRequestContext searchRequestContext = SearchRequestContext.forAuditEvents()
                .form(FormFieldsGroup.createAndGroup(formField))
                .search(SearchRequestType.TERM)
                .searchFields(Collections.singletonList(AuditHeaderField.SUCCESS.getField()))
                .values(Collections.singletonList(true))
                .totalCount(true)
                .countOnly(true)
                .build();

        successCount = searchService.search(searchRequestContext).getTotalCount();

        // Make search for total count with failed result.
        searchRequestContext = SearchRequestContext.forAuditEvents()
                .form(FormFieldsGroup.createAndGroup(formField))
                .search(SearchRequestType.TERM)
                .searchFields(Collections.singletonList(AuditHeaderField.SUCCESS.getField()))
                .values(Collections.singletonList(false))
                .totalCount(true)
                .countOnly(true)
                .build();

        failedCount = searchService.search(searchRequestContext).getTotalCount();

        // Make limited data search.
        List<String> returnField = Arrays.stream(HEADERS)
                                         .map(RemoveJobCvsHeaders::getLinkedAuditField)
                                         .map(AuditHeaderField::getField)
                                         .distinct()
                                         .collect(Collectors.toList());

        searchRequestContext = SearchRequestContext.forAuditEvents()
                .form(FormFieldsGroup.createAndGroup(formField))
                .returnFields(returnField)
                .totalCount(true)
                .count(MAX_REPORT_SEARCH_SIZE)
                .build();

        result = searchService.search(searchRequestContext);

        LOGGER.debug("Search records [success={}, failed={}, totalCount={}]", successCount, failedCount,
                result.getTotalCount());
    }

    @Nonnull
    @Override
    protected String getAdditionMessage(JobExecution jobExecution) {
        int success = successCount.intValue();
        int failed = failedCount.intValue();

        String report = FailedSuccessReport.builder()
                                  .setSuccessCount(success)
                                  .setFailedCount(failed)
                                  .setEmptyMessage(MessageUtils.getMessage(EMPTY_RESULT))
                                  .setSuccessMessage(MessageUtils.getMessage(SUCCESS_MESSAGE))
                                  .setFailedMessage(MessageUtils.getMessage(ERROR_MESSAGE))
                                  .setMapper(ReportUtil::mapToRecords)
                                  .createFailedSuccessReport()
                                  .generateReport();

        // Special message added if total count exceeds MAX_REPORT_SEARCH_SIZE.
        if (success + failed > MAX_REPORT_SEARCH_SIZE) {
            return report +
                    StringUtils.LF +
                    ReportUtil.SPACE +
                    MessageUtils.getMessage(FIRST_RECORDS_MESSAGE, MAX_REPORT_SEARCH_SIZE);
        } else {
            return report;
        }
    }

    @Override
    protected Collection<? extends SearchResultHitDTO> getInfo() {
        return result.getHits();
    }

    @Override
    protected CvsElementExtractor<SearchResultHitDTO>[] getCvsHeaders() {
        return HEADERS;
    }
}

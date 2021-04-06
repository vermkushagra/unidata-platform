package com.unidata.mdm.backend.service.job.removerelations;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.OPERATION_ID;
import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.SUCCESS;

/**
 * Remove job report generator
 */
public class RemoveRelationsJobReportGenerator extends CvsReportGenerator<SearchResultHitDTO> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveRelationsJobReportGenerator.class);

    /**
     * Cvs headers
     */
    private static final RemoveRelationsJobCvsHeaders[] HEADERS = RemoveRelationsJobCvsHeaders.values();

    /**
     * Failed message
     */
    private final static String ERROR_MESSAGE = "app.job.batch.delete.untouched";

    /**
     * Success message
     */
    private final static String SUCCESS_MESSAGE = "app.job.batch.delete.touched";

    /**
     * Empty message
     */
    private final static String EMPTY_RESULT = "app.job.batch.remove.relations.nothing.done";

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
        }
    }

    private void initResult() {
        FormField formField = FormField.strictString(OPERATION_ID.getField(), operationId);
        List<String> returnField = Arrays.stream(HEADERS)
                                         .map(RemoveRelationsJobCvsHeaders::getLinkedAuditField)
                                         .map(AuditHeaderField::getField)
                                         .distinct()
                                         .collect(Collectors.toList());
        SearchRequestContext searchRequestContext = SearchRequestContext.forAuditEvents()
                                                                        .form(FormFieldsGroup.createAndGroup(formField))
                                                                        .returnFields(returnField)
                                                                        .totalCount(true)
                                                                        .count(MAX_REPORT_SEARCH_SIZE)
                                                                        .build();
        result = searchService.search(searchRequestContext);
    }

    @Nonnull
    @Override
    protected String getAdditionMessage(JobExecution jobExecution) {
        final AtomicInteger success = new AtomicInteger(0);
        final AtomicInteger failed = new AtomicInteger(0);
        result.getHits()
              .stream()
              .map(hit -> hit.getFieldValue(SUCCESS.getField()))
              .filter(Objects::nonNull)
              .filter(SearchResultHitFieldDTO::isNonNullField)
              .filter(SearchResultHitFieldDTO::isSingleValue)
              .map(field -> (Boolean) field.getValues().get(0))
              .forEach(value -> {
                  if (value) {
                      success.incrementAndGet();
                  } else {
                      failed.incrementAndGet();
                  }
              });
        return generateMessage(success.get(), failed.get());
    }

    @Nonnull
    private String generateMessage(int success, int failed) {
        return FailedSuccessReport.builder()
                                  .setSuccessCount(success)
                                  .setFailedCount(failed)
                                  .setEmptyMessage(MessageUtils.getMessage(EMPTY_RESULT))
                                  .setSuccessMessage(MessageUtils.getMessage(SUCCESS_MESSAGE))
                                  .setFailedMessage(MessageUtils.getMessage(ERROR_MESSAGE))
                                  .setMapper(ReportUtil::mapToRecords)
                                  .createFailedSuccessReport()
                                  .generateReport();
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

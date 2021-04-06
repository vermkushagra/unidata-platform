package com.unidata.mdm.backend.service.job.softdeletecleanup;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobConstants;
import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobUtils;
import com.unidata.mdm.backend.service.job.reports.CvsReportGenerator;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.reports.cvs.CvsElementExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.unidata.mdm.backend.common.search.FormField.strictString;
import static com.unidata.mdm.backend.service.search.util.AuditHeaderField.OPERATION_ID;


/**
 * Soft delete cleanup job report generator
 */
public class SoftDeleteCleanupJobReportGenerator extends CvsReportGenerator<SearchResultHitDTO> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SoftDeleteCleanupJobReportGenerator.class);

    /**
     * Cvs headers
     */
    private final static SoftDeleteCleanupJobCvsHeaders[] HEADERS = SoftDeleteCleanupJobCvsHeaders.values();

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
            TimeUnit.SECONDS.sleep(delay);
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
                .map(SoftDeleteCleanupJobCvsHeaders::getLinkedAuditField)
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
                ImportDataJobUtils.getObjectReferenceName(runId, SoftDeleteCleanupJobConstants.MODIFY_ITEM_JOB_RECORDS_COUNTER));
        long records = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, SoftDeleteCleanupJobConstants.MODIFY_ITEM_JOB_RECORDS_FAILED_COUNTER));
        long recordsFailed = counter.get();

        counter = hazelcastInstance.getAtomicLong(
                ImportDataJobUtils.getObjectReferenceName(runId, SoftDeleteCleanupJobConstants.MODIFY_ITEM_JOB_RECORDS_DELETE_COUNTER));
        long recordsDeleted = counter.get();

        sb.append(MessageUtils.getMessage(SoftDeleteCleanupJobConstants.MSG_REPORT_RECORDS_TOTAL))
                .append(' ')
                .append(records)
                .append(".\n")
                .append(MessageUtils.getMessage(SoftDeleteCleanupJobConstants.MSG_REPORT_RECORDS_DELETED))
                .append(' ')
                .append(recordsDeleted)
                .append(".\n")
                .append(MessageUtils.getMessage(SoftDeleteCleanupJobConstants.MSG_REPORT_RECORDS_FAILED))
                .append(' ')
                .append(recordsFailed)
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

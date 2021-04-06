package com.unidata.mdm.backend.service.job.reports;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.service.DataRecordsService;

public abstract class ReportGenerator extends NotificationGenerator {

    /**
     * The data record service.
     */
    @Autowired
    protected DataRecordsService dataRecordService;
    /**
     * Report name
     */
    private String reportName;
    /**
     * Mime type
     */
    private String reportType = "text/plain";

    @Override
    public void afterJob(JobExecution jobExecution) {
        super.afterJob(jobExecution);
        addAttachment(jobExecution);
    }

    /**
     * @param jobExecution - job execution
     */
    private void addAttachment(JobExecution jobExecution) {

        byte[] report = getReportBytes(jobExecution);
        if (report == null) {
            return;
        }

        List<String> eventIds = getUserEventIds();
        for (String eventId : eventIds) {

            SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder()
                    .eventKey(eventId)
                    .mimeType(reportType)
                    .binary(true)
                    .inputStream(new ByteArrayInputStream(report))
                    .filename(reportName)
                    .build();

            dataRecordService.saveLargeObject(slorCTX);
        }
    }

    protected byte[] getReportBytes(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            return null;
        } else {
            return getReport(jobExecution);
        }
    }

    /**
     * @param jobExecution
     * @return null in case when you
     */
    protected abstract byte[] getReport(JobExecution jobExecution);

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
}

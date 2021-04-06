package com.unidata.mdm.backend.util.reports;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.service.job.reports.JobReportConstants;
import com.unidata.mdm.backend.util.MessageUtils;

public class ReportUtil {

    /**
     * Space
     */
    public static final char SPACE = ' ';

    /**
     * Dot
     */
    public static final char DOT = '.';

    /**
     * Colon
     */
    public static final char COLON = ':';

    /**
     * Semi - Colon
     */
    public static final char SEMI_COLON = ';';

    /**
     * Dash
     */
    public static final char DASH = '-';

    /**
     * Convert to
     * @param recordCount
     * @return
     */
    @Nonnull
    public static String mapToRecords(long recordCount) {
        if (recordCount == 0) {
            return MessageUtils.getMessage(JobReportConstants.JOB_REPORT_RECORDS_1);
        } else if (recordCount == 1) {
            return MessageUtils.getMessage(JobReportConstants.JOB_REPORT_RECORD);
        } else if (recordCount < 5) {
            return MessageUtils.getMessage(JobReportConstants.JOB_REPORT_RECORDS_2);
        } else {
            return MessageUtils.getMessage(JobReportConstants.JOB_REPORT_RECORDS_1);
        }
    }

    /**
     * Convert to
     * @param recordCount
     * @return
     */
    @Nonnull
    public static String mapToClusters(long recordCount) {
        if (recordCount == 0) {
            return MessageUtils.getMessage(JobReportConstants.JOB_REPORT_CLUSTERS_1);
        } else if (recordCount == 1) {
            return MessageUtils.getMessage(JobReportConstants.JOB_REPORT_CLUSTER);
        } else if (recordCount < 5) {
            return MessageUtils.getMessage(JobReportConstants.JOB_REPORT_CLUSTERS_2);
        } else {
            return MessageUtils.getMessage(JobReportConstants.JOB_REPORT_CLUSTERS_1);
        }
    }
}

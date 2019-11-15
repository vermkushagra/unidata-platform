package org.unidata.mdm.core.dto.reports;

import javax.annotation.Nonnull;

import org.springframework.context.ApplicationContext;
import org.unidata.mdm.system.util.MessageUtils;

public class ReportUtil {

    private static MessageUtils messageUtils;

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
     *
     * @param recordCount
     * @return
     */
    @Nonnull
    public static String mapToRecords(long recordCount) {
        if (recordCount == 0) {
            return messageUtils.getMessage(JobReportConstants.JOB_REPORT_RECORDS_1);
        } else if (recordCount == 1) {
            return messageUtils.getMessage(JobReportConstants.JOB_REPORT_RECORD);
        } else if (recordCount < 5) {
            return messageUtils.getMessage(JobReportConstants.JOB_REPORT_RECORDS_2);
        } else {
            return messageUtils.getMessage(JobReportConstants.JOB_REPORT_RECORDS_1);
        }
    }

    public static void init(ApplicationContext applicationContext) {
        messageUtils = applicationContext.getBean(MessageUtils.class);
    }


}

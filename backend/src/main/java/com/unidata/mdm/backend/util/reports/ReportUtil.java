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

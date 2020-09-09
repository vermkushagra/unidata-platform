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

package com.unidata.mdm.backend.service.job.reports;

/**
 * @author Mikhail Mikhailov
 * Various job report constants.
 */
public class JobReportConstants {
    /**
     * Started.
     */
    public static final String JOB_STATUS_STARTED = "app.job.status.started";
    /**
     * Starting.
     */
    public static final String JOB_STATUS_STARTING = "app.job.status.starting";
    /**
     * Completed.
     */
    public static final String JOB_STATUS_COMPLETED = "app.job.status.completed";
    /**
     * Stopping.
     */
    public static final String JOB_STATUS_STOPPING = "app.job.status.stopping";
    /**
     * Stopped.
     */
    public static final String JOB_STATUS_STOPPED = "app.job.status.stopped";
    /**
     * Failed.
     */
    public static final String JOB_STATUS_FAILED = "app.job.status.failed";
    /**
     * Unknown.
     */
    public static final String JOB_STATUS_UNKNOWN = "app.job.status.unknown";
    /**
     * Records (first plural).
     */
    public static final String JOB_REPORT_RECORDS_1 = "app.job.report.term.records.1";
    /**
     * Records (second plural).
     */
    public static final String JOB_REPORT_RECORDS_2 = "app.job.report.term.records.2";
    /**
     * Records (singular).
     */
    public static final String JOB_REPORT_RECORD = "app.job.report.term.record";
    /**
     * Clusters (first plural).
     */
    public static final String JOB_REPORT_CLUSTERS_1 = "app.job.report.term.clusters.1";
    /**
     * Clusters (second plural).
     */
    public static final String JOB_REPORT_CLUSTERS_2 = "app.job.report.term.clusters.2";
    /**
     * Clusters (singular).
     */
    public static final String JOB_REPORT_CLUSTER = "app.job.report.term.cluster";

    public static final String USER_NAME_PARAM = "userName";

    public static final String JOB_REPORT_TYPE = "app.job.report.reportType";

    /**
     * Constructor.
     */
    private JobReportConstants() {
        super();
    }

}

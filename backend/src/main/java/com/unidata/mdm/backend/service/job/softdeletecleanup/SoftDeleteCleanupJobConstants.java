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

package com.unidata.mdm.backend.service.job.softdeletecleanup;

/**
 * @author Dmitry Kopin on 11.01.2018.
 */
public class SoftDeleteCleanupJobConstants {
    public static final String MODIFY_ITEM_JOB_RECORDS_COUNTER = "#RECORDS_PROCESSED";

    public static final String MODIFY_ITEM_JOB_RECORDS_DELETE_COUNTER = "#RECORDS_DELETED";

    public static final String MODIFY_ITEM_JOB_RECORDS_FAILED_COUNTER = "#RECORDS_FAILED";

    /**
     * Total records message.
     */
    public static final String MSG_REPORT_RECORDS_TOTAL = "app.job.soft.delete.cleanup.records.total";
    /**
     * Total relations message.
     */
    public static final String MSG_REPORT_RECORDS_DELETED = "app.job.soft.delete.cleanup.records.deleted";
    /**
     * Total classifiers message.
     */
    public static final String MSG_REPORT_RECORDS_FAILED = "app.job.soft.delete.cleanup.records.failed";

}

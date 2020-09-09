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

package com.unidata.mdm.backend.service.job.exchange.out;

/**
 * @author Mikhail Mikhailov
 * Export job constants.
 */
public interface ExportDataConstants {
    /**
     * Export job logger name.
     */
    public static final String EXPORT_JOB_LOGGER_NAME = "export-job-logger";
    /**
     * Exchange object prefix for the key of [prefix]_[jobId]_[partitionId].
     */
    public static final String EXCHANGE_OBJECTS_PREFIX = "eo";
    /**
     * Exchange objects map name.
     */
    public static final String EXCHANGE_OBJECTS_MAP_NAME = "export-data-job-exchange-objects";
    /**
     * Update result mark.
     */
    public static final String EXPORT_JOB_UPDATE_RESULT = "UPDATE";
    /**
     * Insert result mark.
     */
    public static final String EXPORT_JOB_INSERT_RESULT = "INSERT";
    /**
     * Skip result mark.
     */
    public static final String EXPORT_JOB_SKIP_RESULT = "SKIP";
    /**
     * Fail result mark.
     */
    public static final String EXPORT_JOB_FAIL_RESULT = "FAIL";
}

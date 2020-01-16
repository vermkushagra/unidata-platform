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

package org.unidata.mdm.core.configuration;

/**
 *
 * @author maria.chistyakova
 * @since  30.10.2019
 */
public interface UserMessageConstants {

    /*
    STATISTIC_EXPORT_SUCCESS, DATA_EXPORT_FETCHED_RECORDS from backend and over

     */
    String JOB_REINDEX_META_SUCCESS = "app.user.events.reindex.meta.jobs.success";
    String JOB_REINDEX_META_FAIL = "app.user.events.reindex.meta.jobs.fail";
}

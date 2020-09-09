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

package com.unidata.mdm.backend.service.job.modify;

/**
 * @author Dmitry Kopin on 11.01.2018.
 */
public class ModifyItemJobConstants {
    public static final String MODIFY_ITEM_JOB_RECORDS_COUNTER = "#RECORDS_MODIFIED";

    public static final String MODIFY_ITEM_JOB_RECORDS_SKEPT_COUNTER = "#RECORDS_SKEPT";

    public static final String MODIFY_ITEM_JOB_RECORDS_FAILED_COUNTER = "#RECORDS_FAILED";

    public static final String MODIFY_ITEM_JOB_CLASSIFIERS_COUNTER = "#CLASSIFIERS_MODIFIED";

    public static final String MODIFY_ITEM_JOB_CLASSIFIERS_SKEPT_COUNTER = "#CLASSIFIERS_SKEPT";

    public static final String MODIFY_ITEM_JOB_CLASSIFIERS_FAILED_COUNTER = "#CLASSIFIERS_FAILED";

    public static final String MODIFY_ITEM_JOB_CLASSIFIERS_DELETED_COUNTER = "#CLASSIFIERS_DELETED";

    public static final String MODIFY_ITEM_JOB_RELATIONS_COUNTER = "#RELATIONS_MODIFIED";

    public static final String MODIFY_ITEM_JOB_RELATIONS_SKEPT_COUNTER = "#RELATIONS_SKEPT";

    public static final String MODIFY_ITEM_JOB_RELATIONS_DELETED_COUNTER = "#RELATIONS_DELETED";

    public static final String MODIFY_ITEM_JOB_RELATIONS_FAILED_COUNTER = "#RELATIONS_FAILED";

    /**
     * Total records message.
     */
    public static final String MSG_REPORT_RECORDS_TOTAL = "app.job.batch.modify.records.total";
    /**
     * Total relations message.
     */
    public static final String MSG_REPORT_RELATIONS_TOTAL = "app.job.batch.modify.relations.total";
    /**
     * Total classifiers message.
     */
    public static final String MSG_REPORT_CLASSIFIERS_TOTAL = "app.job.batch.modify.classifiers.total";
    /**
     * Updated count.
     */
    public static final String MSG_REPORT_UPDATED = "app.job.batch.modify.updated";
    /**
     * Failed count.
     */
    public static final String MSG_REPORT_FAILED = "app.job.batch.modify.failed";
    /**
     * Deleted count.
     */
    public static final String MSG_REPORT_DELETED = "app.job.batch.modify.deleted";
    /**
     * Skept count.
     */
    public static final String MSG_REPORT_SKEPT = "app.job.batch.modify.skept";


    public static final String JOB_NAME = "modifyJob";
}

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

package org.unidata.mdm.data.context;

import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Context flags.
 */
public final class DataContextFlags {
    /**
     * Batch upsert indicator.
     */
    public static final int FLAG_BATCH_OPERATION = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Empty storage?
     */
    public static final int FLAG_EMPTY_STORAGE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Skip extensions?
     */
    public static final int FLAG_BYPASS_EXTENSION_POINTS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Suppress workflow (directed from UI)?
     */
    public static final int FLAG_SUPPRESS_WORKFLOW = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Enrichment hint.
     */
    public static final int FLAG_IS_ENRICHMENT = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Context setup hint.
     */
    public static final int FLAG_IS_SETUP = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Skip DQ hint.
     */
    public static final int FLAG_SKIP_DQ = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Skip DQ hint.
     */
    public static final int FLAG_RECALCULATE_WHOLE_TIMELINE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Restore hint.
     */
    public static final int FLAG_IS_RECORD_RESTORE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Restore hint.
     */
    public static final int FLAG_IS_PERIOD_RESTORE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Restore hint.
     */
    public static final int FLAG_MERGE_WITH_PREVIOUS_VERSION = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Restore hint.
     */
    public static final int FLAG_SKIP_CONSISTENCY_CHECKS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Restore hint.
     */
    public static final int FLAG_SKIP_MATCHING_PREPROCESSING = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();

    public static final int FLAG_SKIP_TIMELINE_CALCULATIONS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Restore hint.
     */
    public static final int FLAG_SKIP_MATCHING = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Restore hint.
     */
    public static final int FLAG_SKIP_INDEX_DROP = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Restore hint.
     */
    public static final int FLAG_SUPPRESS_AUDIT = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Restore hint.
     */
    public static final int FLAG_IS_APPLY_DRAFT = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
// @Modules Moved to fragments
//  flags.set(ContextUtils.CTX_FLAG_FETCH_TASKS, b.tasks);
//  flags.set(ContextUtils.CTX_FLAG_FETCH_RELATIONS, b.fetchRelations);
//  flags.set(ContextUtils.CTX_FLAG_FETCH_CLASSIFIERS, b.fetchClassifiers);
//  flags.set(ContextUtils.CTX_FLAG_FETCH_CLUSTERS, b.fetchClusters);
//  flags.set(ContextUtils.CTX_FLAG_FETCH_DQ_ERRORS, b.fetchDQErrors);
    public static final int FLAG_FETCH_ORIGINS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_FETCH_ORIGINS_HISTORY = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_FETCH_SOFT_DELETED = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_FETCH_LARGE_OBJECTS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_FETCH_TIMELINE_DATA = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_FETCH_KEYS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_FETCH_DATA_FOR_PERIOD = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_FETCH_TIMELINE_BY_TO_SIDE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Fetch all relations for the given record etalon id.
     */
    public static final int FLAG_FETCH_ALL_RELATIONS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_INCLUDE_MERGED = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_INCLUDE_INACTIVE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_INCLUDE_DRAFTS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_INCLUDE_WINNERS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_DIFF_TO_DRAFT = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_DIFF_TO_PREVIOUS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_STRICT_DRAFT = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_REDUCE_REFERENCE_RELATIONS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_INACTIVATE_CASCADE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_INACTIVATE_WIPE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_INACTIVATE_PERIOD = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_INACTIVATE_ORIGIN = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_INACTIVATE_ETALON = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    public static final int FLAG_WORKFLOW_ACTION = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();

    /**
     * Constructor.
     */
    private DataContextFlags() {
        super();
    }
}

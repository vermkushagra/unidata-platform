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

package com.unidata.mdm.backend.service.data;

import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContextConfig;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.SplitContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteRecordDTO;
import com.unidata.mdm.backend.common.dto.EtalonRecordDTO;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.dto.GetRecordsDTO;
import com.unidata.mdm.backend.common.dto.MergeRecordsDTO;
import com.unidata.mdm.backend.common.dto.SplitRecordsDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.service.data.batch.BatchSetAccumulator;

public interface RecordsServiceComponent {

    /**
     * Loads (calculates) contributing records time line for an etalon ID.
     *
     * @param ctx the identifying context
     * @return time line
     */
    TimelineDTO loadRecordsTimeline(GetRequestContext ctx);

    /**
     * Gets a record using parameters set by the context.
     *
     * @param ctx the request context
     * @return {@link GetRecordsDTO}
     */

    GetRecordDTO loadRecord(GetRequestContext ctx);

    /**
     * Gets a record using parameters set by the context.
     *
     * @param ctx the request context
     * @return {@link GetRecordsDTO}
     */
    GetRecordDTO loadEtalonRecordView(GetRequestContext ctx);

    /**
     * Gets a records list using parameters set by the context.
     *
     * @param ctx the request context
     * @return {@link GetRecordsDTO}
     */
    GetRecordsDTO loadRecords(GetMultipleRequestContext ctx);

    /**
     * Deletes a record.
     *
     * @param ctx the context
     * @return key of the record deleted
     */
    DeleteRecordDTO deleteRecord(DeleteRequestContext ctx);

    /**
     * Deletes a number of records at once.
     *
     * @param ctxts the context
     * @return key of the record deleted
     */
    List<DeleteRecordDTO> deleteRecords(List<DeleteRequestContext> ctxts);

    /**
     * Does processing of an etalon.
     * Basically, does the following things for all affected periods:<ul>
     * <li>executes DQ rules,</li>
     * <li>does possible origin upsert,</li>
     * <li>updates Elastiocsearch state</li>
     * </ul>
     *
     * @param ctx the context
     */
    void calculateEtalons(final UpsertRequestContext ctx);

    /**
     * Butch delete runner.
     *
     * @param ctxs list of {@link DeleteRequestContext}
     * @return result
     */
    List<DeleteRecordDTO> batchDeleteRecords(List<DeleteRequestContext> ctxs);

    /**
     * Butch delete runner.
     *
     * @param accumulator the accumulator
     * @return result
     */
    List<DeleteRecordDTO> batchDeleteRecords(BatchSetAccumulator<DeleteRequestContext> accumulator);

    /**
     * Batch Upsert records.
     *
     * @param accumulator the contexts accumulator.
     * @return {@link UpsertRecordDTO}
     */
    List<UpsertRecordDTO> batchUpsertRecords(BatchSetAccumulator<UpsertRequestContext> accumulator);

    /**
     * Batch Upsert records with default parameters.
     *
     * @param ctxs contexts for upsert.
     * @param abortOnFailure abort on Failure flag
     * @return list of {@link UpsertRecordDTO}
     */
    List<UpsertRecordDTO> batchUpsertRecords(List<UpsertRequestContext> ctxs, boolean abortOnFailure);
    /**
     * Upsert a record.
     *
     * @param ctx the request context
     * @return {@link UpsertRecordDTO}
     */
    UpsertRecordDTO upsertRecord(UpsertRequestContext ctx);
    /**
     * Try to restore given record.
     * If record was modified save it.
     * If it wasn't modified restore and recalculate etalon.
     * @param ctx request context
     * @return <code>true</code> if restored, otherwise<code>false</code>
     */
    EtalonRecordDTO restorePeriod(UpsertRequestContext ctx);

    /**
     * Try to restore given record.
     * If record was modified save it.
     * If it wasn't modified restore and recalculate etalon.
     *
     * @param isModified
     * @param ctx request context
     * @return <code>true</code> if restored, otherwise<code>false</code>
     */
    EtalonRecordDTO restoreRecord(UpsertRequestContext ctx, boolean isModified);
    /**
     * Merges several etalon records (and their origins) to one master record.
     *
     * @param ctx current merge context
     * @return true if successful, false otherwise
     */
    MergeRecordsDTO merge(MergeRequestContext ctx);

    /**
     * Detach record to another record
     * @param originId origin to detach
     * @return result dto
     */
    SplitRecordsDTO splitRecord(final String originId);

    /**
     * Merges several etalon records (and their origins) to one master record. Batch mode
     *
     * @param ctxs batch merge records
     * @return true if successful, false otherwise
     */
    List<MergeRecordsDTO> batchMerge(List<MergeRequestContext> ctxs);

    /**
     * Get data quality error list.
     *
     * @param id etalon id.
     * @param entity entity name.
     * @param date date
     * @return list with data quality errors.
     */
    List<DataQualityError> extractDQErrors(String id, String entity, Date date);

    /**
     * Reindex etalon record
     *
     * @param ctx request for identify
     * @return result, true or false
     */

    boolean reindexEtalon(GetRequestContext ctx);

    /**
     * Build reindex request context
     *
     * @param config config
     * @param keys record keys
     * @param timeline timeline
     * @return return index request context
     */
    IndexRequestContext buildIndexRequestContext(final IndexRequestContextConfig config,
                                                 final RecordKeys keys,
                                                 final WorkflowTimelineDTO timeline);
}

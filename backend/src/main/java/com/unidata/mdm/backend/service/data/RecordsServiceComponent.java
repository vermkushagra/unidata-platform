package com.unidata.mdm.backend.service.data;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteRecordDTO;
import com.unidata.mdm.backend.common.dto.EtalonRecordDTO;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.dto.GetRecordsDTO;
import com.unidata.mdm.backend.common.dto.MergeRecordsDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.service.data.batch.BatchSetAccumulator;

import java.util.List;

public interface RecordsServiceComponent{

    /**
     * Loads (calculates) contributing records time line for an etalon ID.
     * @param ctx the identifying context
     * @return time line
     */
    TimelineDTO loadRecordsTimeline(GetRequestContext ctx);

    /**
     * Gets a record using parameters set by the context.
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
     * @param ctx the context
     */
    void calculateEtalons(final UpsertRequestContext ctx);
    /**
     * Butch delete runner.
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
     * @return list of {@link UpsertRecordDTO}
     */
    List<UpsertRecordDTO> batchUpsertRecords(List<UpsertRequestContext> ctxs);

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
     * Merges several etalon records (and their origins) to one master record. Batch mode
     *
     * @param ctxs batch merge records
     * @return true if successful, false otherwise
     */
    List<MergeRecordsDTO> batchMerge(List<MergeRequestContext> ctxs);
}

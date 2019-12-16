package org.unidata.mdm.data.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Nonnull;

import org.unidata.mdm.core.context.DeleteLargeObjectRequestContext;
import org.unidata.mdm.core.context.FetchLargeObjectRequestContext;
import org.unidata.mdm.core.context.SaveLargeObjectRequestContext;
import org.unidata.mdm.core.dto.LargeObjectDTO;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetMultipleRequestContext;
import org.unidata.mdm.data.context.GetRecordTimelineRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.JoinRequestContext;
import org.unidata.mdm.data.context.MergeRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContext;
import org.unidata.mdm.data.context.SplitRecordRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.dto.RecordsBulkResultDTO;
import org.unidata.mdm.data.dto.DeleteRecordDTO;
import org.unidata.mdm.data.dto.EtalonRecordDTO;
import org.unidata.mdm.data.dto.GetRecordDTO;
import org.unidata.mdm.data.dto.GetRecordsDTO;
import org.unidata.mdm.data.dto.KeysJoinDTO;
import org.unidata.mdm.data.dto.MergeRecordsDTO;
import org.unidata.mdm.data.dto.SplitRecordsDTO;
import org.unidata.mdm.data.dto.UpsertRecordDTO;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.keys.RecordKeys;

public interface DataRecordsService {
    // FIXME Kill this.
    String ETALON_ID = "etalonId";

    /**
     * Gets a records using parameters set by the context.
     * @param ctx the request context
     * @return {@link GetRecordDTO}
     */
    GetRecordDTO getRecord(GetRequestContext ctx);

    /**
     * Gets keys for identity context.
     * @param ctx the context
     * @return keys or null
     */
    RecordKeys identify(RecordIdentityContext ctx);

    /**
     * Joins a new external id to an existing etalon key.
     * @param ctx the context
     * @return result
     */
    KeysJoinDTO join(JoinRequestContext ctx);

    /**
     * Gets an etalon record using parameters set by the context.
     * @param ctx the request context
     * @return {@link GetRecordDTO}
     */
    GetRecordDTO getEtalonRecordPreview(GetRequestContext ctx);

    /**
     * Gets a records list using parameters set by the context.
     * @param ctx the request context
     * @return {@link GetRecordsDTO}
     */
    GetRecordsDTO getRecords(GetMultipleRequestContext ctx);

    /**
     * Deletes a record.
     *
     * @param ctx the context
     * @return key of the record deleted
     */
    DeleteRecordDTO deleteRecord(DeleteRequestContext ctx);

    /**
     * Merges several golden records (and their origins) to one winner record.
     *
     * @param ctx current merge context
     * @return true if successful, false otherwise
     */
    MergeRecordsDTO merge(MergeRequestContext ctx);

    /**
     * Fetch LOB delegate.
     *
     * @param ctx the context
     * @return DTO containing data or null
     */
    LargeObjectDTO fetchLargeObject(FetchLargeObjectRequestContext ctx);

    /**
     * Save large object data delegate.
     *
     * @param ctx the context
     * @return true if successful, false otherwise
     */
    LargeObjectDTO saveLargeObject(SaveLargeObjectRequestContext ctx);

    /**
     * Delete large object data delegate.
     *
     * @param ctx the context
     * @return true if successful, false otherwise
     */
    boolean deleteLargeObject(DeleteLargeObjectRequestContext ctx);
    /**
     * Loads record timeline.
     * @param ctx the request
     * @return timeline
     */
    Timeline<OriginRecord> loadTimeline(GetRecordTimelineRequestContext ctx);

    /**
     * @param ctx - record upsert context
     * @return {@link UpsertRecordDTO}
     */
    UpsertRecordDTO upsertRecord(UpsertRequestContext ctx);

    /**
     * Copy record with save in 'draft' state
     * @param ctx context for get source record
     * @return upsert result by periods
     */
    List<UpsertRecordDTO> copyRecord(GetRequestContext ctx);

    /**
     * Publish record and all periods from 'draft' state to 'publish'
     * @param ctx context for get record
     * @return upsert result by periods
     */
    List<UpsertRecordDTO> applyDraftRecord(GetRequestContext ctx);

    /**
     * Upsert record with relations and classifiers with database transaction
     * @param ctx
     * @return
     */
    UpsertRecordDTO upsertFullTransactional(@Nonnull UpsertRequestContext ctx);

    /**
     * @param recordUpsertCtxs - collection of upsert record contexts.
     * @return collection {@link UpsertRecordDTO}
     */
    RecordsBulkResultDTO bulkUpsertRecords(List<UpsertRequestContext> recordUpsertCtxs, boolean abourOnFailure);

    /**
     * @param recordUpsertCtxs - collection of upsert record contexts.
     * @return collection {@link UpsertRecordDTO}
     */
    @Deprecated
    RecordsBulkResultDTO bulkUpsertRecords(List<UpsertRequestContext> recordUpsertCtxs);

    /**
     * Try to restore given record.
     * If record was modified save it.
     * If it wasn't modified restore and recalculate etalon.
     * @param ctx upsert context
     * @param isModified
     * @return <code>EtalonRecordDTO</code> if restored, otherwise<code>null</code>
     */
    EtalonRecordDTO restore(UpsertRequestContext ctx, boolean isModified);

    /**
     * Try to restore given record's period.
     * @param ctx restore context
     * @return <code>EtalonRecordDTO</code> if restored, otherwise<code>null</code>
     */
    EtalonRecordDTO restorePeriod(UpsertRequestContext ctx);

    /**
     * Detach origin from current etalon to new created etalon.
     *
     * @return new etalon id, to which detach origin
     */
    SplitRecordsDTO detachOrigin(SplitRecordRequestContext ctx);
    /**
     * Reindex record by etalon id
     * @param ctx ctx for identify
     * @return
     */
    boolean reindexEtalon(final RecordIdentityContext ctx);
    /**
     * Does mostly the same thing as the method above, but applies DQ rules additionally.
     * @param ctx the context to process
     */
    void reapplyEtalon(UpsertRequestContext ctx);

    List<String> selectCovered(List<String> etalonIds, LocalDateTime from, LocalDateTime to, boolean full);
    /**
     * Get a record as XML using parameters set by the context.
     * @param ctx the request context
     * @return {@link GetRecordDTO}
     */
    String getRecordAsXMLString(GetRequestContext ctx);

    // TODO: @Modules
//    void reindexModifiedEtalons(final CommonDependentContext ctx);
}

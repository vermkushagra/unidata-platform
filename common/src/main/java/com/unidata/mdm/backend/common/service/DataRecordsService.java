package com.unidata.mdm.backend.common.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.FetchLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsDigestRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.JoinRequestContext;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteClassifierDTO;
import com.unidata.mdm.backend.common.dto.DeleteClassifiersDTO;
import com.unidata.mdm.backend.common.dto.DeleteRecordDTO;
import com.unidata.mdm.backend.common.dto.DeleteRelationDTO;
import com.unidata.mdm.backend.common.dto.DeleteRelationsDTO;
import com.unidata.mdm.backend.common.dto.EtalonRecordDTO;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetClassifiersDTO;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.dto.GetRecordsDTO;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.common.dto.GetRelationsDTO;
import com.unidata.mdm.backend.common.dto.KeysJoinDTO;
import com.unidata.mdm.backend.common.dto.LargeObjectDTO;
import com.unidata.mdm.backend.common.dto.MergeRecordsDTO;
import com.unidata.mdm.backend.common.dto.RelationDigestDTO;
import com.unidata.mdm.backend.common.dto.SplitRecordsDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifierDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifiersDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationsDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.DataQualityError;

public interface DataRecordsService {

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
     * Upsert a record.
     *
     * @param ctx the request context
     * @return {@link UpsertRecordDTO}
     */
    UpsertRecordDTO upsertRecord(UpsertRequestContext ctx);

    /**
     * Recalculates etalon.
     *
     * @param ctx the context
     * @return {@link UpsertRecordDTO}
     */
    UpsertRecordDTO recalculateEtalon(UpsertRequestContext ctx);

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

    List<MergeRecordsDTO> batchMerge(List<MergeRequestContext> ctxs);

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
     * Loads the versions time line for the given etalon ID.
     *
     * @param ctx the identifying context
     * @return time line
     */
    TimelineDTO getRecordsTimeline(GetRequestContext ctx);

    /**
     * Loads complete relations versions time line for the given record etalon ID and relation name.
     *
     * @param recordEtalonId the etalon ID
     * @param name relation name
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return timeline
     */
    List<TimelineDTO> getRelationsTimeline(String recordEtalonId, String name, boolean includeDrafts,
            boolean checkPendingState);

    /**
     * Loads relevant relations versions time line for the given record etalon ID and relation name.
     *
     * @param recordEtalonId the etalon ID
     * @param name relation name
     * @param asOf filetrs versions, having this date inside their validity period
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return timeline
     */
    List<TimelineDTO> getRelationsTimeline(String recordEtalonId, String name, Date asOf, boolean includeDrafts,
            boolean checkPendingState);

    /**
     * Loads relevant relations versions time line for the given record etalon ID and relation name.
     *
     * @param recordEtalonId the etalon ID
     * @param name relation name
     * @param from filters versions, having this date inside their validity period or greater than this but less than to
     * @param to filters versions, having this date inside their validity period or less than this but greater than to
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return timeline
     */
    List<TimelineDTO> getRelationsTimeline(String recordEtalonId, String name, Date from, Date to,
            boolean includeDrafts, boolean checkPendingState);

    /**
     * Loads complete relations versions time line for the given record etalon ID and relation name.
     *
     * @param recordEtalonId the etalon ID
     * @return timeline
     */
    TimelineDTO getRelationTimeline(String recordEtalonId);

    /**
     * Loads relevant relations versions time line for the given record etalon ID and relation name.
     *
     * @param recordEtalonId the etalon ID
     * @param asOf filters versions, having this date inside their validity period
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return timeline
     */
    TimelineDTO getRelationTimeline(String recordEtalonId, Date asOf, boolean includeDrafts, boolean checkPendingState);

    /**
     * Loads relevant relations versions time line for the given record etalon ID and relation name.
     *
     * @param recordEtalonId the etalon ID
     * @param from filters versions, having this date inside their validity period or greater than this but less than to
     * @param to filters versions, having this date inside their validity period or less than this but greater than to
     * @param includeDrafts include draft versions
     * @param checkPendingState include draft versions into view or not
     * @return timeline
     */
    TimelineDTO getRelationTimeline(String recordEtalonId, Date from, Date to, boolean includeDrafts,
            boolean checkPendingState);

    /**
     * Collects and returns relation's digest according to the request context.
     *
     * @param ctx request context
     * @return result
     */
    RelationDigestDTO loadRelatedEtalonIdsForDigest(GetRelationsDigestRequestContext ctx);

    /**
     * Loads a relation by its etalon or origin id.
     *
     * @param ctx the context
     * @return relation
     */
    GetRelationDTO getRelation(GetRelationRequestContext ctx);

    /**
     * Gets the relations.
     * @param ctx the context
     * @return relations DTO
     */
    GetRelationsDTO getRelations(GetRelationsRequestContext ctx);

    /**
     * Upsert relation call.
     *
     * @param ctx the context
     * @return result (inserted/updated record)
     */
    UpsertRelationDTO upsertRelation(UpsertRelationRequestContext ctx);

    /**
     * Upsert relations call.
     *
     * @param ctx the context
     * @return result (inserted/updated records)
     */
    UpsertRelationsDTO upsertRelations(UpsertRelationsRequestContext ctx);

    /**
     * Loads a classifier data by its etalon or origin id.
     *
     * @param ctx the context
     * @return classifier data
     */
    GetClassifierDTO getClassifier(GetClassifierDataRequestContext ctx);

    /**
     * Gets the classifiers data.
     * @param ctx the context
     * @return relations DTO
     */
    GetClassifiersDTO getClassifiers(GetClassifiersDataRequestContext ctx);

    /**
     * Upsert classifier call.
     *
     * @param ctx the context
     * @return result (inserted/updated record)
     */
    UpsertClassifierDTO upsertClassifier(UpsertClassifierDataRequestContext ctx);

    /**
     * Upsert classifiers call.
     *
     * @param ctxts the contexts
     * @return result (inserted/updated records)
     */
    UpsertClassifiersDTO upsertClassifiers(List<UpsertClassifierDataRequestContext> ctxts);

    /**
     * Upsert classifiers call.
     *
     * @param ctx the context
     * @return result (inserted/updated records)
     */
    UpsertClassifiersDTO upsertClassifiers(UpsertClassifiersDataRequestContext ctx);

    /**
     * Delete classifier call.
     *
     * @param ctx the context
     * @return result (deleted record)
     */
    DeleteClassifierDTO deleteClassifier(DeleteClassifierDataRequestContext ctx);

    /**
     * Delete classifiers call.
     *
     * @param ctxts the contexts
     * @return result (deleted records)
     */
    DeleteClassifiersDTO deleteClassifiers(List<DeleteClassifierDataRequestContext> ctxts);

    /**
     * Delete classifiers call.
     *
     * @param ctxts the contexts
     * @return result (deleted records)
     */
    DeleteClassifiersDTO deleteClassifiers(DeleteClassifiersDataRequestContext ctxts);

    /**
     * @param ctx - record upsert context
     * @return {@link UpsertRecordDTO}
     */
    UpsertRecordDTO atomicUpsert(UpsertRequestContext ctx);

    /**
     * @param recordUpsertCtxs - collection of upsert record contexts.
     * @return collection {@link UpsertRecordDTO}
     */
    Collection<UpsertRecordDTO> atomicBulkUpsert(Collection<UpsertRequestContext> recordUpsertCtxs);

    /**
     * Deletes a relation.
     *
     * @return result DTO
     */
    DeleteRelationDTO deleteRelation(DeleteRelationRequestContext ctx);

    /**
     * Deletes possibly multiple relations.
     *
     * @return result DTO
     */
    DeleteRelationsDTO deleteRelations(DeleteRelationsRequestContext ctx);

    /**
     * Try to restore given record.
     * If record was modified save it.
     * If it wasn't modified restore and recalculate etalon.
     * @param isModified
     * @param rctx restore context
     * @return <code>EtalonRecordDTO</code> if restored, otherwise<code>null</code>
     */
    EtalonRecordDTO restore(UpsertRequestContext ctx, boolean isModified);

    /**
     * Get data quality error from the elastic search.
     *
     * @param id
     *            etalon id.
     * @param entity
     *            entity name.
     * @param date date
     * @return list with data quality errors.
     */
    List<DataQualityError> getDQErrors(String id, String entity, Date date);

    /**
     * Detach origin from current etalon to new created etalon.
     *
     * @param originId Origin id, which detach
     * @return new etalon id, to which detach origin
     */
    SplitRecordsDTO detachOrigin(String originId);
}
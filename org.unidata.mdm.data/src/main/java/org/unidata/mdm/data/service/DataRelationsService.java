package org.unidata.mdm.data.service;

import java.util.List;

import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.DeleteRelationRequestContext;
import org.unidata.mdm.data.context.DeleteRelationsRequestContext;
import org.unidata.mdm.data.context.GetRelationRequestContext;
import org.unidata.mdm.data.context.GetRelationsDigestRequestContext;
import org.unidata.mdm.data.context.GetRelationsRequestContext;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRelationsRequestContext;
import org.unidata.mdm.data.dto.DeleteRelationDTO;
import org.unidata.mdm.data.dto.DeleteRelationsDTO;
import org.unidata.mdm.data.dto.GetRelationDTO;
import org.unidata.mdm.data.dto.GetRelationsDTO;
import org.unidata.mdm.data.dto.RelationDigestDTO;
import org.unidata.mdm.data.dto.UpsertRelationDTO;
import org.unidata.mdm.data.dto.UpsertRelationsDTO;
import org.unidata.mdm.data.type.apply.batch.BatchSetAccumulator;
import org.unidata.mdm.data.type.data.OriginRelation;

/**
 * @author Mikhail Mikhailov on Dec 3, 2019
 */
public interface DataRelationsService {
    /**
     * Loads relevant relations time line for the given relation identities and relation name.
     *
     * @param ctx the context
     * @return timeline
     */
    List<Timeline<OriginRelation>> loadTimelines(GetRelationsRequestContext ctx);
    /**
     * Loads a relation by its etalon or origin id.
     *
     * @param ctx the context
     * @return relation
     */
    GetRelationDTO getRelation(GetRelationRequestContext ctx);
    /**
     * Gets relations by simple request contexts.
     *
     * @param ctxts the contexts
     * @return relations DTO
     */
    List<GetRelationDTO> getRelations(List<GetRelationRequestContext> ctxts);
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
     * Upsert multiple updating relations call.
     *
     * @param ctxts the contexts to process
     * @return result (inserted/updated record)
     */
    List<UpsertRelationDTO> upsertRelations(List<UpsertRelationRequestContext> ctxts);
    /**
     * Upsert relations call.
     *
     * @param ctx the context
     * @return result (inserted/updated records)
     */
    UpsertRelationsDTO upsertRelations(UpsertRelationsRequestContext ctx);
    /**
     * Deletes a relation.
     *
     * @return result DTO
     */
    DeleteRelationDTO deleteRelation(DeleteRelationRequestContext ctx);
    /**
     * Deletes relations.
     *
     * @param ctxts the contexts
     * @return result DTO
     */
    List<DeleteRelationDTO> deleteRelations(List<DeleteRelationRequestContext> ctxts);
    /**
     * Deletes possibly multiple relations.
     *
     * @return result DTO
     */
    DeleteRelationsDTO deleteRelations(DeleteRelationsRequestContext ctx);
    /**
     * Collects and returns relation's digest according to the request context.
     *
     * @param ctx request context
     * @return result
     */
    RelationDigestDTO loadRelatedEtalonIdsForDigest(GetRelationsDigestRequestContext ctx);
    /**
     * Deletes relations in batched fashion.
     *
     * @param accumulator accumulator
     * @return list of results
     */
    List<DeleteRelationsDTO> batchDeleteRelations(BatchSetAccumulator<DeleteRelationsRequestContext> accumulator);
    /**
     * Batch delete relations with default accumulator context.
     *
     * @param ctxs contexts for delete
     * @return delete result
     */
    List<DeleteRelationsDTO> batchDeleteRelations(List<DeleteRelationsRequestContext> ctxs, boolean abortOnFailure);
    /**
     * Batch upsert relations.
     *
     * @param accumulator accumulator
     * @return result
     */
    List<UpsertRelationsDTO> batchUpsertRelations(BatchSetAccumulator<UpsertRelationsRequestContext> accumulator);
    /**
     * Batch upsert relations with default accumulator context.
     *
     * @param ctxs contexts for update
     * @return update result
     */
    List<UpsertRelationsDTO> batchUpsertRelations(List<UpsertRelationsRequestContext> ctxs, boolean abortOnFailure);
}

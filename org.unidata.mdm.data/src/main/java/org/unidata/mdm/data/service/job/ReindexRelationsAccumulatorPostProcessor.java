package org.unidata.mdm.data.service.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.service.job.JobCommonParameters;
import org.unidata.mdm.core.type.annotation.JobRef;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRelationsRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.dao.RelationsDao;
import org.unidata.mdm.data.dto.UpsertRecordDTO;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertContainmentExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertIndexingExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertPersistenceExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertStartExecutor;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertTimelineExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertConnectorExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertFinishExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertPersistenceExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertProcessExecutor;
import org.unidata.mdm.data.service.segments.relations.batch.RelationsUpsertStartExecutor;
import org.unidata.mdm.data.type.apply.batch.impl.RecordUpsertBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetAccumulator;
import org.unidata.mdm.meta.type.RelationSide;
import org.unidata.mdm.system.service.PipelineService;
import org.unidata.mdm.system.type.batch.BatchSetPostProcessor;
import org.unidata.mdm.system.type.pipeline.ConnectedPipeline;
import org.unidata.mdm.system.type.pipeline.Pipeline;

@JobRef("reindexDataJob")
@Component
@StepScope
public class ReindexRelationsAccumulatorPostProcessor
    implements BatchSetPostProcessor<UpsertRequestContext, UpsertRecordDTO, RecordUpsertBatchSetAccumulator> {
    /**
     * If true, rels will be reindexed
     */
    @Value("#{jobParameters[reindexRelations] ?: false}")
    private boolean jobReindexRelations;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{stepExecutionContext[reindexRelations] ?: false}")
    private boolean stepReindexRelations;
    /**
     * Skip data quality
     */
    @Value("#{jobParameters[skipDq] ?: true}")
    private boolean skipCleanse;
    /**
     * Clean types
     */
    @Value("#{jobParameters[cleanIndexes] ?: false }")
    private boolean skipDrop;
    /**
     * The block size.
     */
    @Value("#{jobParameters[" + JobCommonParameters.PARAM_BLOCK_SIZE + "]}")
    private long blockSize;
    /**
     * Job operation id
     */
    @Value("#{jobParameters[" + JobCommonParameters.PARAM_OPERATION_ID + "]}")
    private String operationId;
    /**
     * Relations DAO.
     */
    @Autowired
    private RelationsDao relationsDao;
    /**
     * PLS.
     */
    @Autowired
    private PipelineService pipelineService;
    public ReindexRelationsAccumulatorPostProcessor() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectedPipeline connect() {

        if (!jobReindexRelations && !stepReindexRelations) {
            return null;
        }

        return ConnectedPipeline.of(
                Pipeline.start(pipelineService.start(RelationsUpsertStartExecutor.SEGMENT_ID))
                    .with(pipelineService.point(RelationsUpsertProcessExecutor.SEGMENT_ID))
                    .with(pipelineService.point(RelationsUpsertPersistenceExecutor.SEGMENT_ID))
                    .end(pipelineService.finish(RelationsUpsertFinishExecutor.SEGMENT_ID)),
                pipelineService.connector(RelationsUpsertConnectorExecutor.SEGMENT_ID));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void accumulate(RecordUpsertBatchSetAccumulator bsa) {

        if (!jobReindexRelations && !stepReindexRelations) {
            return;
        }

        List<UpsertRelationsRequestContext> payload = new ArrayList<>(bsa.workingCopy().size());
        for (UpsertRequestContext ctx : bsa.workingCopy()) {

            Map<String, List<UUID>> ids
                = relationsDao.loadMappedRelationEtalonIds(
                        UUID.fromString(ctx.getEtalonKey()), null, RelationSide.FROM);

            if (MapUtils.isEmpty(ids)) {
                continue;
            }

            Map<String, List<UpsertRelationRequestContext>> result = new HashMap<>(payload.size());
            ids.forEach((k, v) ->
                result.put(k, v.stream()
                        .map(id ->
                            UpsertRelationRequestContext.builder()
                                .relationEtalonKey(id.toString())
                                .relationName(k)
                                .recalculateWholeTimeline(true)
                                .skipIndexDrop(skipDrop)
                                .operationId(operationId)
                                .build()
                        )
                        .collect(Collectors.toList())));

            payload.add(UpsertRelationsRequestContext.builder()
                .relations(result)
                .build());
        }

        if (CollectionUtils.isEmpty(payload)) {
            return;
        }

        RelationUpsertBatchSetAccumulator relbsa = bsa.fragment(RelationUpsertBatchSetAccumulator.ID);
        if (Objects.isNull(relbsa)) {

            // Our fragment pipeline
            Pipeline p = Pipeline.start(pipelineService.start(RelationUpsertStartExecutor.SEGMENT_ID))
                    .with(pipelineService.point(RelationUpsertContainmentExecutor.SEGMENT_ID))
                    .with(pipelineService.point(RelationUpsertTimelineExecutor.SEGMENT_ID))
                    .with(pipelineService.point(RelationUpsertIndexingExecutor.SEGMENT_ID))
                    .with(pipelineService.point(RelationUpsertPersistenceExecutor.SEGMENT_ID))
                    .end(pipelineService.finish(RelationUpsertFinishExecutor.SEGMENT_ID));

            relbsa = new RelationUpsertBatchSetAccumulator(bsa.workingCopy().size(), false, false);
            relbsa.setPipeline(p);

            // Add to carrier
            bsa.fragment(relbsa);
        }

        relbsa.charge(payload);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int order() {
        return 10;
    }
}

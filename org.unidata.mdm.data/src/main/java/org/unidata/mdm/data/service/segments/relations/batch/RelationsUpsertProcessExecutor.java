package org.unidata.mdm.data.service.segments.relations.batch;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.data.context.UpsertRelationsRequestContext;
import org.unidata.mdm.data.dto.UpsertRelationsDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.segments.relations.RelationUpsertConnectorExecutor;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationUpsertBatchSetStatistics;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.system.type.batch.BatchIterator;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.batch.BatchedPoint;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RelationsUpsertProcessExecutor.SEGMENT_ID)
public class RelationsUpsertProcessExecutor extends BatchedPoint<RelationUpsertBatchSetAccumulator> {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationsUpsertProcessExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RELATIONS_UPSERT_PROCESS]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.relations.upsert.process.description";
    /**
     * Connector.
     */
    @Autowired
    private RelationUpsertConnectorExecutor relationsUpsertConnectorExecutor;
    /**
     * Constructor.
     */
    public RelationsUpsertProcessExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(RelationUpsertBatchSetAccumulator accumulator) {

        MeasurementPoint.start();
        try {

            RelationUpsertBatchSetStatistics statistics = accumulator.statistics();
            for (BatchIterator<UpsertRelationsRequestContext> bi = accumulator.iterator(); bi.hasNext(); ) {

                UpsertRelationsRequestContext ctx = bi.next();
                try {

                    UpsertRelationsDTO result = relationsUpsertConnectorExecutor.execute(ctx, accumulator.pipeline());

                    ctx.getRelations().values().stream()
                        .flatMap(Collection::stream)
                        .forEach(uCtx -> {

                            UpsertAction action = uCtx.upsertAction();
                            if (action == UpsertAction.UPDATE) {
                                statistics.incrementUpdated();
                            } else if (action == UpsertAction.INSERT) {
                                statistics.incrementInserted();
                            } else {
                                statistics.incrementSkipped();
                            }
                        });

                    if (statistics.collectResults()) {
                        statistics.addResult(result);
                    }

                } catch (Exception e) {

                    statistics.incrementFailed(ctx.getRelations().values().stream()
                            .flatMap(Collection::stream)
                            .count());

                    LOGGER.warn("Batch upsert relations exception caught.", e);

                    if (accumulator.isAbortOnFailure()) {
                        throw e;
                    }

                    bi.remove();
                }
            }

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return RelationUpsertBatchSetAccumulator.class.isAssignableFrom(start.getInputTypeClass());
    }
}

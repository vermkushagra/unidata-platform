package org.unidata.mdm.data.service.segments.relations.batch;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.data.context.DeleteRelationsRequestContext;
import org.unidata.mdm.data.dto.DeleteRelationsDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.segments.relations.RelationsDeleteConnectorExecutor;
import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSetStatistics;
import org.unidata.mdm.system.type.batch.BatchIterator;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.batch.BatchedPoint;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RelationsDeleteProcessExecutor.SEGMENT_ID)
public class RelationsDeleteProcessExecutor extends BatchedPoint<RelationDeleteBatchSetAccumulator> {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationsDeleteProcessExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RELATIONS_DELETE_PROCESS]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.relations.delete.process.description";
    /**
     * Connector.
     */
    @Autowired
    private RelationsDeleteConnectorExecutor relationsDeleteConnectorExecutor;
    /**
     * Constructor.
     */
    public RelationsDeleteProcessExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(RelationDeleteBatchSetAccumulator accumulator) {

        MeasurementPoint.start();
        try {

            RelationDeleteBatchSetStatistics statistics = accumulator.statistics();
            for (BatchIterator<DeleteRelationsRequestContext> bi = accumulator.iterator(); bi.hasNext(); ) {

                DeleteRelationsRequestContext ctx = bi.next();
                try {

                    DeleteRelationsDTO result = relationsDeleteConnectorExecutor.execute(ctx, accumulator.pipeline());

                    ctx.getRelations().values().stream()
                        .flatMap(Collection::stream)
                        .forEach(dCtx -> {

                            if (dCtx.isInactivatePeriod()) {
                                statistics.incrementUpdated();
                            } else {
                                statistics.incrementDeleted();
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
        return RelationDeleteBatchSetAccumulator.class.isAssignableFrom(start.getInputTypeClass());
    }
}

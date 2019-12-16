package org.unidata.mdm.data.service.segments.relations.batch;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.dto.RelationsBulkResultDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSetAccumulator;
import org.unidata.mdm.data.type.apply.batch.impl.RelationDeleteBatchSetStatistics;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.batch.BatchedFinish;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RelationsDeleteFinishExecutor.SEGMENT_ID)
public class RelationsDeleteFinishExecutor extends BatchedFinish<RelationDeleteBatchSetAccumulator, RelationsBulkResultDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RELATIONS_DELETE_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.relations.delete.finish.description";
    /**
     * Constructor.
     */
    public RelationsDeleteFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, RelationsBulkResultDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RelationsBulkResultDTO finish(RelationDeleteBatchSetAccumulator accumulator) {

        MeasurementPoint.start();
        try {

            RelationsBulkResultDTO result = new RelationsBulkResultDTO();
            RelationDeleteBatchSetStatistics statistics = accumulator.statistics();

            result.setDeleted(statistics.getDeleted());
            result.setFailed(statistics.getFailed());
            result.setSkipped(statistics.getSkipped());
            result.setUpdated(statistics.getUpdated());

            if (statistics.collectResults()) {
                result.setDeleteRelations(statistics.getResults());
            }

            return result;

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

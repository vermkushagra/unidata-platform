package org.unidata.mdm.data.service.segments.records.batch;

import org.springframework.stereotype.Component;
import org.unidata.mdm.data.dto.RecordsBulkResultDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.type.apply.batch.impl.RecordUpsertBatchSetAccumulator;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.pipeline.batch.BatchedFinish;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov on Dec 12, 2019
 */
@Component(RecordsUpsertFinishExecutor.SEGMENT_ID)
public class RecordsUpsertFinishExecutor extends BatchedFinish<RecordUpsertBatchSetAccumulator, RecordsBulkResultDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[BATCH_RECORD_UPSERT_FINISH]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".batch.record.upsert.finish.description";
    /**
     * Constructor.
     */
    public RecordsUpsertFinishExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, RecordsBulkResultDTO.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordsBulkResultDTO finish(RecordUpsertBatchSetAccumulator accumulator) {

        MeasurementPoint.start();
        try {

            RecordsBulkResultDTO result = new RecordsBulkResultDTO();

            result.setFailed(accumulator.statistics().getFailed());
            result.setSkipped(accumulator.statistics().getSkipped());
            result.setInserted(accumulator.statistics().getInserted());
            result.setUpdated(accumulator.statistics().getUpdated());

            if (accumulator.statistics().collectResults()) {
                result.setUpsertRecords(accumulator.statistics().getResults());
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
        return RecordUpsertBatchSetAccumulator.class.isAssignableFrom(start.getInputTypeClass());
    }
}

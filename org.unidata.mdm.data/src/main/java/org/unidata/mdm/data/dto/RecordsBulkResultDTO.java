package org.unidata.mdm.data.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.system.type.pipeline.batch.BatchedPipelineOutput;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragment;

/**
 * The all-in-one bulk ops result transfer object.
 * @author Dmitry Kopin on 14.02.2019.
 */
public class RecordsBulkResultDTO extends AbstractBulkResultDTO implements BatchedPipelineOutput, OutputFragment<RecordsBulkResultDTO> {
    /**
     * The id.
     */
    public static final FragmentId<RecordsBulkResultDTO> ID = new FragmentId<>("RECORDS_BULK_RESULT", RecordsBulkResultDTO::new);
    /**
     * Upserted info.
     */
    private List<UpsertRecordDTO> upsertRecords;
    /**
     * Deleted info.
     */
    private List<DeleteRecordDTO> deleteRecords;
    /**
     * Gets upserted.
     * @return upserted
     */
    public List<UpsertRecordDTO> getUpsertRecords() {
        return Objects.isNull(upsertRecords) ? Collections.emptyList() : upsertRecords;
    }
    /**
     * Set upserted.
     * @param records
     */
    public void setUpsertRecords(List<UpsertRecordDTO> records) {
        this.upsertRecords = records;
    }
    /**
     * @return the deleteRecords
     */
    public List<DeleteRecordDTO> getDeleteRecords() {
        return Objects.isNull(deleteRecords) ? Collections.emptyList() : deleteRecords;
    }
    /**
     * @param deleteRecords the deleteRecords to set
     */
    public void setDeleteRecords(List<DeleteRecordDTO> deleteRecords) {
        this.deleteRecords = deleteRecords;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentId<RecordsBulkResultDTO> fragmentId() {
        return ID;
    }
}

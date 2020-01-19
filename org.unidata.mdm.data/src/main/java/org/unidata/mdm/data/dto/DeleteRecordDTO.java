package org.unidata.mdm.data.dto;

import java.util.List;
import java.util.Objects;

import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.system.dto.ExecutionResult;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 */
public class DeleteRecordDTO implements RecordDTO, PipelineOutput, ExecutionResult {
    /**
     * Record keys for short upsert.
     */
    private RecordKeys recordKeys;

    private List<ErrorInfoDTO> errors;
    /**
     * Constructor for failures.
     */
    public DeleteRecordDTO() {
        super();
    }
    /**
     * Constructor for keys.
     */
    public DeleteRecordDTO(RecordKeys recordKeys) {
        super();
        this.recordKeys = recordKeys;
    }
    /**
     * @return the originKey
     */
    public RecordOriginKey getOriginKey() {
        return Objects.nonNull(recordKeys) ? recordKeys.getOriginKey() : null;
    }
    /**
     * @return the goldenKey
     */
    public RecordEtalonKey getEtalonKey() {
        return Objects.nonNull(recordKeys) ? recordKeys.getEtalonKey() : null;
    }
    /**
     * @return the golden
     */
    public boolean wasSuccess() {
        return Objects.nonNull(recordKeys);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys getRecordKeys() {
        return recordKeys;
    }
    /**
     * @param recordKeys the recordKeys to set
     */
    public void setRecordKeys(RecordKeys recordKeys) {
        this.recordKeys = recordKeys;
    }

    public List<ErrorInfoDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfoDTO> errors) {
        this.errors = errors;
    }
}

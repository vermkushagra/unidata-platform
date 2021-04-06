package com.unidata.mdm.backend.common.dto;

import java.util.Objects;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 */
public class DeleteRecordDTO implements RecordDTO {
    /**
     * Record keys for short upsert.
     */
    private RecordKeys recordKeys;
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
    public OriginKey getOriginKey() {
        return Objects.nonNull(recordKeys) ? recordKeys.getOriginKey() : null;
    }
    /**
     * @return the goldenKey
     */
    public EtalonKey getEtalonKey() {
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
}

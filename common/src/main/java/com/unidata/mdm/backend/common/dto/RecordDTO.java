package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.keys.RecordKeys;

/**
 * @author Mikhail Mikhailov
 * Record (keys) DTO.
 */
public interface RecordDTO {
    /**
     * Gets the record keys.
     * @return keys
     */
    RecordKeys getRecordKeys();

}

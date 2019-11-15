package org.unidata.mdm.data.dto;

import org.unidata.mdm.data.type.keys.RecordKeys;

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

package com.unidata.mdm.backend.common.dto;

import java.util.List;

import com.unidata.mdm.backend.common.types.OriginRecord;

/**
 * @author Mikhail Mikhailov
 * List of origin records.
 */
public interface OriginRecordsDTO {

    /**
     * Gets the list of origins.
     * @return list of origins or null
     */
    List<OriginRecord> getOrigins();
}

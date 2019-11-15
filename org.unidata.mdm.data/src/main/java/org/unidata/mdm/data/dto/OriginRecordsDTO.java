package org.unidata.mdm.data.dto;

import java.util.List;

import org.unidata.mdm.data.type.data.OriginRecord;

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

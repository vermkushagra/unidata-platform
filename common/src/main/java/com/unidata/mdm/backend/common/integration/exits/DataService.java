package com.unidata.mdm.backend.common.integration.exits;

import java.util.Date;

import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;

/**
 * @author Mikhail Mikhailov
 * Simple data service interface, suitable for straight simple operations.
 */
public interface DataService {

    /**
     * Find an etalon record by id.
     * @param etalonId the id
     * @param forDate date
     * @return record or null
     */
    public EtalonRecord findEtalonRecord(String etalonId, Date forDate);
    /**
     * Find origin record by origin id.
     * @param originId the origin id
     * @return record or null
     */
    public OriginRecord findOriginRecord(String originId);
    /**
     * Find origin record by external id.
     * @param externalId the external id
     * @param entityName the entity name
     * @param sourceSystem the source system
     * @return record or null
     */
    public OriginRecord findOriginRecord(String externalId, String entityName, String sourceSystem);
    /**
     * Upsert a new origin record.
     * @param record the record to upsert
     * @return true if successful, false otherwise
     */
    public boolean upsertOriginRecord(OriginRecord record);
}

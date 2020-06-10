package com.unidata.mdm.backend.common.service;

import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * @author Dmitry Kopin on 01.12.2017.
 */
public interface ValidationService {

    /**
     * Validate entity record data and name
     * @param record record for check
     * @param id the id
     */
    void checkEntityDataRecord(DataRecord record, String id);

    /**
     * Validate lookup record data and name
     * @param record record for check
     * @param id the id
     */
    void checkLookupDataRecord(DataRecord record, String id);

    /**
     * Validate relation record data and name
     * @param record record for check
     * @param id the id
     */
    void checkRelationDataRecord(DataRecord record, String id);
}

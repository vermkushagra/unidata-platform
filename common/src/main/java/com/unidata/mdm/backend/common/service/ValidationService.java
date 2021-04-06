package com.unidata.mdm.backend.common.service;

import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * @author Dmitry Kopin on 01.12.2017.
 */
public interface ValidationService {

    /**
     * Validate record data and name
     * @param record record for check
     * @param entityName entity name
     */
    void checkDataRecord(DataRecord record, String entityName);
}

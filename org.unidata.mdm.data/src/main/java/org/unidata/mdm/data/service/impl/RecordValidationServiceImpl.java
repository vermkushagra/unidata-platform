package org.unidata.mdm.data.service.impl;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.data.service.RecordValidationService;

/**
 * @author Mikhail Mikhailov on Nov 5, 2019
 */
@Component("recordValidationService")
public class RecordValidationServiceImpl extends AbstractValidationServiceImpl implements RecordValidationService {
    /**
     * Constructor.
     */
    public RecordValidationServiceImpl() {
        super();
    }

    @Override
    public void checkEntityDataRecord(DataRecord record, String id) {
        checkDataRecord(record, id);
    }

    @Override
    public void checkLookupDataRecord(DataRecord record, String id) {
        checkDataRecord(record, id);
    }

    @Override
    public void checkRelationDataRecord(DataRecord record, String id) {
        checkDataRecord(record, id);
    }
}

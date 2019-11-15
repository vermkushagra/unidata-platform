package org.unidata.mdm.data.type.data;

import org.unidata.mdm.core.type.calculables.CalculationResult;
import org.unidata.mdm.core.type.data.DataRecord;

/**
 * @author Mikhail Mikhailov
 * The etalon record.
 */
public interface EtalonRecord extends DataRecord, CalculationResult<OriginRecord> {
    /**
     * Gets the info section.
     * @return info section
     */
    EtalonRecordInfoSection getInfoSection();
}

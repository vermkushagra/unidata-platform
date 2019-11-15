package org.unidata.mdm.data.type.data;

import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.data.DataRecord;

/**
 * @author Mikhail Mikhailov
 * The origin record.
 */
public interface OriginRecord extends DataRecord, Calculable {
    /**
     * Gets the info section.
     * @return info section
     */
    OriginRecordInfoSection getInfoSection();
}

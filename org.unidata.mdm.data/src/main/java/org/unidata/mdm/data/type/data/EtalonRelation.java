package org.unidata.mdm.data.type.data;

import org.unidata.mdm.core.type.calculables.CalculationResult;
import org.unidata.mdm.core.type.data.DataRecord;

/**
 * @author Mikhail Mikhailov
 *
 */
public interface EtalonRelation extends DataRecord, CalculationResult<OriginRelation> {
    /**
     * Gets the info section.
     * @return the info section.
     */
    EtalonRelationInfoSection getInfoSection();
}

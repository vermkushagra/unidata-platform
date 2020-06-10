/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.types.EtalonRecord;

/**
 * @author Mikhail Mikhailov
 * Etalon record getter interface.
 */
public interface EtalonRecordDTO {
    /**
     * @return the golden record
     */
    public EtalonRecord getEtalon();
}

package com.unidata.mdm.backend.common.types.extended;

import com.unidata.mdm.backend.common.types.ComplexAttribute;


/**
 * @author Dmitrii Kopin
 * Complex attribute, which can verify itself.
 */
public interface WinnerInformationComplexAttribute extends ComplexAttribute {
    /**
     * get winner source system
     * @return winner source system
     */
    String getWinnerSourceSystem();
    /**
     * get winner external identificator
     * @return winner external identificator
     */
    String getWinnerExternalId();
}

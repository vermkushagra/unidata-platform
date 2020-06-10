package com.unidata.mdm.backend.common.types.extended;

import com.unidata.mdm.backend.common.types.CodeAttribute;


/**
 * @author Dmitrii Kopin
 * Simple attribute, which can verify itself.
 */
public interface WinnerInformationCodeAttribute<T> extends CodeAttribute<T> {
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

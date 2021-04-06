package com.unidata.mdm.backend.common.types.extended;

import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.impl.ClobSimpleAttributeImpl;

/**
 * @author Dmitry Kopin
 * Extended Clob attribute.
 */
public class ExtendedClobSimpleAttributeImpl extends ClobSimpleAttributeImpl
        implements WinnerInformationSimpleAttribute<CharacterLargeValue> {

    /**
     * Winner source system
     */
    private final String winnerSourceSystem;
    /**
     * Winner external id
     */
    private final String winnerExternalId;

    /**
     * Constructor.
     */
    public ExtendedClobSimpleAttributeImpl(String name, CharacterLargeValue value, String sourceSystem, String externalId) {
        super(name, value);
        this.winnerSourceSystem = sourceSystem;
        this.winnerExternalId = externalId;
    }

    @Override
    public String getWinnerSourceSystem(){
        return winnerSourceSystem;
    }

    @Override
    public String getWinnerExternalId(){
        return winnerExternalId;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

package com.unidata.mdm.backend.common.types.extended;

import com.unidata.mdm.backend.common.types.impl.DateSimpleAttributeImpl;

import java.time.LocalDate;

/**
 * @author Dmitry Kopin
 * Extended Date attribute.
 */
public class ExtendedDateSimpleAttributeImpl extends DateSimpleAttributeImpl
        implements WinnerInformationSimpleAttribute<LocalDate> {

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
    public ExtendedDateSimpleAttributeImpl(String name, LocalDate value, String sourceSystem, String externalId) {
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

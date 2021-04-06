package com.unidata.mdm.backend.common.types.extended;

import com.unidata.mdm.backend.common.types.impl.TimeArrayAttributeImpl;

import java.time.LocalTime;

/**
 * @author Dmitry Kopin
 * Extended Time array attribute.
 */
public class ExtendedTimeArrayAttributeImpl extends TimeArrayAttributeImpl
        implements WinnerInformationArrayAttribute<LocalTime> {

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
    public ExtendedTimeArrayAttributeImpl(String name, String sourceSystem, String externalId) {
        super(name);
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

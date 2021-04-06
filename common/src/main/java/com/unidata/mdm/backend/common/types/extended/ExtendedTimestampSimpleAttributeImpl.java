package com.unidata.mdm.backend.common.types.extended;

import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;

import java.time.LocalDateTime;

/**
 * @author Dmitry Kopin
 * Extended Timestamp attribute.
 */
public class ExtendedTimestampSimpleAttributeImpl extends TimestampSimpleAttributeImpl
        implements WinnerInformationSimpleAttribute<LocalDateTime> {

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
    public ExtendedTimestampSimpleAttributeImpl(String name, LocalDateTime value, String sourceSystem, String externalId) {
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

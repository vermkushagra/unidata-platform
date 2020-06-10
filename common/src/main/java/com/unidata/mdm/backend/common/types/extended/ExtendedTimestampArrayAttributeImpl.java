package com.unidata.mdm.backend.common.types.extended;

import com.unidata.mdm.backend.common.types.impl.TimestampArrayAttributeImpl;

import java.time.LocalDateTime;

/**
 * @author Dmitry Kopin
 * Extended Timestamp array attribute.
 */
public class ExtendedTimestampArrayAttributeImpl extends TimestampArrayAttributeImpl
        implements WinnerInformationArrayAttribute<LocalDateTime> {

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
    public ExtendedTimestampArrayAttributeImpl(String name, String sourceSystem, String externalId) {
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

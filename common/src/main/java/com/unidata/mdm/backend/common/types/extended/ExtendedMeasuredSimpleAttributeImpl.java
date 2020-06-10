package com.unidata.mdm.backend.common.types.extended;

import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;

/**
 * @author Dmitry Kopin
 * Extended Measured attribute.
 */
public class ExtendedMeasuredSimpleAttributeImpl extends MeasuredSimpleAttributeImpl
        implements WinnerInformationSimpleAttribute<Double> {

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
    public ExtendedMeasuredSimpleAttributeImpl(String name, Double value, String sourceSystem, String externalId) {
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

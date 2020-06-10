package com.unidata.mdm.backend.common.types.extended;

import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.impl.BlobSimpleAttributeImpl;

/**
 * @author Dmitry Kopin
 * Extended Blob attribute.
 */
public class ExtendedBlobSimpleAttributeImpl extends BlobSimpleAttributeImpl
        implements WinnerInformationSimpleAttribute<BinaryLargeValue> {

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
    public ExtendedBlobSimpleAttributeImpl(String name, BinaryLargeValue value, String sourceSystem, String externalId) {
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

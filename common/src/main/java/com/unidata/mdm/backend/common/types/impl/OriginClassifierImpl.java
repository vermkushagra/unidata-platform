package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.OriginClassifierInfoSection;

/**
 * @author Mikhail Mikhailov
 * Origin classifier impl class.
 */
public class OriginClassifierImpl extends AbstractDataRecord implements OriginClassifier {

    /**
     * Info section.
     */
    private OriginClassifierInfoSection infoSection;
    /**
     * Constructor.
     */
    public OriginClassifierImpl() {
        super();
    }

    /**
     * Constructor.
     * @param data
     */
    public OriginClassifierImpl(DataRecord data) {
        super(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginClassifierInfoSection getInfoSection() {
        return infoSection;
    }
    /**
     * @param infoSection the infoSection to set
     */
    public void setInfoSection(OriginClassifierInfoSection infoSection) {
        this.infoSection = infoSection;
    }

    /**
     * Fluent info section setter.
     * @param infoSection the info section to set
     * @return self
     */
    public OriginClassifierImpl withInfoSection(OriginClassifierInfoSection infoSection) {
        setInfoSection(infoSection);
        return this;
    }

    /**
     * Fluent data setter.
     * @param data the data to set
     * @return self
     */
    public OriginClassifierImpl withDataRecord(DataRecord data) {
        internalSet(data);
        return this;
    }
}

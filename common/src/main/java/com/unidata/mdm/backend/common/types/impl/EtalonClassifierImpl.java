package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonClassifierInfoSection;

/**
 * @author Mikhail Mikhailov
 * Etalon classifier data container/ facade.
 */
public class EtalonClassifierImpl extends AbstractDataRecord implements EtalonClassifier {

    /**
     * Info section.
     */
    private EtalonClassifierInfoSection infoSection;

    /**
     * Constructor.
     */
    public EtalonClassifierImpl() {
        super();
    }

    /**
     * Constructor.
     * @param data
     */
    public EtalonClassifierImpl(DataRecord data) {
        super(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonClassifierInfoSection getInfoSection() {
        return infoSection;
    }

    /**
     * @param infoSection the infoSection to set
     */
    public void setInfoSection(EtalonClassifierInfoSection infoSection) {
        this.infoSection = infoSection;
    }

    /**
     * Fluent info section setter.
     * @param infoSection the info section to set
     * @return self
     */
    public EtalonClassifierImpl withInfoSection(EtalonClassifierInfoSection infoSection) {
        setInfoSection(infoSection);
        return this;
    }

    /**
     * Fluent data setter.
     * @param data the data to set
     * @return self
     */
    public EtalonClassifierImpl withDataRecord(DataRecord data) {
        internalSet(data);
        return this;
    }
}

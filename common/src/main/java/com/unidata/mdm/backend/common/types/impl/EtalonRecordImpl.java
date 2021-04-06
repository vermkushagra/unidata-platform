package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;

/**
 * @author Mikhail Mikhailov
 * Etalon record container/view.
 */
public class EtalonRecordImpl extends AbstractDataRecord implements EtalonRecord {

    /**
     * The info section.
     */
    private EtalonRecordInfoSection infoSection;

    /**
     * Constructor.
     */
    public EtalonRecordImpl() {
        super();
    }

    /**
     * Constructor.
     * @param data the data to set
     */
    public EtalonRecordImpl(DataRecord data) {
        super(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRecordInfoSection getInfoSection() {
        return infoSection;
    }

    /**
     * @param infoSection the infoSection to set
     */
    public void setInfoSection(EtalonRecordInfoSection infoSection) {
        this.infoSection = infoSection;
    }

    /**
     * Fluent info section setter.
     * @param infoSection the info section to set
     * @return self
     */
    public EtalonRecordImpl withInfoSection(EtalonRecordInfoSection infoSection) {
        setInfoSection(infoSection);
        return this;
    }

    /**
     * Fluent data setter.
     * @param data the data to set
     * @return self
     */
    public EtalonRecordImpl withDataRecord(DataRecord data) {
        internalSet(data);
        return this;
    }
}

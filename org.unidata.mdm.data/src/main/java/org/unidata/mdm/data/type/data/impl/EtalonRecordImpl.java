package org.unidata.mdm.data.type.data.impl;

import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.impl.AbstractDataRecord;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.EtalonRecordInfoSection;

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

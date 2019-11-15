package org.unidata.mdm.data.type.data.impl;

import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.impl.AbstractDataRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.OriginRecordInfoSection;

/**
 * @author Mikhail Mikhailov
 * Origin record data container.
 */
public class OriginRecordImpl extends AbstractDataRecord implements OriginRecord {

    /**
     * Origin info section.
     */
    private OriginRecordInfoSection infoSection;

    /**
     * Constructor.
     */
    public OriginRecordImpl() {
        super();
    }

    /**
     * Constructor.
     * @param data the view to set
     */
    public OriginRecordImpl(DataRecord data) {
        super(data);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRecordInfoSection getInfoSection() {
        return infoSection;
    }

    /**
     * @param infoSection the infoSection to set
     */
    public void setInfoSection(OriginRecordInfoSection infoSection) {
        this.infoSection = infoSection;
    }

    /**
     * Fluent info section setter.
     * @param infoSection the info section to set
     * @return self
     */
    public OriginRecordImpl withInfoSection(OriginRecordInfoSection infoSection) {
        setInfoSection(infoSection);
        return this;
    }

    /**
     * Fluent data setter.
     * @param data the data to set
     * @return self
     */
    public OriginRecordImpl withDataRecord(DataRecord data) {
        internalSet(data);
        return this;
    }
}

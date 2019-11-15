package org.unidata.mdm.data.type.data.impl;

import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.impl.AbstractDataRecord;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.OriginRelationInfoSection;

/**
 * @author Mikhail Mikhailov
 * Origin relation.
 */
public class OriginRelationImpl extends AbstractDataRecord implements OriginRelation {
    /**
     * Info section.
     */
    private OriginRelationInfoSection infoSection;
    /**
     * Constructor.
     */
    public OriginRelationImpl() {
        super();
    }

    /**
     * Constructor.
     * @param data
     */
    public OriginRelationImpl(DataRecord data) {
        super(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRelationInfoSection getInfoSection() {
        return infoSection;
    }

    /**
     * @param infoSection the infoSection to set
     */
    public void setInfoSection(OriginRelationInfoSection infoSection) {
        this.infoSection = infoSection;
    }

    /**
     * Fluent info section setter.
     * @param infoSection the info section to set
     * @return self
     */
    public OriginRelationImpl withInfoSection(OriginRelationInfoSection infoSection) {
        setInfoSection(infoSection);
        return this;
    }

    /**
     * Fluent data setter.
     * @param data the data to set
     * @return self
     */
    public OriginRelationImpl withDataRecord(DataRecord data) {
        internalSet(data);
        return this;
    }

}

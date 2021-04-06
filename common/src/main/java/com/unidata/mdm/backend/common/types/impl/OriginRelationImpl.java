package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.OriginRelationInfoSection;

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

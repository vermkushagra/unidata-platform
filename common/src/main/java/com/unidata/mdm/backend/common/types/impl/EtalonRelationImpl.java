package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;

/**
 * @author Mikhail Mikhailov
 * Etalon relation data container/ facade.
 */
public class EtalonRelationImpl extends AbstractDataRecord implements EtalonRelation {

    /**
     * Info section.
     */
    private EtalonRelationInfoSection infoSection;

    /**
     * Constructor.
     */
    public EtalonRelationImpl() {
        super();
    }

    /**
     * Constructor.
     * @param data
     */
    public EtalonRelationImpl(DataRecord data) {
        super(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRelationInfoSection getInfoSection() {
        return infoSection;
    }

    /**
     * @param infoSection the infoSection to set
     */
    public void setInfoSection(EtalonRelationInfoSection infoSection) {
        this.infoSection = infoSection;
    }

    /**
     * Fluent info section setter.
     * @param infoSection the info section to set
     * @return self
     */
    public EtalonRelationImpl withInfoSection(EtalonRelationInfoSection infoSection) {
        setInfoSection(infoSection);
        return this;
    }

    /**
     * Fluent data setter.
     * @param data the data to set
     * @return self
     */
    public EtalonRelationImpl withDataRecord(DataRecord data) {
        internalSet(data);
        return this;
    }
}

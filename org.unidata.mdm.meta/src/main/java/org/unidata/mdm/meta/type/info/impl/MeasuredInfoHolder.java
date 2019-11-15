package org.unidata.mdm.meta.type.info.impl;

import org.unidata.mdm.core.type.model.MeasuredModelElement;
import org.unidata.mdm.meta.AttributeMeasurementSettingsDef;

/**
 * @author Mikhail Mikhailov
 * Measured settings info holder.
 */
public class MeasuredInfoHolder implements MeasuredModelElement {
    /**
     * The settings.
     */
    private final AttributeMeasurementSettingsDef measured;
    /**
     * Constructor.
     */
    public MeasuredInfoHolder(AttributeMeasurementSettingsDef def) {
        super();
        this.measured = def;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueId() {
        return measured.getValueId();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultUnitId() {
        return measured.getDefaultUnitId();
    }
}

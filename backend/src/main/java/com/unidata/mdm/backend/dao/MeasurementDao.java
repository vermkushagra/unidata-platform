package com.unidata.mdm.backend.dao;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Map;

import com.unidata.mdm.backend.po.measurement.MeasurementValuePO;

/**
 * Measurement dao
 */
public interface MeasurementDao {

    /**
     * Save
     *
     * @param value - value
     */
    void save(@Nonnull MeasurementValuePO value);

    /**
     * Update
     * @param value - value
     */
    void update(@Nonnull MeasurementValuePO value);

    /**
     * @param valueId - value id
     * @return measurement value
     */
    @Nullable
    MeasurementValuePO getById(@Nonnull String valueId);

    /**
     * @return measurement value
     */
    @Nonnull
    Map<String, MeasurementValuePO> getAllValues();

    /**
     * @param measureValueIds - value ids
     * @return true if was removed, other wise false
     */
    boolean removeValues(@Nonnull Collection<String> measureValueIds);

}

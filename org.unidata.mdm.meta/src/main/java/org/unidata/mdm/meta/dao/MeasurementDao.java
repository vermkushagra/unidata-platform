/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.meta.dao;


import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.meta.po.MeasurementValuePO;

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

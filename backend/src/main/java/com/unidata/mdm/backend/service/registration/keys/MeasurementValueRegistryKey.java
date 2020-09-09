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

package com.unidata.mdm.backend.service.registration.keys;

import static com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey.Type.MEASUREMENT_VALUE;

import javax.annotation.Nonnull;

public class MeasurementValueRegistryKey implements UniqueRegistryKey {

    /**
     * Measurement value id;
     */
    @Nonnull
    private final String valueId;

    /**
     * Constructor
     *
     * @param valueId - value id
     */
    public MeasurementValueRegistryKey(@Nonnull String valueId) {
        this.valueId = valueId;
    }

    @Override
    public Type keyType() {
        return MEASUREMENT_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasurementValueRegistryKey)) return false;

        MeasurementValueRegistryKey that = (MeasurementValueRegistryKey) o;

        return valueId.equals(that.valueId);

    }

    @Override
    public int hashCode() {
        return valueId.hashCode();
    }

    @Nonnull
    public String getValueId() {
        return valueId;
    }

    @Override
    public String toString() {
        return "{" +
                "valueId='" + valueId + '\'' +
                '}';
    }
}

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

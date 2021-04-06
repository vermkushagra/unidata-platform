package com.unidata.mdm.backend.service.job;

import javax.annotation.Nonnull;

public interface ComplexJobParameterHolder {
    /**
     * Put complex parameter to container.
     * @param key - key
     * @param parameter -  parameter
     */
    void putComplexParameter(@Nonnull String key, @Nonnull Object parameter);

    /**
     *
     * @param key - key
     * @param <T> -
     * @return complex parameter
     */
    @SuppressWarnings("unchecked")
    <T> T getComplexParameter(@Nonnull String key);

    /**
     * Clean complex parameter
     * @param key -  key
     */
    void removeComplexParameter(@Nonnull String key);

    /**
     * Combination of methods getComplexParameter() and removeComplexParameter()
     * @param complexParameterKey
     * @param <T>
     * @return
     */
    <T> T getComplexParameterAndRemove(@Nonnull String complexParameterKey);
}

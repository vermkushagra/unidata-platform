package org.unidata.mdm.core.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Service;
import org.unidata.mdm.core.service.job.ComplexJobParameterHolder;

/**
 * Object responsible fot containing complex parameters for job execution.
 */
@Service
public class ComplexJobParameterHolderImpl implements ComplexJobParameterHolder {

    /**
     * Map of complex parameters
     */
    private Map<String, Object> complexParameters = new ConcurrentHashMap<>();

    @Override
    public void putComplexParameter(@Nonnull String key, @Nonnull Object parameter) {
        complexParameters.put(key, parameter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getComplexParameter(@Nonnull String key) {
        return (T) complexParameters.get(key);
    }

    @Override
    public void removeComplexParameter(@Nonnull String key) {
        complexParameters.remove(key);
    }

    @Override
    public <T> T getComplexParameterAndRemove(@Nonnull String complexParameterKey) {
        T result = getComplexParameter(complexParameterKey);
        removeComplexParameter(complexParameterKey);
        return result;
    }
}

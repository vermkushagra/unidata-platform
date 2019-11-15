package org.unidata.mdm.core.service.impl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.service.ConfigurationService;

/**
 * @author Alexander Malyshev
 */
@Service
public class ConfigurationServiceImpl implements ConfigurationService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public String getSystemStringProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }

    @Override
    public String getSystemStringPropertyWithDefault(String key, String defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    @Override
    public Boolean getSystemBooleanProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key, Boolean.class);
    }

    @Override
    public Boolean getSystemBooleanPropertyWithDefault(String key, Boolean defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, Boolean.class, defaultValue);
    }

    @Override
    public Integer getSystemIntProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key, Integer.class);
    }

    @Override
    public Integer getSystemIntPropertyWithDefault(String key, Integer defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, Integer.class, defaultValue);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

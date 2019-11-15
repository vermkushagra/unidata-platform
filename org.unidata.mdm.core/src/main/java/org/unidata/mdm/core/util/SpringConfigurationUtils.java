package org.unidata.mdm.core.util;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Alexander Malyshev
 */
public final class SpringConfigurationUtils {
    private SpringConfigurationUtils() {}

    public static PropertiesFactoryBean classpathPropertiesFactoryBean(final String path) {
        final PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource(path));
        return propertiesFactoryBean;
    }
}

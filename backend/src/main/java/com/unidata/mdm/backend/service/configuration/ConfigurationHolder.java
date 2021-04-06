package com.unidata.mdm.backend.service.configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.conf.Configuration;

/**
 * @author Michael Yashin. Created on 28.05.2015.
 */
public class ConfigurationHolder {
    /**
     * User exits configuration.
     */
    public static final String CONFIGURATION_FILENAME = "unidata-conf.xml";
    /**
     * Backend system properties filename.
     */
    public static final String PROPERTIES_FILENAME = "backend.properties";
    /**
     * Configuration path property.
     */
    public static final String CONFIGURATION_PATH_PROPERTY = "unidata.conf";
    /**
     * Catalina base path property.
     */
    public static final String CATALINA_PATH_PROPERTY = "catalina.base";
    /**
     * Logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationHolder.class);
    /**
     * Configuration singleton.
     */
    private static Configuration instance;
    /**
     * Backend properties.
     */
    private static Properties properties;
    /**
     * Global configuration getter method.
     * @return configuration
     * @throws Exception
     */
    public static synchronized Configuration getUserExitsConfiguration() throws Exception {
        if (instance == null) {
            String confPathProperty = System.getProperty(CONFIGURATION_PATH_PROPERTY);
            if (confPathProperty == null) {
                confPathProperty = ".";
            }

            final Path confPath = Paths.get(confPathProperty, CONFIGURATION_FILENAME);
            try (InputStream is = new FileInputStream(confPath.toFile())) {
                instance = JaxbUtils.unmarshalConfiguration(is);
            } catch (Exception e) {
                LOGGER.warn("I/O error caught while reading user exits confguration {}.", e);
            }
        }
        return instance;
    }

    /**
     * Loads system properties.
     * @return properties.
     */
    public static synchronized Properties getBackendProperties() {
        if (properties == null) {
            String confPathProperty = System.getProperty(CONFIGURATION_PATH_PROPERTY);
            if (confPathProperty == null) {
                confPathProperty = ".";
            }

            Path confPath = Paths.get(System.getProperty(CONFIGURATION_PATH_PROPERTY), PROPERTIES_FILENAME);
            try (InputStream is = new FileInputStream(confPath.toFile())) {
                Properties p = new Properties();
                p.load(is);
                properties = p;
            } catch (Exception e) {
                LOGGER.warn("I/O error caught while reading backend properties {}.", e);
            }
        }
        return properties;
    }
}

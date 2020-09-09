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

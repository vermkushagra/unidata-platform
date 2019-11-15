package org.unidata.mdm.core.service;

/**
 * @author Mikhail Mikhailov
 * Configuration service basic interface.
 */
public interface ConfigurationService {
    /**
     * Gets a string property value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    String getSystemStringProperty(String key);
    /**
     * Gets a string property value from the environment falling back to default, if not found.
     *
     * @param key the key
     * @param defaultValue fall back
     * @return value or null
     */
    String getSystemStringPropertyWithDefault(String key, String defaultValue);
    /**
     * Gets a boolean property value from the environment.
     *
     * @param key the key
     * @return value or null
     */
    Boolean getSystemBooleanProperty(String key);
    /**
     * Gets a boolean property value from the environment falling back to default, if not found.
     *
     * @param key the key
     * @param defaultValue fall back value
     * @return value or null
     */
    Boolean getSystemBooleanPropertyWithDefault(String key, Boolean defaultValue);
    /**
     * Gets an int property from the environment falling back to default, if not found.
     *
     * @param key the key
     * @return value or null
     */
    Integer getSystemIntProperty(String key);
    /**
     * Gets an int property  Int value from the environment falling back to default, if not found.
     *
     * @param key the key
     * @param defaultValue fall back value
     * @return value or null
     */
    Integer getSystemIntPropertyWithDefault(String key, Integer defaultValue);
}

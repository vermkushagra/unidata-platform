package org.unidata.mdm.system.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.system.configuration.SystemConfiguration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Mikhail Mikhailov
 * JSON related stuff.
 */
public class JsonUtils {

    public static final String DEFAULT_OBJECT_MAPPER_BEAN_NAME = "objectMapper";
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    /**
     * Default object mapper.
     */
    private static ObjectMapper objectMapper;
    /**
     * Constructor.
     */
    private JsonUtils() {
        super();
    }
    /**
     * Convenient init method.
     */
    public static void init() {
        try {
            objectMapper = SystemConfiguration.getBean(ObjectMapper.class); // From WAR spring configuration.
        } catch (Exception exc) {
            LOGGER.warn("Platform configuration bean GET. Exception caught.", exc);
        }
    }
    /**
     * Gets the mapper.
     * @return the mapper.
     */
    public static ObjectMapper getMapper() {
        return objectMapper;
    }
    /**
     * Reads a value from a string.
     * @param json the JASON string.
     * @param klass the target class
     * @return object or null
     */
    public static<T> T read(String json, Class<T> klass) {
        if (StringUtils.isNotBlank(json)) {
            try {
                return objectMapper.readValue(json, klass);
            } catch (JsonParseException e) {
                LOGGER.warn("Caught a 'JsonParseException' while reading classified value.", e);
            } catch (JsonMappingException e) {
                LOGGER.warn("Caught a 'JsonMappingException' while reading classified value.", e);
            } catch (IOException e) {
                LOGGER.warn("Caught a 'IOException' while reading classified value.", e);
            }
        }

        return null;
    }
    /**
     * Reads a value from a string.
     * @param json the JASON string.
     * @param ref the target ref
     * @return object or null
     */
    public static<T> T read(String json, TypeReference<T> ref) {
        if (StringUtils.isNotBlank(json)) {
            try {
                return objectMapper.readValue(json, ref);
            } catch (JsonParseException e) {
                LOGGER.warn("Caught a 'JsonParseException' while reading ref value.", e);
            } catch (JsonMappingException e) {
                LOGGER.warn("Caught a 'JsonMappingException' while reading ref value.", e);
            } catch (IOException e) {
                LOGGER.warn("Caught a 'IOException' while reading ref value.", e);
            }
        }

        return null;
    }
    /**
     * Reads a value from a file.
     * @param json the JASON file.
     * @param ref the target ref
     * @return object or null
     */
    public static<T> T read(File json, TypeReference<T> ref) {
        if (Objects.nonNull(json) && json.exists()) {
            try {
                return objectMapper.readValue(json, ref);
            } catch (JsonParseException e) {
                LOGGER.warn("Caught a 'JsonParseException' while reading file ref value.", e);
            } catch (JsonMappingException e) {
                LOGGER.warn("Caught a 'JsonMappingException' while reading file ref value.", e);
            } catch (IOException e) {
                LOGGER.warn("Caught a 'IOException' while reading file ref value.", e);
            }
        }

        return null;
    }
    /**
     * Writes a value as string.
     * @param value the value to write.
     * @return string
     */
    public static String write(Object value) {
        if (Objects.nonNull(value)) {
            try {
                return objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                LOGGER.warn("Caught a 'JsonProcessingException' while writing value.", e);
            }
        }
        return null;
    }
    /**
     * Writes a collection with empty check as string.
     * @param collection the collection to write
     * @return string
     */
    public static String write(Collection<?> collection) {
        if (CollectionUtils.isNotEmpty(collection)) {
            try {
                return objectMapper.writeValueAsString(collection);
            } catch (JsonProcessingException e) {
                LOGGER.warn("Caught a 'JsonProcessingException' while writing collection value.", e);
            }
        }
        return null;
    }
    /**
     * Writes a map with empty check as string.
     * @param map the map to write
     * @return string
     */
    public static String write(Map<?, ?> map) {
        if (MapUtils.isNotEmpty(map)) {
            try {
                return objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                LOGGER.warn("Caught a 'JsonProcessingException' while writing map value.", e);
            }
        }
        return null;
    }
}

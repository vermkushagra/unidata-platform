package org.unidata.mdm.system.type.rendering;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.Nullable;

/**
 * @author Mikhail Mikhailov on Jan 16, 2020
 */
public abstract class AbstractMapInputSource implements InputSource {

    private final Map<String, Object> values = new HashMap<>();

    public boolean isSet(String key) {
        return this.values.containsKey(key);
    }

    public void remove(String key) {
        this.values.remove(key);
    }

    public String getString(String key) {
        return validateAndGet(key, String.class);
    }

    public String getString(String key, String defaultString) {

        if (!isSet(key)) {
            return defaultString;
        }
        return getString(key);
    }

    public Long getLong(String key) {
        return validateAndGet(key, Long.class);
    }

    public Long getLong(String key, long defaultLong) {

        if (!isSet(key)) {
            return defaultLong;
        }
        return getLong(key);
    }

    public Integer getInt(String key) {
        return validateAndGet(key, Integer.class);
    }

    public Integer getInt(String key, int defaultInt) {

        if (!isSet(key)) {
            return defaultInt;
        }
        return getInt(key);
    }

    public Double getDouble(String key) {
        return validateAndGet(key, Double.class);
    }

    public Double getDouble(String key, double defaultDouble) {

        if (!isSet(key)) {
            return defaultDouble;
        }
        return getDouble(key);
    }

    public Boolean getBoolean(String key) {
        return validateAndGet(key, Boolean.class);
    }

    public Boolean getBoolean(String key, boolean defaultBoolean) {

        if (!isSet(key)) {
            return defaultBoolean;
        }
        return getBoolean(key);
    }

    @Nullable
    public<T> T get(String key, Class<T> type) {
        return validateAndGet(key, type);
    }

    public void putString(String key, @Nullable String value) {
        put(key, value);
    }

    public void putLong(String key, long value) {
        put(key, value);
    }

    public void putInt(String key, int value) {
        put(key, value);
    }

    public void putDouble(String key, double value) {
        put(key, value);
    }

    public void putBoolean(String key, boolean value) {
        put(key, value);
    }

    public void put(String key, Object value) {
        this.values.put(key, value);
    }

    @SuppressWarnings({ "unchecked" })
    private <T> T validateAndGet(String key, Class<T> type) {

        Object value = values.get(key);
        if (Objects.isNull(value)) {
            return null;
        }

        if (!type.isInstance(value)) {
            throw new ClassCastException("Value for key=[" + key + "] is not of type: [" + type + "], it is ["
                    + ("(" + value.getClass() + ")" + value) + "]");
        }

        return (T) value;
    }
}

package org.unidata.mdm.system.dto;

/**
 * The Class Param.
 */
public class Param {

    /** The key. */
    private String key;

    /** The value. */
    private String value;

    /**
     * Param constructor
     * @param key key
     * @param value value
     */
    public Param(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the new key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

}

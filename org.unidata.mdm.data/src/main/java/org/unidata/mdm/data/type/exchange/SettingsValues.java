package org.unidata.mdm.data.type.exchange;

/**
 * @author Mikhail Mikhailov
 * Simple settigs values enumeration.
 */
public enum SettingsValues {
    /**
     * Default charset.
     */
    DEF_CHARSET("charset"),
    /**
     * CSV field separator.
     */
    DEF_FIELD_SEPARATIOR("fieldSeparator");
    /**
     * Value.
     */
    private final String value;
    /**
     * Constructor.
     * @param v the value
     */
    private SettingsValues(String v) {
        this.value = v;
    }
    /**
     * @return the value
     */
    public String value() {
        return value;
    }
}

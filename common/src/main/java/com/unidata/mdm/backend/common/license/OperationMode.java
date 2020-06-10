package com.unidata.mdm.backend.common.license;

/**
 * @author Mikhail Mikhailov
 * Platform operation mode, according to the license.
 */
public enum OperationMode {
    /**
     * Production mode. Default, if nothing specified.
     */
    PRODUCTION_MODE("production", 1 << 0),
    /**
     * Development mode. Some features, not visible otherwise, will be available.
     */
    DEVELOPMENT_MODE("development", 1 << 1);
    /**
     * Safe create from tag value.
     * @param val the tag value
     * @return enum or null
     */
    public static OperationMode ofTagValue(String val) {

        for (int i = 0; i < values().length; i++) {
            if (values()[i].tag().equals(val)) {
                return values()[i];
            }
        }

        return null;
    }
    /**
     * Constructor.
     * @param tag this mode configuration tag
     */
    private OperationMode(String tag, int mask) {
        this.tag = tag;
        this.mask = mask;
    }
    /**
     * Config. tag value.
     */
    private final String tag;
    /**
     * Bit mask.
     */
    private final int mask;
    /**
     * @return the configuration tag
     */
    public String tag() {
        return tag;
    }
    /**
     * @return the mask
     */
    public int mask() {
        return mask;
    }
    /**
     * Mask all.
     * @return all mask
     */
    public static int maskAll() {
        return PRODUCTION_MODE.mask() | DEVELOPMENT_MODE.mask();
    }
}

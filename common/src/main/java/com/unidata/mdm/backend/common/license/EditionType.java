package com.unidata.mdm.backend.common.license;
/**
 * @author Mikhail Mikhailov
 * Edition type. Platform features turned on or off depending on the type.
 */
public enum EditionType {
    /**
     * Standard edition.
     */
    STANDARD_EDITION("standard", 1 << 0),
    /**
     * High performance edition.
     */
    HP_EDITION("hpe", 1 << 1);
    /**
     * Safe create from tag value.
     * @param val the tag value
     * @return enum or null
     */
    public static EditionType ofTagValue(String val) {

        for (int i = 0; i < values().length; i++) {
            if (values()[i].tag().equals(val)) {
                return values()[i];
            }
        }

        return null;
    }
    /**
     * Constructor.
     * @param tag this edition type configuration tag
     */
    private EditionType(String tag, int mark) {
        this.tag = tag;
        this.mask = mark;
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
        return STANDARD_EDITION.mask() | HP_EDITION.mask();
    }
}

package org.unidata.mdm.search.type;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * Type of a simple value.
 */
public enum FieldType {
    BOOLEAN,
    DATE,
    TIME,
    TIMESTAMP,
    INSTANT,
    INTEGER,
    NUMBER,
    STRING,
    COMPOSITE,  // Special field type, containing other fields.
    ANY;        // Special field type, marking an untyped SE. This is used for queries. FIXME: Rename to NONE, what is more precise.

    /**
     * Creates instance from JAXB friendly value
     * @param v the value
     * @return enum instamce
     */
    public static FieldType fromValue(String v) {

        for (FieldType c: FieldType.values()) {
            if (c.name().equalsIgnoreCase(v)) {
                return c;
            }
        }

        throw new IllegalArgumentException(v);
    }
}

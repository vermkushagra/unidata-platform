package org.unidata.mdm.data.type.data;

/**
 * @author Mikhail Mikhailov
 * Relation type.
 * This is a duplication of the RelType enum, just to have the type value unrelated.
 */
public enum RelationType {
    /**
     * Reference relation.
     */
    REFERENCES,
    /**
     * Containment relation.
     */
    CONTAINS,
    /**
     * Many to many relation.
     */
    MANY_TO_MANY;
    /**
     * Convenient value method.
     * @return value / name.
     */
    public String value() {
        return name();
    }
    /**
     * Covenient from value wmethod.
     * @param v value / name
     * @return parsed value
     */
    public static RelationType fromValue(String v) {
        return valueOf(v);
    }
}

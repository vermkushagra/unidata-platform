package org.unidata.mdm.search.type;

/**
 * Index field.
 */
public interface IndexField {
    /**
     * @return filed name in search index
     */
    String getName();
    /**
     * @return search type
     */
    IndexType getType();
    /**
     * Gets the field's data type.
     * @return
     */
    FieldType getFieldType();
}

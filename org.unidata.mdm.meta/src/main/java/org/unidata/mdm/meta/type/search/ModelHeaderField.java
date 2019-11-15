package org.unidata.mdm.meta.type.search;

import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.IndexField;
import org.unidata.mdm.search.type.IndexType;

/**
 * @author Mikhail Mikhailov
 * Classifier data fields.
 */
public enum ModelHeaderField implements IndexField {
    /**
     * Parent etalon id.
     */
    FIELD_SEARCH_OBJECTS("$search_objects", FieldType.STRING),
    /**
     * Parent record etalon id.
     */
    FIELD_DISPLAY_NAME("displayName", FieldType.STRING),
    /**
     * Period id.
     */
    FIELD_NAME("name", FieldType.STRING),
    /**
     * Classifiers digest
     */
    FIELD_VALUE("value", FieldType.STRING);
    /**
     * Field.
     */
    private final String field;
    /**
     * The type.
     */
    private final FieldType type;
    /**
     * Constructor.
     * @param field the field name
     */
    ModelHeaderField(String field, FieldType type) {
        this.field = field;
        this.type = type;
    }
    /**
     * @return the field
     */
    @Override
    public String getName() {
        return field;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return type;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexType getType() {
        return ModelIndexType.MODEL;
    }
}

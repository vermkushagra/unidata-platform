package org.unidata.mdm.meta.type.search;

import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.IndexField;
import org.unidata.mdm.search.type.IndexType;

/**
 * Header mark fields for indexed relation.
 */
public enum RelationHeaderField implements IndexField {
    /**
     * Left end of relation
     */
    FIELD_ETALON_ID("$etalon_id", FieldType.STRING),
    /**
     * Left end of relation
     */
    FIELD_FROM_ETALON_ID("$etalon_id_from", FieldType.STRING),
    /**
     * Right end of relation
     */
    FIELD_TO_ETALON_ID("$etalon_id_to", FieldType.STRING),
    /**
     * Period id.
     */
    FIELD_PERIOD_ID("$period_id", FieldType.STRING),
    /**
     * 'from' validity range mark.
     */
    FIELD_FROM("$from", FieldType.TIMESTAMP),
    /**
     * 'to' validity range mark
     */
    FIELD_TO("$to", FieldType.TIMESTAMP),
    /**
     * Type of relation {@link com.unidata.mdm.meta.RelType}
     */
    REL_TYPE("$type", FieldType.STRING),
    /**
     * User defined relation name
     */
    REL_NAME("$rel_name", FieldType.STRING),
    /**
     * 'created_at' creation date
     */
    FIELD_CREATED_AT("$created_at", FieldType.TIMESTAMP),
    /**
     * 'updated_at' date of the last update
     */
    FIELD_UPDATED_AT("$updated_at", FieldType.TIMESTAMP),
    /**
     * Is pending mark.
     */
    FIELD_PENDING("$pending", FieldType.BOOLEAN),
    /**
     * Is published mark.
     */
    FIELD_PUBLISHED("$published", FieldType.BOOLEAN),
    /**
     * Is deleted mark.
     */
    FIELD_DELETED("$deleted", FieldType.BOOLEAN),
    /**
     * Is direct index record or not.
     */
    FIELD_DIRECT("$direct", FieldType.STRING);
    /**
     * The name.
     */
    private final String field;
    /**
     * The type.
     */
    private final FieldType type;
    /**
     * Constructor.
     * @param field
     */
    RelationHeaderField(String field, FieldType type) {
        this.field = field;
        this.type = type;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return type;
    }
    /**
     * @return the field
     */
    @Override
    public String getName() {
        return field;
    }

    @Override
    public IndexType getType() {
        return EntityIndexType.RELATION;
    }
}

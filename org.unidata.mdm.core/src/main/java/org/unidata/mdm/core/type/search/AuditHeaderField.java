package org.unidata.mdm.core.type.search;

import org.unidata.mdm.core.type.audit.Event;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.IndexField;
import org.unidata.mdm.search.type.IndexType;

public enum AuditHeaderField implements IndexField {

    ETALON_ID(Event.ETALON_ID, FieldType.STRING),

    ORIGIN_ID(Event.ORIGIN_ID, FieldType.STRING),

    EXTERNAL_ID(Event.EXTERNAL_ID, FieldType.STRING),

    OPERATION_ID(Event.OPERATION_ID, FieldType.STRING),

    SERVER_IP(Event.SERVER_IP, FieldType.STRING),

    CLIENT_IP(Event.CLIENT_IP, FieldType.STRING),

    ENDPOINT(Event.ENDPOINT, FieldType.STRING),

    DETAILS(Event.DETAILS, FieldType.STRING),

    DATE(Event.DATE, FieldType.TIMESTAMP),

    USER(Event.USER, FieldType.STRING),

    ENTITY(Event.ENTITY, FieldType.STRING),

    SUB_SYSTEM(Event.SUB_SYSTEM, FieldType.STRING),

    ACTION(Event.ACTION, FieldType.STRING),

    SOURCE_SYSTEM(Event.SOURCE_SYSTEM, FieldType.STRING),

    SUCCESS(Event.SUCCESS, FieldType.BOOLEAN),

    TASK_ID(Event.TASK_ID, FieldType.STRING);
    /**
     * The field name.
     */
    private final String field;
    /**
     * The type.
     */
    private final FieldType type;
    /**
     * Constructor.
     * @param field the name
     * @param type the type
     */
    AuditHeaderField(String field, FieldType type) {
        this.field = field;
        this.type = type;
    }
    /**
     * {@inheritDoc}
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
        return AuditIndexType.AUDIT;
    }
}

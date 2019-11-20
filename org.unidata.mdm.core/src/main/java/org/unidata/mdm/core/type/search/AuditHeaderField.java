package org.unidata.mdm.core.type.search;

import org.unidata.mdm.core.dto.EnhancedAuditEvent;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.IndexField;
import org.unidata.mdm.search.type.IndexType;

public enum AuditHeaderField implements IndexField {

    TYPE(EnhancedAuditEvent.TYPE_FIELD, FieldType.STRING),
    PARAMETERS(EnhancedAuditEvent.PARAMETERS_FIELD, FieldType.STRING),
    SUCCESS(EnhancedAuditEvent.SUCCESS_FIELD, FieldType.BOOLEAN),
    USER(EnhancedAuditEvent.USER_FIELD, FieldType.STRING),
    CLIENT_IP(EnhancedAuditEvent.CLIENT_IP_FIELD, FieldType.STRING),
    SERVER_IP(EnhancedAuditEvent.SERVER_IP_FIELD, FieldType.STRING),
    WHEN(EnhancedAuditEvent.WHEN_FIELD, FieldType.TIMESTAMP);

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

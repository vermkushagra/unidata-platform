/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.core.type.search;

import org.unidata.mdm.core.dto.EnrichedAuditEvent;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.IndexField;
import org.unidata.mdm.search.type.IndexType;

public enum AuditHeaderField implements IndexField {

    TYPE(EnrichedAuditEvent.TYPE_FIELD, FieldType.STRING),
    PARAMETERS(EnrichedAuditEvent.PARAMETERS_FIELD, FieldType.STRING),
    SUCCESS(EnrichedAuditEvent.SUCCESS_FIELD, FieldType.BOOLEAN),
    LOGIN(EnrichedAuditEvent.LOGIN_FIELD, FieldType.STRING),
    CLIENT_IP(EnrichedAuditEvent.CLIENT_IP_FIELD, FieldType.STRING),
    SERVER_IP(EnrichedAuditEvent.SERVER_IP_FIELD, FieldType.STRING),
    ENDPOINT(EnrichedAuditEvent.ENDPOINT_FIELD, FieldType.STRING),
    WHEN_HAPPENED(EnrichedAuditEvent.WHEN_HAPPENED_FIELD, FieldType.TIMESTAMP);

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

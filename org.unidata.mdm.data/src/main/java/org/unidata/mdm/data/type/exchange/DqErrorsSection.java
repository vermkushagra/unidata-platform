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

package org.unidata.mdm.data.type.exchange;

import java.io.Serializable;

import org.unidata.mdm.data.type.exchange.db.DbJsonDqErrorsSection;
import org.unidata.mdm.data.type.exchange.db.DbSingleRowDqErrorsSection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Dq errors section
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({ @JsonSubTypes.Type(value = DbJsonDqErrorsSection.class, name = "DB_JSON"),
                      @JsonSubTypes.Type(value = DbSingleRowDqErrorsSection.class, name = "DB_SINGLE") })
public class DqErrorsSection implements Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6121640813210053268L;
    /**
     * entity name field
     */
    private String entityNameField;

    /**
     * external id field
     */
    private String externalIdField;

    /**
     * source system field
     */
    private String sourceSystemField;

    /**
     * etalon id field
     */
    private String etalonIdField;

    public String getEntityNameField() {
        return entityNameField;
    }

    public void setEntityNameField(String entityNameField) {
        this.entityNameField = entityNameField;
    }

    public String getExternalIdField() {
        return externalIdField;
    }

    public void setExternalIdField(String externalIdField) {
        this.externalIdField = externalIdField;
    }

    public String getSourceSystemField() {
        return sourceSystemField;
    }

    public void setSourceSystemField(String sourceSystemField) {
        this.sourceSystemField = sourceSystemField;
    }

    public String getEtalonIdField() {
        return etalonIdField;
    }

    public void setEtalonIdField(String etalonIdField) {
        this.etalonIdField = etalonIdField;
    }
}

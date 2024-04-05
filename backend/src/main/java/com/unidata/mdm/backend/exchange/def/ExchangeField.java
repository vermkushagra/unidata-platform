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

/**
 *
 */
package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.unidata.mdm.backend.exchange.def.csv.CsvExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;

/**
 * @author Mikhail Mikhailov
 * JSON mapping for field exchange.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = CsvExchangeField.class, name = "CSV"),
    @Type(value = DbExchangeField.class, name = "DB")
})
public class ExchangeField implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2787748820197558002L;
    /**
     * Name of the field.
     */
    private String name;
    /**
     * A transformer.
     */
    private List<ExchangeFieldTransformer> transformations;
    /**
     * Code attribute for lookup entities.
     */
    private boolean codeAttribute;
    /**
     * Name of code attribute or alias code attribute
     */
    private String refToAttribute;
    /**
     * Nested attributes expansion rules.
     */
    private List<ComplexAttributeExpansion> expansions;
    /**
     * Direct value, not part of import.
     */
    private Object value;
    /**
     * Constructor.
     */
    public ExchangeField() {
        super();
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the transformations
     */
    public List<ExchangeFieldTransformer> getTransformations() {
        return transformations;
    }

    /**
     * @param transformations the transformations to set
     */
    public void setTransformations(List<ExchangeFieldTransformer> transform) {
        this.transformations = transform;
    }

    /**
     * @return the codeAttribute
     */
    public boolean isCodeAttribute() {
        return codeAttribute;
    }

    /**
     * @param codeAttribute the codeAttribute to set
     */
    public void setCodeAttribute(boolean codeAttribute) {
        this.codeAttribute = codeAttribute;
    }

    /**
     * @return the expansions
     */
    public List<ComplexAttributeExpansion> getExpansions() {
        return expansions;
    }

    /**
     * @param expansions the expansions to set
     */
    public void setExpansions(List<ComplexAttributeExpansion> expansions) {
        this.expansions = expansions;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return name of code attribute or alias code attribute
     */
    public String getRefToAttribute() {
        return refToAttribute;
    }

    /**
     * @param refToAttribute name of code attribute or alias code attribute
     */
    public void setRefToAttribute(String refToAttribute) {
        this.refToAttribute = refToAttribute;
    }
}
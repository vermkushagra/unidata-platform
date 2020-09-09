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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.unidata.mdm.backend.exchange.def.csv.CsvRelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;

/**
 * @author Mikhail Mikhailov
 *
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = CsvRelatesToRelation.class, name = "CSV"),
    @Type(value = DbRelatesToRelation.class, name = "DB")
})
public class RelatesToRelation extends ExchangeRelation {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5817772683349949496L;

    /**
     * Fields.
     */
    private List<ExchangeField> fields;

    /**
     * Version range.
     */
    private VersionRange versionRange;

    /**
     * The TO natural key.
     */
    private NaturalKey toNaturalKey;

    /**
     * The TO system key.
     */
    private SystemKey toSystemKey;

    /**
     * if presented, describe alias unique attribute in entity - which will be used for resolving "to" side of reference!
     */
    private String toEntityAttributeName;

    /**
     * The TO source system.
     */
    private String toSourceSystem;

    /**
     * Constructor.
     */
    public RelatesToRelation() {
        super();
    }

    /**
     * @return the fields
     */
    public List<ExchangeField> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(List<ExchangeField> fields) {
        this.fields = fields;
    }

    /**
     * @return the versionRange
     */
    public VersionRange getVersionRange() {
        return versionRange;
    }

    /**
     * @param versionRange the versionRange to set
     */
    public void setVersionRange(VersionRange versionRange) {
        this.versionRange = versionRange;
    }

    /**
     * @return the toNaturalKey
     */
    public NaturalKey getToNaturalKey() {
        return toNaturalKey;
    }

    /**
     * @param toNaturalKey the toNaturalKey to set
     */
    public void setToNaturalKey(NaturalKey toKey) {
        this.toNaturalKey = toKey;
    }

    /**
     * @return the toSystemKey
     */
    public SystemKey getToSystemKey() {
        return toSystemKey;
    }

    /**
     * @param toSystemKey the toSystemKey to set
     */
    public void setToSystemKey(SystemKey toSystemKey) {
        this.toSystemKey = toSystemKey;
    }

    /**
     * @return the toSourceSystem
     */
    public String getToSourceSystem() {
        return toSourceSystem;
    }

    /**
     * @param toSourceSystem the toSourceSystem to set
     */
    public void setToSourceSystem(String toSourceSystem) {
        this.toSourceSystem = toSourceSystem;
    }

    public String getToEntityAttributeName() {
        return toEntityAttributeName;
    }

    public void setToEntityAttributeName(String toEntityAttributeName) {
        this.toEntityAttributeName = toEntityAttributeName;
    }
    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public boolean isRelTo() {
        return true;
    }
}

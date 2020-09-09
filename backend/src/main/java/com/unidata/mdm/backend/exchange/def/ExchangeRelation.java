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

package com.unidata.mdm.backend.exchange.def;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExchangeRelation implements ExchangeObject {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8741928702454493823L;
    /**
     * Relation name.
     */
    private String relation;
    /**
     * Parent reference field.
     */
    private NaturalKey fromNaturalKey;
    /**
     * Parent reference field.
     */
    private SystemKey fromSystemKey;
    /**
     * The update mark.
     */
    private UpdateMark updateMark;
    /**
     * Has historical records.
     */
    private boolean multiVersion;
    /**
     * Max records.
     */
    private long maxRecordCount = 0L;

    /**
     * Constructor.
     */
    public ExchangeRelation() {
        super();
    }

    /**
     * @return the relation
     */
    public String getRelation() {
        return relation;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation(String relation) {
        this.relation = relation;
    }

    /**
     * @return the fromNaturalKey
     */
    public NaturalKey getFromNaturalKey() {
        return fromNaturalKey;
    }

    /**
     * @param fromNaturalKey the fromNaturalKey to set
     */
    public void setFromNaturalKey(NaturalKey parentKeyField) {
        this.fromNaturalKey = parentKeyField;
    }

    /**
     * @return the fromSystemKey
     */
    public SystemKey getFromSystemKey() {
        return fromSystemKey;
    }

    /**
     * @param fromSystemKey the fromSystemKey to set
     */
    public void setFromSystemKey(SystemKey fromSystemKey) {
        this.fromSystemKey = fromSystemKey;
    }

    /**
     * @return the updateMark
     */
    public UpdateMark getUpdateMark() {
        return updateMark;
    }

    /**
     * @param updateMark the updateMark to set
     */
    public void setUpdateMark(UpdateMark updateMark) {
        this.updateMark = updateMark;
    }

    /**
     * @return the historical
     */
    public boolean isMultiVersion() {
        return multiVersion;
    }

    /**
     * @param historical the historical to set
     */
    public void setMultiVersion(boolean historical) {
        this.multiVersion = historical;
    }

    /**
     * @return the maxRecordCount
     */
    public long getMaxRecordCount() {
        return maxRecordCount;
    }

    /**
     * @param maxRecordCount the maxRecordCount to set
     */
    public void setMaxRecordCount(long maxRecordCount) {
        this.maxRecordCount = maxRecordCount;
    }
    /**
     * Is containment?
     * @return true, if so, false otherwise
     */
    @JsonIgnore
    public boolean isContainment() {
        return false;
    }
    /**
     * Is rel to?
     * @return true, if so, false otherwise
     */
    @JsonIgnore
    public boolean isRelTo() {
        return false;
    }
}
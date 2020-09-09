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

package com.unidata.mdm.backend.service.job.exchange.in.types;

import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * @author Mikhail Mikhailov
 * Relation import.
 */
public class ImportRelationSet extends ImportDataSet {
    /**
     * The origin key.
     */
    private OriginKey fromOriginKey;
    /**
     * Etalon key.
     */
    private EtalonKey fromEtalonKey;
    /**
     * The origin key.
     */
    private OriginKey toOriginKey;
    /**
     * Etalon key.
     */
    private EtalonKey toEtalonKey;
    /**
     * Relation name.
     */
    private String relationName;
    /**
     * Upsert relations result.
     */
    private UpsertRelationsRequestContext relationsUpsert;
    /**
     * Delete relations result.
     */
    private DeleteRelationsRequestContext relationsDelete;
    /**
     * Constructor.
     * @param data
     */
    public ImportRelationSet(DataRecord data) {
        super(data);
    }
    /**
     * @return the fromOriginKey
     */
    public OriginKey getFromOriginKey() {
        return fromOriginKey;
    }
    /**
     * @param fromOriginKey the fromOriginKey to set
     */
    public void setFromOriginKey(OriginKey fromOriginKey) {
        this.fromOriginKey = fromOriginKey;
    }
    /**
     * @return the fromEtalonKey
     */
    public EtalonKey getFromEtalonKey() {
        return fromEtalonKey;
    }
    /**
     * @param fromEtalonKey the fromEtalonKey to set
     */
    public void setFromEtalonKey(EtalonKey fromEtalonKey) {
        this.fromEtalonKey = fromEtalonKey;
    }
    /**
     * @return the toOriginKey
     */
    public OriginKey getToOriginKey() {
        return toOriginKey;
    }
    /**
     * @param toOriginKey the toOriginKey to set
     */
    public void setToOriginKey(OriginKey toOriginKey) {
        this.toOriginKey = toOriginKey;
    }
    /**
     * @return the toEtalonKey
     */
    public EtalonKey getToEtalonKey() {
        return toEtalonKey;
    }
    /**
     * @param toEtalonKey the toEtalonKey to set
     */
    public void setToEtalonKey(EtalonKey toEtalonKey) {
        this.toEtalonKey = toEtalonKey;
    }
    /**
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }
    /**
     * @param relationName the relationName to set
     */
    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRelation() {
        return true;
    }
    /**
     * @return the relationsUpsert
     */
    public UpsertRelationsRequestContext getRelationsUpsert() {
        return relationsUpsert;
    }
    /**
     * @param relationsUpsert the relationsUpsert to set
     */
    public void setRelationsUpsert(UpsertRelationsRequestContext relationsUpsert) {
        this.relationsUpsert = relationsUpsert;
    }
    /**
     * @return the relationsDeelete
     */
    public DeleteRelationsRequestContext getRelationsDelete() {
        return relationsDelete;
    }
    /**
     * @param relationsDeelete the relationsDeelete to set
     */
    public void setRelationsDelete(DeleteRelationsRequestContext relationsDeelete) {
        this.relationsDelete = relationsDeelete;
    }
}

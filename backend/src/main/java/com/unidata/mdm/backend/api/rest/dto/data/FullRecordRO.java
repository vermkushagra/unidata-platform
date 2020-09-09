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

package com.unidata.mdm.backend.api.rest.dto.data;

/**
 * @author Dmitry Kopin on 19.06.2018.
 *         Full record for 'atomic' upsert
 */
public class FullRecordRO {
    /**
     * Data record
     */
    private EtalonRecordRO dataRecord;
    /**
     * 'reference' relations
     */
    private RelationReferencesWrapperRO relationReference;
    /**
     * 'contains' relations
     */
    private RelationContainsWrapperRO relationContains;
    /**
     * 'm2m' relations
     */
    private RelationManyToManyWrapperRO relationManyToMany;


    public EtalonRecordRO getDataRecord() {
        return dataRecord;
    }

    public void setDataRecord(EtalonRecordRO dataRecord) {
        this.dataRecord = dataRecord;
    }

    public RelationReferencesWrapperRO getRelationReference() {
        return relationReference;
    }

    public void setRelationReference(RelationReferencesWrapperRO relationReference) {
        this.relationReference = relationReference;
    }

    public RelationContainsWrapperRO getRelationContains() {
        return relationContains;
    }

    public void setRelationContains(RelationContainsWrapperRO relationContains) {
        this.relationContains = relationContains;
    }

    public RelationManyToManyWrapperRO getRelationManyToMany() {
        return relationManyToMany;
    }

    public void setRelationManyToMany(RelationManyToManyWrapperRO relationManyToMany) {
        this.relationManyToMany = relationManyToMany;
    }
}

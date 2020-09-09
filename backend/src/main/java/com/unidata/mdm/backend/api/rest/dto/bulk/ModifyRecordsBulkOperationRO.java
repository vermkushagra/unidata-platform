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
package com.unidata.mdm.backend.api.rest.dto.bulk;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRelationToRO;
import com.unidata.mdm.backend.common.types.BulkOperationType;

/**
 * REST Parameter class specific for modify bulk operation.
 *
 * @author Mikhail Mikhailov
 * 
 */
public class ModifyRecordsBulkOperationRO extends BulkOperationBaseRO {

    /** The etalon record RO. */
    private EtalonRecordRO etalonRecordRO;

    /** The relations. */
    private List<EtalonRelationToRO> relations;

    /**
     * Constructor.
     */
    public ModifyRecordsBulkOperationRO() {
        super();
    }

    /**
     * Gets the etalon record RO.
     *
     * @return the etalon record RO
     */
    public EtalonRecordRO getEtalonRecordRO() {
        return etalonRecordRO;
    }

    /**
     * Sets the etalon record RO.
     *
     * @param etalonRecordRO the new etalon record RO
     */
    public void setEtalonRecordRO(EtalonRecordRO etalonRecordRO) {
        this.etalonRecordRO = etalonRecordRO;
    }

    /**
     * Gets the relations.
     *
     * @return the relations
     */
    public List<EtalonRelationToRO> getRelations() {
        return relations;
    }

    /**
     * Sets the relations.
     *
     * @param relations the new relations
     */
    public void setRelations(List<EtalonRelationToRO> relations) {
        this.relations = relations;
    }

    /**
     * Bulk operation type.
     *
     * @return type
     */
    @JsonIgnore
    public BulkOperationType getType() {
        return BulkOperationType.MODIFY_RECORDS;
    }
}

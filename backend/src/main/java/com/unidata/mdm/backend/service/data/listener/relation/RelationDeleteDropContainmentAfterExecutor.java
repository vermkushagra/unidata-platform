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

package com.unidata.mdm.backend.service.data.listener.relation;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for deleting containments of a relation.
 */
public class RelationDeleteDropContainmentAfterExecutor implements DataRecordAfterExecutor<DeleteRelationRequestContext> {
    /**
     * Record component. Index updates are processed later.
     */
    @Autowired
    private EtalonRecordsComponent etalonRecordsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
            if (relation.getRelType() != RelType.CONTAINS) {
                return true;
            }

            DeleteRequestContext dCtx = ctx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT);
            etalonRecordsComponent.deleteEtalon(dCtx);
            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }
}

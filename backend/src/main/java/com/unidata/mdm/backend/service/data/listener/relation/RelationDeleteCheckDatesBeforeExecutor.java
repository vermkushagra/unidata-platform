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

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.data.listener.AbstractValidityRangeCheckExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Single point for checking and possible adjustment of from - to dates.
 */
public class RelationDeleteCheckDatesBeforeExecutor
    extends AbstractValidityRangeCheckExecutor<DeleteRelationRequestContext>
    implements DataRecordBeforeExecutor<DeleteRelationRequestContext> {
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRelationRequestContext ctx) {

        if (!ctx.isInactivatePeriod()) {
            return true;
        }

        MeasurementPoint.start();
        try {

            Date factoryValidFrom = null;
            Date factoryValidTo = null;

            RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
            // Take settings from the 'to' side for containments.
            // Check against system dates only otherwise.
            if (relation.getRelType() == RelType.CONTAINS) {

                EntityWrapper ew = metaModelService.getValueById(relation.getToEntity(), EntityWrapper.class);
                if (Objects.nonNull(ew)) {
                    factoryValidFrom = ew.getValidityStart();
                    factoryValidTo = ew.getValidityEnd();
                }
            }

            super.execute(ctx, factoryValidFrom, factoryValidTo);
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }
}

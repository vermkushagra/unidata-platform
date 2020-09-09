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

package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.data.listener.AbstractValidityRangeCheckExecutor;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;

/**
 * @author Mikhail Mikhailov
 * Single point for checking and possible adjustment of from - to dates.
 */
public class DataRecordDeleteCheckDatesBeforeExecutor
    extends AbstractValidityRangeCheckExecutor<DeleteRequestContext>
    implements DataRecordBeforeExecutor<DeleteRequestContext> {
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {

        if (!ctx.isInactivatePeriod()) {
            return true;
        }

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();
            Date factoryValidFrom = null;
            Date factoryValidTo = null;

            EntityWrapper ew = metaModelService.getValueById(keys.getEntityName(), EntityWrapper.class);
            if (Objects.nonNull(ew)) {
                factoryValidFrom = ew.getValidityStart();
                factoryValidTo = ew.getValidityEnd();
            } else {
                LookupEntityWrapper lew = metaModelService.getValueById(keys.getEntityName(), LookupEntityWrapper.class);
                factoryValidFrom = lew.getValidityStart();
                factoryValidTo = lew.getValidityEnd();
            }

            super.execute(ctx, factoryValidFrom, factoryValidTo);
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }
}

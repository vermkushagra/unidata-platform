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

package org.unidata.mdm.data.service.segments.records;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.segments.ValidityRangeCheckSupport;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 * Single point for checking and possible adjustment of from - to dates.
 */
@Component(RecordUpsertPeriodCheckExecutor.SEGMENT_ID)
public class RecordUpsertPeriodCheckExecutor
    extends Point<UpsertRequestContext>
    implements RecordIdentityContextSupport, ValidityRangeCheckSupport<UpsertRequestContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_PERIOD_CHECK]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.period.check.description";
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public RecordUpsertPeriodCheckExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            String entityName = selectEntityName(ctx);

            EntityModelElement ew = metaModelService.getEntityModelElementById(entityName);
            if (Objects.isNull(ew)) {
                return;
            }

            execute(ctx, ew.getValidityStart(), ew.getValidityEnd());
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}

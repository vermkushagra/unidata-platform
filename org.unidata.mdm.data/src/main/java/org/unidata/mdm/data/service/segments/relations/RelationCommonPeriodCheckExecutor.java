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

package org.unidata.mdm.data.service.segments.relations;

import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.context.MutableValidityRangeContext;
import org.unidata.mdm.data.context.RelationIdentityContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.segments.ValidityRangeCheckSupport;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 * Check dates for relations.
 */
@Component(RelationCommonPeriodCheckExecutor.SEGMENT_ID)
public class RelationCommonPeriodCheckExecutor
    extends Point<PipelineInput>
    implements ValidityRangeCheckSupport<MutableValidityRangeContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_COMMON_PERIOD_CHECK]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.common.check.period.description";
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public RelationCommonPeriodCheckExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(PipelineInput ctx) {

        MeasurementPoint.start();
        try {

            RelationIdentityContext target = ctx.narrow();

            Date factoryValidFrom = null;
            Date factoryValidTo = null;

            // Take settings from the 'to' side for containments.
            // Check against system dates only otherwise.
            if (target.relationType() == RelationType.CONTAINS) {

                RelationDef def = metaModelService.getRelationById(target.relationName());
                EntityInfoHolder ew = metaModelService.getValueById(def.getToEntity(), EntityInfoHolder.class);
                if (Objects.nonNull(ew)) {
                    factoryValidFrom = ew.getValidityStart();
                    factoryValidTo = ew.getValidityEnd();
                }
            }

            execute(ctx.narrow(), factoryValidFrom, factoryValidTo);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return RelationIdentityContext.class.isAssignableFrom(start.getInputTypeClass())
            && MutableValidityRangeContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}

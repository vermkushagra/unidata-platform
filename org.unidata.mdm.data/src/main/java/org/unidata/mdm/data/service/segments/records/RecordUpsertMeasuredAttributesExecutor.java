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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.segments.MeasurementMetaSettingSupport;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.impl.MeasuredAttributeValueConverter;
import org.unidata.mdm.system.type.pipeline.PipelineInput;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * Normalize measured attributes before upserting record
 */
@Component(RecordUpsertMeasuredAttributesExecutor.SEGMENT_ID)
public class RecordUpsertMeasuredAttributesExecutor extends Point<PipelineInput>
        implements RecordIdentityContextSupport, MeasurementMetaSettingSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_MEASURED_ATTRIBUTES]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.measured.attributes.description";
    /**
     * Measurement service
     */
    @Autowired
    private MetaMeasurementService metaMeasurementService;
    /**
     * Meta model service
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Measured attribute converter
     */
    @Autowired
    private MeasuredAttributeValueConverter measuredAttributeValueConverter;

    public RecordUpsertMeasuredAttributesExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    public boolean execute(DataRecord record, String entityName) {
        MeasurementPoint.start();
        try {
            if (record != null) {
                measuredAttributeValueConverter.enrichMeasuredAttributesByBase(record);
                processDataRecord(record, entityName, StringUtils.EMPTY);
            }
            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaMeasurementService measurementService() {
        return metaMeasurementService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModelService modelService() {
        return metaModelService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void point(PipelineInput ctx) {

        if (ctx instanceof UpsertRequestContext) {
            UpsertRequestContext urCtx = (UpsertRequestContext) ctx;
            execute(urCtx.getRecord(), selectEntityName(urCtx));
        } else if (ctx instanceof UpsertRelationRequestContext) {
            UpsertRelationRequestContext urCtx = (UpsertRelationRequestContext) ctx;
            execute(urCtx.getRelation(), urCtx.relationName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass())
            || UpsertRelationRequestContext.class.isAssignableFrom(start.getInputTypeClass()) ;
    }
}

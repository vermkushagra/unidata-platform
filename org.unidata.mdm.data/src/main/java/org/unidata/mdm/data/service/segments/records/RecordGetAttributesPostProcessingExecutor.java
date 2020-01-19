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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.context.FetchLargeObjectRequestContext;
import org.unidata.mdm.core.service.LargeObjectsServiceComponent;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.Attribute.AttributeType;
import org.unidata.mdm.core.type.data.BinaryLargeValue;
import org.unidata.mdm.core.type.data.CharacterLargeValue;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.SimpleAttribute.DataType;
import org.unidata.mdm.core.type.data.impl.AbstractLargeValue;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.core.type.data.impl.SimpleAttributesDiff;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.segments.AttributesPostProcessingSupport;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.impl.EtalonRecordImpl;
import org.unidata.mdm.meta.service.LookupService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 *
 */
@Component(RecordGetAttributesPostProcessingExecutor.SEGMENT_ID)
public class RecordGetAttributesPostProcessingExecutor extends Point<GetRequestContext>
    implements AttributesPostProcessingSupport {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_GET_ATTRIBUTES_POSTPROCESSING]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.get.post.process.description";

    @Autowired
    private LargeObjectsServiceComponent largeObjectsServiceComponent;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;


    @Autowired
    protected LookupService lookupService;
    /**
     * Constructor.
     */
    public RecordGetAttributesPostProcessingExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaModelService metaModelService() {
        return metaModelService;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public LookupService lookupService() {
        return lookupService;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void point(GetRequestContext ctx) {

        MeasurementPoint.start();
        try {

            Timeline<OriginRecord> timeline = ctx.currentTimeline();
            EtalonRecord etalon = timeline.isSingleton() ? timeline.first().getCalculationResult() : null;
            List<OriginRecord> origins =  timeline.isSingleton() ? timeline.first().toValueList() : Collections.emptyList();
            List<EtalonRecord> etalons = new ArrayList<>();

            if (etalon != null) {
                etalons.add(etalon);
            }

            SimpleAttributesDiff diffToDraft = ctx.diffToDraft();
            if (CollectionUtils.isNotEmpty(etalons) && Objects.nonNull(diffToDraft) && !diffToDraft.isEmpty()) {

                // Create fake etalon record, just to set attributes display values
                // Only first level is processed
                SerializableDataRecord sdr = new SerializableDataRecord();
                sdr.addAll(diffToDraft.asAttributesTable().values().stream()
                        .flatMap(m -> m.values().stream())
                        .collect(Collectors.toList()));

                EtalonRecord diffEtalon = new EtalonRecordImpl()
                        .withDataRecord(sdr)
                        .withInfoSection(etalons.get(0).getInfoSection());

                etalons.add(diffEtalon);
            }

            if (CollectionUtils.isEmpty(etalons) && CollectionUtils.isEmpty(origins)) {
                return;
            }

            enrichRecordsIfNeed(etalons, ctx);

            processRecords(etalons, origins);
        } finally {
            MeasurementPoint.stop();
        }
    }

    private void enrichRecordsIfNeed(List<EtalonRecord> etalons, GetRequestContext ctx) {

        if(ctx.isFetchLargeObjects()){
            etalons.stream()
                    .flatMap(etalonRecord -> etalonRecord.getAllAttributesRecursive().stream())
                    .filter(attribute -> attribute.getAttributeType() == AttributeType.SIMPLE)
                    .map(Attribute::<SimpleAttribute<?>>narrow)
                    .filter(attribute -> attribute.getDataType() == DataType.CLOB || attribute.getDataType() == DataType.BLOB)
                    .forEach(this::fillLargeValueAttribute);
        }
    }

    @SuppressWarnings("unchecked")
    private void fillLargeValueAttribute(SimpleAttribute<?> attribute) {

        AbstractLargeValue largeValue = null;
        FetchLargeObjectRequestContext fetchCtx = null;

        if (attribute.getDataType() == DataType.BLOB) {

            BinaryLargeValue binarylargeValue = ((SimpleAttribute<BinaryLargeValue>) attribute).getValue();
            if(binarylargeValue instanceof AbstractLargeValue){
                largeValue = (AbstractLargeValue) binarylargeValue;
                fetchCtx = new FetchLargeObjectRequestContext.FetchLargeObjectRequestContextBuilder()
                        .binary(true)
                        .recordKey(largeValue.getId())
                        .build();
            }
        }

        if (attribute.getDataType() == DataType.CLOB) {
            CharacterLargeValue characterLargeValue = ((SimpleAttribute<CharacterLargeValue>) attribute).getValue();
            if(characterLargeValue instanceof AbstractLargeValue){
                largeValue = (AbstractLargeValue) characterLargeValue;
                fetchCtx = new FetchLargeObjectRequestContext.FetchLargeObjectRequestContextBuilder()
                        .binary(false)
                        .recordKey(largeValue.getId())
                        .build();
            }
        }

        if(largeValue != null){
            largeValue.setData(largeObjectsServiceComponent.fetchLargeObjectByteArray(fetchCtx));
        }
    }

    @Override
    public boolean supports(Start<?> start) {
        return GetRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}

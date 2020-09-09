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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.context.FetchLargeObjectRequestContext;
import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.impl.AbstractLargeValue;
import com.unidata.mdm.backend.common.types.impl.BlobSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.ClobSimpleAttributeImpl;
import com.unidata.mdm.backend.service.data.binary.LargeObjectsServiceComponent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.TypeOfChange;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DataRecordGetAttributesProcessingAfterExecutor extends AbstractDataRecordAttributesProcessingExecutor implements DataRecordAfterExecutor<GetRequestContext> {

    @Autowired
    LargeObjectsServiceComponent largeObjectsServiceComponent;

    /**
     * Constructor.
     */
    public DataRecordGetAttributesProcessingAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(GetRequestContext ctx) {

        MeasurementPoint.start();
        try {

            Map<String, Map<TypeOfChange, Attribute>> diffToDraft = ctx.getFromStorage(StorageId.DATA_GET_DIFF_TO_DRAFT);
            EtalonRecord getRecord = ctx.getFromStorage(StorageId.DATA_GET_ETALON_RECORD);
            List<EtalonRecord> getRecords = ctx.getFromStorage(StorageId.DATA_GET_ETALON_RECORDS);
            List<OriginRecord> origins =  ctx.getFromStorage(StorageId.DATA_GET_ORIGINS_RECORDS);
            List<EtalonRecord> etalons = new ArrayList<>();

            if (getRecord != null) {
                etalons.add(getRecord);
            }

            if (getRecords != null && !getRecords.isEmpty()) {
                etalons.addAll(getRecords);
            }

            if (CollectionUtils.isNotEmpty(etalons) && MapUtils.isNotEmpty(diffToDraft)) {

                // Create fake etalon record, just to set attributes display values
                // Only first level is processed
                SerializableDataRecord sdr = new SerializableDataRecord();
                sdr.addAll(diffToDraft.values().stream()
                        .flatMap(m -> m.values().stream())
                        .collect(Collectors.toList()));

                EtalonRecord diffEtalon = new EtalonRecordImpl()
                        .withDataRecord(sdr)
                        .withInfoSection(etalons.get(0).getInfoSection());

                etalons.add(diffEtalon);
            }

            if (origins == null) {
                origins = Collections.emptyList();
            }

            if (CollectionUtils.isEmpty(etalons) && CollectionUtils.isEmpty(origins)) {
                return true;
            }

            enrichRecordsIfNeed(etalons, ctx);

            processRecords(etalons, origins);

            return true;
        } finally {
            MeasurementPoint.stop();
        }
    }

    private void enrichRecordsIfNeed(List<EtalonRecord> etalons, GetRequestContext ctx){
        if(ctx.isFetchLargeObjects()){

            etalons.stream()
                    .flatMap(etalonRecord -> etalonRecord.getAllAttributes().stream())
                    .filter(attribute -> attribute instanceof ClobSimpleAttributeImpl
                            || attribute instanceof BlobSimpleAttributeImpl)
                    .forEach(this::fillLargeValueAttribute);
        }
    }

    private void fillLargeValueAttribute(Attribute attribute) {
        AbstractLargeValue largeValue = null;
        FetchLargeObjectRequestContext fetchCtx = null;
        if(attribute instanceof BlobSimpleAttributeImpl){
            BinaryLargeValue binarylargeValue = ((BlobSimpleAttributeImpl) attribute).getValue();
            if(binarylargeValue instanceof AbstractLargeValue){
                largeValue = (AbstractLargeValue) binarylargeValue;
                fetchCtx = new FetchLargeObjectRequestContext.FetchLargeObjectRequestContextBuilder()
                        .binary(true)
                        .recordKey(largeValue.getId())
                        .build();
            }
        }
        if(attribute instanceof ClobSimpleAttributeImpl){
            CharacterLargeValue characterLargeValue = ((ClobSimpleAttributeImpl) attribute).getValue();
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

}

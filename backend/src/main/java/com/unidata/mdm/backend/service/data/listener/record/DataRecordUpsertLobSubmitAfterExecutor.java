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
package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.BlobSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.ClobSimpleAttributeImpl;
import com.unidata.mdm.backend.dao.LargeObjectsDao;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * @author Mikhail Mikhailov
 */
public class DataRecordUpsertLobSubmitAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {

    /**
     * LOB DAO.
     */
    @Autowired
    private LargeObjectsDao largeObjectsDao;

    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * Constructor.
     */
    public DataRecordUpsertLobSubmitAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        if (action == UpsertAction.NO_ACTION) {
            return true;
        }

        // TODO implement for 1+ levels
        MeasurementPoint.start();
        try {

            Map<SimpleAttribute<?>, SimpleAttributeDef> fileAttributes = new HashMap<>();
            EtalonRecord etalon = ctx.getFromStorage(StorageId.DATA_UPSERT_ETALON_RECORD);
            OriginRecord origin = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);

            Map<String, AttributeInfoHolder> attrs;
            if (Objects.nonNull(etalon) || Objects.nonNull(origin)) {
                attrs = metaModelService.getAttributesInfoMap(Objects.nonNull(etalon)
                    ? etalon.getInfoSection().getEntityName()
                    : origin.getInfoSection().getOriginKey().getEntityName());
            } else {
                attrs = Collections.emptyMap();
            }

            if (etalon != null) {

                for (Entry<String, AttributeInfoHolder> attr : attrs.entrySet()) {
                    if (attr.getValue().isBlob() || attr.getValue().isClob()) {

                        SimpleAttribute<?> sa = etalon.getSimpleAttribute(attr.getKey());
                        if (Objects.nonNull(sa)) {
                            fileAttributes.put(sa, attr.getValue().narrow());
                        }
                    }
                }

                if (MapUtils.isNotEmpty(fileAttributes)) {
                    ensureFileAttributesActive(fileAttributes, etalon.getInfoSection().getEtalonKey().getId(), false);
                    fileAttributes.clear();
                }
            }

            if (origin != null) {

                for (Entry<String, AttributeInfoHolder> attr : attrs.entrySet()) {
                    if (attr.getValue().isBlob() || attr.getValue().isClob()) {

                        SimpleAttribute<?> sa = origin.getSimpleAttribute(attr.getKey());
                        if (Objects.nonNull(sa)) {
                            fileAttributes.put(sa, attr.getValue().narrow());
                        }
                    }
                }

                if (MapUtils.isNotEmpty(fileAttributes)) {
                    ensureFileAttributesActive(fileAttributes, origin.getInfoSection().getOriginKey().getId(), true);
                }
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * @param fileAttributes
     */
    private void ensureFileAttributesActive(Map<SimpleAttribute<?>, SimpleAttributeDef> fileAttributes, String id, boolean isOrigin) {

        for (Entry<SimpleAttribute<?>, SimpleAttributeDef> e : fileAttributes.entrySet()) {

            boolean isBinary = e.getValue().getSimpleDataType() == SimpleDataType.BLOB;
            String objectId = isBinary ? getBlobObjectId(e.getKey()) : getClobObjectId(e.getKey());
            if (objectId != null) {
                largeObjectsDao.ensureActive(objectId, id, isOrigin, isBinary);
            }
        }
    }

    /**
     * @param simpleAttribute
     * @return Object id if exist
     */
    private String getBlobObjectId(SimpleAttribute<?> simpleAttribute) {
        BinaryLargeValue blobValue = simpleAttribute.getDataType() == DataType.BLOB ? ((BlobSimpleAttributeImpl) simpleAttribute).getValue() : null;
        return blobValue == null ? null : blobValue.getId();
    }

    /**
     * @param simpleAttribute
     * @return Object id if exist
     */
    private String getClobObjectId(SimpleAttribute<?> simpleAttribute) {
        CharacterLargeValue clobValue = simpleAttribute.getDataType() == DataType.CLOB ? ((ClobSimpleAttributeImpl) simpleAttribute).getValue() : null;
        return clobValue == null ? null : clobValue.getId();
    }
}

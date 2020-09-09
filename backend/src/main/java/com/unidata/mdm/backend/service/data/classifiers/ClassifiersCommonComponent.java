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

package com.unidata.mdm.backend.service.data.classifiers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.ClassifierIdentityContext;
import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.dao.ClassifiersDAO;
import com.unidata.mdm.backend.po.ClassifierKeysPO;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;

/**
 * @author Mikhail Mikhailov
 * Common stuff.
 */
@Component
public class ClassifiersCommonComponent {
    /**
     * Classifier data DAO.
     */
    @Autowired
    private ClassifiersDAO classifierDAO;
    /**
     * MMS.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * CRC.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Identifies target.
     * @param ctx the context
     * @return keys or null
     */
    public ClassifierKeys identify(ClassifierIdentityContext ctx) {

        MeasurementPoint.start();
        try {

            ClassifierKeysPO po = null;
            if (ctx.isClassifierEtalonKey()) {
                po = classifierDAO.loadClassifierKeysByClassifierEtalonId(
                        metaModelService.getAdminSourceSystem().getName(), ctx.getClassifierEtalonKey());
            } else if (ctx.isClassifierOriginKey()) {
                po = classifierDAO.loadClassifierKeysByClassifierOriginId(ctx.getClassifierOriginKey());
            } else if (ctx.isRecordKeyAndNodeId() || ctx.isRecordKeyAndNodeCode() || ctx.isRecordKeyAndNodeName()) {

                RecordKeys parentKeys = ctx.keys();
                if (parentKeys == null) {
                    parentKeys = commonRecordsComponent.identify(ctx);
                }

                if (parentKeys == null) {
                    return null;
                }

                // Skip pointless keys resolution upon initial load.
                // May quite have an impact on millions of records
                boolean isInitialLoad = ctx instanceof UpsertClassifierDataRequestContext
                        && ((UpsertClassifierDataRequestContext) ctx).isInitialLoad();

                if (!isInitialLoad) {

                    if (Objects.nonNull(parentKeys.getOriginKey())) {
                        List<ClassifierKeysPO> potentialKeyList = classifierDAO.loadPotentialClassifierKeysByRecordOriginIdAndClassifierName(
                                parentKeys.getOriginKey().getId(),
                                ctx.getClassifierName());
                        po = resolvePotentialKey(ctx, potentialKeyList);
                    }

                    if (Objects.isNull(po) && Objects.nonNull(parentKeys.getEtalonKey())) {
                        List<ClassifierKeysPO> potentialKeyList = classifierDAO.loadPotentialClassifierKeysByRecordEtalonIdAndClassifierName(
                                metaModelService.getAdminSourceSystem().getName(),
                                parentKeys.getEtalonKey().getId(),
                                ctx.getClassifierName());
                        po = resolvePotentialKey(ctx, potentialKeyList);
                    }
                }
            }

            if (Objects.isNull(po)) {
                return null;
            }

            ClassifierKeys keys = ClassifierKeys.builder()
                    .record(RecordKeys.builder()
                        .entityName(po.getEtalonRecordName())
                        .etalonKey(EtalonKey.builder().id(po.getEtalonIdRecord()).build())
                        .etalonState(po.getEtalonRecordState())
                        .etalonStatus(po.getEtalonRecordStatus())
                        .originKey(po.getOriginIdRecord() == null
                            ? null
                            : OriginKey.builder()
                                .entityName(po.getOriginRecordName())
                                .externalId(po.getOriginRecordExternalId())
                                .id(po.getOriginIdRecord())
                                .sourceSystem(po.getOriginRecordSourceSystem())
                                .build())
                        .originStatus(po.getOriginRecordStatus())
                        .build())
                    .name(po.getEtalonName())
                    .nodeId(po.getOriginNodeId())
                    .nodeName(po.getOriginNodeName())
                    .etalonId(po.getEtalonId())
                    .etalonStatus(po.getEtalonStatus())
                    .etalonState(po.getEtalonState())
                    .originId(po.getOriginId())
                    .originSourceSystem(po.getOriginSourceSystem())
                    .originStatus(po.getOriginStatus())
                    .originRevision(po.getOriginRevision())
                    .build();

            ((CommonRequestContext) ctx).putToStorage(ctx.classifierKeysId(), keys);

            return keys;
        } finally {
            MeasurementPoint.stop();
        }
    }

    private ClassifierKeysPO resolvePotentialKey(ClassifierIdentityContext ctx, List<ClassifierKeysPO> potentialKeyList) {
        if (CollectionUtils.isNotEmpty(potentialKeyList)) {
            if (ctx.isRecordKeyAndNodeId()) {
                Optional<ClassifierKeysPO> optionalPO = potentialKeyList.stream()
                        .filter(potentialKey -> ctx.getClassifierNodeId().equals(potentialKey.getOriginNodeId()))
                        .findAny();
                if(optionalPO.isPresent()) {
                    return optionalPO.get();
                }
            }

            if (ctx.isRecordKeyAndNodeCode()) {
                Optional<ClassifierKeysPO> optionalPO = potentialKeyList.stream()
                        .filter(potentialKey -> ctx.getClassifierNodeCode().equals(potentialKey.getOriginNodeCode()))
                        .findAny();
                if(optionalPO.isPresent()) {
                    return optionalPO.get();
                }
            }

            if (ctx.isRecordKeyAndNodeName()) {
                Optional<ClassifierKeysPO> optionalPO = potentialKeyList.stream()
                        .filter(potentialKey -> ctx.getClassifierNodeName().equals(potentialKey.getOriginNodeName()))
                        .findAny();
                if(optionalPO.isPresent()) {
                    return optionalPO.get();
                }
            }
        }
        return null;
    }
}

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

package com.unidata.mdm.backend.service.data.relations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.unidata.mdm.backend.common.context.ContextUtils;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.GetRelationsRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteRelationDTO;
import com.unidata.mdm.backend.common.dto.GetRelationDTO;
import com.unidata.mdm.backend.common.dto.RelationStateDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationDTO;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.util.ValidityPeriodUtils;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Abstract base relation component.
 */
public interface RelationComponent {
    /**
     * Gets relations.
     * @param ctx
     * @param relation
     * @return
     */
    default Map<RelationStateDTO, List<GetRelationDTO>> get(GetRelationsRequestContext ctx, RelationDef relation) {

        MeasurementPoint.start();
        try {

            List<GetRelationRequestContext> get = ctx.getRelations().get(relation.getName());

            // 1. Do get
            List<GetRelationDTO> collected = new ArrayList<>();
            List<Date> fromDates = new ArrayList<>(get.size());
            List<Date> toDates = new ArrayList<>(get.size());
            for (GetRelationRequestContext gCtx : get) {

                ContextUtils.storageCopy(ctx, gCtx,
                        StorageId.RELATIONS_FROM_KEY,
                        StorageId.RELATIONS_FROM_RIGHTS,
                        StorageId.RELATIONS_FROM_WF_ASSIGNMENTS,
                        StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE);

                GetRelationDTO result = get(gCtx, relation);
                if (Objects.isNull(result)) {
                    continue;
                }

                collected.add(result);
                EtalonRelation record = result.getEtalon();
                if (record != null) {
                    fromDates.add(record.getInfoSection().getValidFrom());
                    toDates.add(record.getInfoSection().getValidTo());
                }
            }

            // 2. Collect result.
            return Collections.singletonMap(
                    new RelationStateDTO(
                            relation.getName(),
                            relation.getRelType(),
                            ValidityPeriodUtils.leastFrom(fromDates),
                            ValidityPeriodUtils.mostTo(toDates)),
                    collected);
        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Gets a reference relation
     * @param ctx the context
     * @param relation relation type
     * @return record or null
     */
    GetRelationDTO get(GetRelationRequestContext ctx, RelationDef relation);
    /**
     * Does upsert of a containment relation.
     * @param ctx the context
     * @param relation the relation to process
     * @return result
     */
    default Map<RelationStateDTO, List<UpsertRelationDTO>> upsert(UpsertRelationsRequestContext ctx, RelationDef relation) {

        MeasurementPoint.start();
        try {

            List<UpsertRelationRequestContext> upsert = ctx.getRelations().get(relation.getName());

            // 1. Do upsert
            List<UpsertRelationDTO> collected = new ArrayList<>();
            List<Date> fromDates = new ArrayList<>(upsert.size());
            List<Date> toDates = new ArrayList<>(upsert.size());
            for (UpsertRelationRequestContext uCtx : upsert) {

                ContextUtils.storageCopy(ctx, uCtx,
                        StorageId.RELATIONS_FROM_KEY,
                        StorageId.RELATIONS_FROM_RIGHTS,
                        StorageId.RELATIONS_FROM_WF_ASSIGNMENTS,
                        StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE);

                UpsertRelationDTO result = null;
                try {
                    result = upsert(uCtx, relation);
                } catch (Exception exc) {
                       ctx.setDqErrors(uCtx.getDqErrors());
                       throw exc;
                }

                if (Objects.isNull(result)) {
                    continue;
                }

                collected.add(result);
                fromDates.add(uCtx.getValidFrom());
                toDates.add(uCtx.getValidTo());
            }

            // 2. Collect result.
            return Collections.singletonMap(
                    new RelationStateDTO(
                            relation.getName(),
                            relation.getRelType(),
                            ValidityPeriodUtils.leastFrom(fromDates),
                            ValidityPeriodUtils.mostTo(toDates)),
                    collected);

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Does upsert of single relation context.
     * @param ctx the context
     * @param relation relation definition
     * @return upserted DTO
     */
    UpsertRelationDTO upsert(UpsertRelationRequestContext ctx, RelationDef relation);
    /**
     * Deletes relations.
     * @param ctx the context
     * @param relation relation definition
     * @return result
     */
    default Map<RelationStateDTO, List<DeleteRelationDTO>> delete(DeleteRelationsRequestContext ctx, RelationDef relation) {

        MeasurementPoint.start();
        try {

            List<DeleteRelationRequestContext> delete = ctx.getRelations().get(relation.getName());

            // 1. Do delete
            List<DeleteRelationDTO> deleted = new ArrayList<>(delete.size());
            List<Date> fromDates = new ArrayList<>(delete.size());
            List<Date> toDates = new ArrayList<>(delete.size());
            for (DeleteRelationRequestContext dCtx : delete) {

                ContextUtils.storageCopy(ctx, dCtx,
                        StorageId.RELATIONS_FROM_KEY,
                        StorageId.RELATIONS_FROM_RIGHTS,
                        StorageId.RELATIONS_FROM_WF_ASSIGNMENTS,
                        StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE);

                DeleteRelationDTO result = delete(dCtx, relation);
                if (Objects.isNull(result)) {
                    continue;
                }

                deleted.add(result);
                fromDates.add(dCtx.getValidFrom());
                toDates.add(dCtx.getValidTo());
            }

            // 2. Collect result.
            return Collections.singletonMap(
                    new RelationStateDTO(
                            relation.getName(),
                            relation.getRelType(),
                            ValidityPeriodUtils.leastFrom(fromDates),
                            ValidityPeriodUtils.mostTo(toDates)),
                    deleted);

        } finally {
            MeasurementPoint.stop();
        }
    }
    /**
     * Deletes relation.
     * @param ctx the context
     * @param relation relation definition
     * @return result
     */
    DeleteRelationDTO delete(DeleteRelationRequestContext ctx, RelationDef relation);
}

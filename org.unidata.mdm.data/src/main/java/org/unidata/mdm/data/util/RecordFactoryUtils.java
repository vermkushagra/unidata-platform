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

package org.unidata.mdm.data.util;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.DataShift;
import org.unidata.mdm.core.type.data.OperationType;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.configuration.DataConfiguration;
import org.unidata.mdm.data.context.AbstractRelationToRequestContext;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.ReadWriteTimelineContext;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.po.data.RecordEtalonPO;
import org.unidata.mdm.data.po.data.RecordOriginPO;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.data.RelationVistoryPO;
import org.unidata.mdm.data.type.data.OriginRecord;
import org.unidata.mdm.data.type.data.impl.OriginRecordImpl;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.system.service.PlatformConfiguration;
import org.unidata.mdm.system.util.IdUtils;

/**
 * @author Mikhail Mikhailov on Nov 7, 2019
 */
public final class RecordFactoryUtils {
    /**
     * MMS instance.
     */
    private static MetaModelService metaModelService;
    /**
     * The platform fields.
     */
    private static PlatformConfiguration platformConfiguration;
    /**
     * Constructor.
     */
    private RecordFactoryUtils() {
        super();
    }
    /**
     * Conventional init method.
     */
    public static void init() {
        metaModelService = DataConfiguration.getBean(MetaModelService.class);
        platformConfiguration = DataConfiguration.getBean(PlatformConfiguration.class);
    }
    /**
     * Creates etalon record PO from a context.
     * @param ctx the context
     * @param isNew new or not
     * @return new etalon record
     */
    public static RecordOriginPO createRecordOriginPO(@Nonnull UpsertRequestContext ctx, @Nonnull RecordKeys keys, RecordStatus status) {

        boolean isNew = keys.getOriginKey() == null || keys.getOriginKey().getId() == null;
        String user = SecurityUtils.getCurrentUserName();

        String entityName = keys.getEntityName() != null
                ? keys.getEntityName()
                : ctx.getEntityName();

        String externalId = null;
        if (keys.getOriginKey() != null && keys.getOriginKey().getExternalId() != null) {
            externalId = keys.getOriginKey().getExternalId();
        } else {
            // Handle UD etalon upsert
            externalId = ctx.getExternalId() == null && ctx.isEtalon()
                    ? IdUtils.v1String()
                    : ctx.getExternalId();
        }

        String sourceSystem = null;
        if (keys.getOriginKey() != null && keys.getOriginKey().getSourceSystem() != null) {
            sourceSystem = keys.getOriginKey().getSourceSystem();
        } else {
            // Handle UD etalon upsert
            sourceSystem = ctx.getSourceSystem() == null && ctx.isEtalon()
                    ? metaModelService.getAdminSourceSystem().getName()
                    : ctx.getSourceSystem();
        }

        RecordOriginPO result = new RecordOriginPO();
        result.setEtalonId(keys.getEtalonKey().getId());
        result.setExternalId(externalId, entityName, sourceSystem);
        result.setEnrichment(ctx.isEnrichment());
        result.setStatus(status);
        result.setShard(keys.getShard());

        Date ts = ctx.timestamp();
        if (!isNew) {

            result.setId(keys.getOriginKey().getId());
            result.setUpdateDate(ctx.getLastUpdate() == null ? ts : ctx.getLastUpdate());
            result.setUpdatedBy(user);
        } else {

            result.setId(IdUtils.v1String());
            result.setInitialOwner(UUID.fromString(keys.getEtalonKey().getId()));
            result.setCreateDate(ts);
            result.setCreatedBy(user);
        }

        return result;
    }
    /**
     * Creates etalon record PO from a context.
     * @param ctx the context
     * @param isNew new or not
     * @return new etalon record
     */
    public static RecordOriginPO createRecordOriginPO(DeleteRequestContext ctx, RecordKeys keys, RecordStatus status) {

        String user = SecurityUtils.getCurrentUserName();
        String entityName = keys.getEntityName();
        String externalId = keys.getOriginKey().getExternalId();
        String sourceSystem = keys.getOriginKey().getSourceSystem();
        Date ts = ctx.timestamp();

        RecordOriginPO result = new RecordOriginPO();
        result.setEtalonId(keys.getEtalonKey().getId());
        result.setShard(keys.getShard());
        result.setExternalId(externalId, entityName, sourceSystem);
        result.setStatus(status);
        result.setId(keys.getOriginKey().getId());
        result.setUpdateDate(ts);
        result.setUpdatedBy(user);

        return result;
    }
    /**
     * Creates etalon record PO from a context.
     * @param ctx the context
     * @param isNew new or not
     * @return new etalon record
     */
    public static RecordEtalonPO createRecordEtalonPO(UpsertRequestContext ctx, RecordKeys keys, RecordStatus status) {

        boolean isNew = keys == null || keys.getEtalonKey() == null || keys.getEtalonKey().getId() == null;
        String user = SecurityUtils.getCurrentUserName();

        String entityName = keys != null && keys.getEntityName() != null
                ? keys.getEntityName()
                : ctx.getEntityName();

        Date ts = ctx.timestamp();

        RecordEtalonPO result = new RecordEtalonPO();
        result.setName(entityName);
        result.setStatus(status);
        result.setOperationId(ctx.getOperationId());
        if (!isNew) {

            result.setId(keys.getEtalonKey().getId());
            result.setUpdateDate(ts);
            result.setUpdatedBy(user);
            result.setShard(keys.getShard());
        } else {

            // @Modules
            //WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS);
            UUID id = IdUtils.v1();

            result.setId(id.toString());
            result.setCreateDate(ts);
            result.setCreatedBy(user);
            result.setShard(StorageUtils.shard(id));
            result.setApproval(ApprovalState.APPROVED /*calculateRecordState(ctx, assignment)*/ );
        }

        return result;
    }
    /**
     * Creates etalon record PO from a context.
     * @param ctx the context
     * @param isNew new or not
     * @return new etalon record
     */
    public static RecordEtalonPO createRecordEtalonPO(DeleteRequestContext ctx, RecordKeys keys, RecordStatus status) {

        String user = SecurityUtils.getCurrentUserName();
        Date ts = ctx.timestamp();

        RecordEtalonPO result = new RecordEtalonPO();
        result.setName(keys.getEntityName());
        result.setStatus(status);
        result.setId(keys.getEtalonKey().getId());
        result.setShard(keys.getShard());
        result.setUpdateDate(ts);
        result.setUpdatedBy(user);
        result.setOperationId(ctx.getOperationId());

        return result;
    }
    /**
     * Converts a {@link OriginRecord} to a persistent object.
     * @param ctx
     *            the context
     * @param etalonId the etalon ID
     *
     * @return persistent object
     */
    public static RecordOriginPO createRecordSystemOriginPO(UpsertRequestContext ctx, String etalonId) {

        String user = SecurityUtils.getCurrentUserName();
        RecordOriginPO result = new RecordOriginPO();

        result.setId(IdUtils.v1String());
        result.setShard(StorageUtils.shard(UUID.fromString(etalonId)));
        result.setEtalonId(etalonId);
        result.setCreatedBy(user);
        result.setExternalId(IdUtils.v1String(), ctx.getEntityName(), ctx.getSourceSystem());
        result.setCreateDate(new Date());
        result.setStatus(ctx.getOriginStatus() == null ? RecordStatus.ACTIVE : ctx.getOriginStatus());

        return result;
    }
    /**
     * Creates version (vistory) persistent object.
     * @param data the data to save
     * @param ctx the context
     * @param shift the {@link DataShift} indicator
     * @return new object
     */
    public static RecordVistoryPO createRecordVistoryPO(
            OriginRecord data, UpsertRequestContext ctx, DataShift shift) {

        RecordKeys keys = ctx.keys();
        String user = SecurityUtils.getCurrentUserName();

        RecordVistoryPO result = new RecordVistoryPO();

        result.setCreatedBy(user);
        result.setValidFrom(ctx.getValidFrom());
        result.setValidTo(ctx.getValidTo());
        result.setStatus(RecordStatus.ACTIVE);

// @Modules
//        WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS);

        // UN-1539
        Date ts = ctx.timestamp();
        result.setShard(keys.getShard());
        result.setCreateDate(ctx.getLastUpdate() != null ? ctx.getLastUpdate() : ts);
        result.setApproval(ApprovalState.APPROVED /* calculateVersionState(ctx, keys, assignment) */ );
        result.setShift(shift);
        result.setData(data);
        result.setId(IdUtils.v1String());
        result.setOriginId(keys.getOriginKey().getId());
        result.setOperationId(ctx.getOperationId());
        result.setOperationType(ctx.operationType());
        result.setMajor(platformConfiguration.getPlatformMajor());
        result.setMinor(platformConfiguration.getPlatformMinor());

        return result;
    }
/**
     * Creates inactive version (vistory) persistent object.
     * @param originId the origin ID
     * @param validFrom valid from date
     * @param validTo valid to date
     * @param prev previous data version
     * @param ctx the context
     * @return new object
     */
    public static RecordVistoryPO createInactiveVistoryRecordPO(
            String originId, Date validFrom, Date validTo, Date createDate, OriginRecord prev, DeleteRequestContext ctx) {

        OriginRecord data;
        String id = IdUtils.v1String();
        if (prev != null) {
            data = prev;
        } else {
            data = new OriginRecordImpl();
        }

        String user = SecurityUtils.getCurrentUserName();
// @Modules
//        WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_DELETE_WF_ASSIGNMENTS);
        RecordKeys keys = ctx.keys();
        Date ts = ctx.timestamp();
        OperationType operationType = ctx.operationType();

        RecordVistoryPO result = new RecordVistoryPO();
        result.setShard(keys.getShard());
        result.setStatus(RecordStatus.INACTIVE);
        result.setApproval(ApprovalState.APPROVED /* calculateVersionState(ctx, keys, assignment) */ );
        result.setCreatedBy(user);
        result.setCreateDate(createDate != null ? createDate : ts);
        result.setId(id);
        result.setOriginId(originId);
        result.setOperationId(ctx.getOperationId());
        result.setData(data);
        result.setValidFrom(validFrom);
        result.setValidTo(validTo);
        result.setOperationType(operationType);
        result.setMajor(platformConfiguration.getPlatformMajor());
        result.setMinor(platformConfiguration.getPlatformMinor());

        return result;
    }

    /**
     * Creates inactive relation version (vistory) persistent object.
     * @param keys the origin ID
     * @param operationId the operation id
     * @param from date from
     * @param to date to
     * @param approvalState the state to set
     * @return new object
     */
    public static RelationVistoryPO createInactiveRelationVistoryPO(
            RelationKeys keys, String operationId, Date from, Date to, ApprovalState approvalState) {

        String user = SecurityUtils.getCurrentUserName();
        RelationVistoryPO result = new RelationVistoryPO();

        result.setId(IdUtils.v1String());
        result.setOriginId(keys.getOriginKey().getId());
        result.setShard(keys.getShard());
        result.setOperationId(operationId);
        result.setData(null);
        result.setStatus(RecordStatus.INACTIVE);
        result.setApproval(approvalState);
        result.setValidFrom(from);
        result.setValidTo(to);
        result.setCreatedBy(user);
        result.setMajor(platformConfiguration.getPlatformMajor());
        result.setMinor(platformConfiguration.getPlatformMinor());

        return result;
    }

    /**
     * Creates new etalons relation PO.
     * @param ctx the context
     * @return po
     */
    public static RelationEtalonPO newRelationEtalonPO(AbstractRelationToRequestContext ctx, RecordStatus status) {

        String etalonIdFrom = null;
        String etalonIdTo = null;

        RelationKeys keys = ctx.relationKeys();
        if (keys != null && keys.getEtalonKey() != null && keys.getEtalonKey().getFrom() != null) {
            etalonIdFrom = keys.getEtalonKey().getFrom().getId();
            etalonIdTo = keys.getEtalonKey().getTo().getId();
        } else {
            RecordKeys from = ctx.fromKeys();
            RecordKeys to = ctx.keys();

            etalonIdFrom = from != null ? from.getEtalonKey().getId() : null;
            etalonIdTo = to != null ? to.getEtalonKey().getId() : null;
        }

        String user = SecurityUtils.getCurrentUserName();

        RelationEtalonPO po = new RelationEtalonPO();
        Date ts = ctx instanceof ReadWriteTimelineContext<?> ? ((ReadWriteTimelineContext<?>) ctx).timestamp() : new Date();

        UUID id = IdUtils.v1();

        po.setId(id.toString());
        po.setShard(StorageUtils.shard(id));
        po.setFromEtalonId(etalonIdFrom);
        po.setToEtalonId(etalonIdTo);
        po.setName(ctx.relationName());
        po.setCreatedBy(user);
        po.setCreateDate(ts);
        po.setStatus(status == null ? RecordStatus.ACTIVE : status);
        po.setApproval(ApprovalState.APPROVED);
        po.setOperationId(ctx.getOperationId());
        po.setRelationType(ctx.relationType());

        return po;
    }

    /**
     * Creates new etalons relation PO.
     * @param ctx the context
     * @param keys the etalon relation id
     * @param fromKey from key
     * @param toKey to key
     * @param status the status to set
     * @return po
     */
    public static RelationOriginPO newRelationOriginPO(
            AbstractRelationToRequestContext ctx, RelationKeys keys, RecordOriginKey fromKey, RecordOriginKey toKey, RecordStatus status) {

        String user = SecurityUtils.getCurrentUserName();
        RelationOriginPO po = new RelationOriginPO();
        Date ts = ctx instanceof ReadWriteTimelineContext<?> ? ((ReadWriteTimelineContext<?>) ctx).timestamp() : new Date();

        po.setId(IdUtils.v1String());
        po.setInitialOwner(UUID.fromString(keys.getEtalonKey().getId()));
        po.setShard(keys.getShard());
        po.setEtalonId(keys.getEtalonKey().getId());
        po.setFromOriginId(fromKey.getId());
        po.setToOriginId(toKey.getId());
        po.setName(keys.getRelationName());
        po.setSourceSystem(fromKey.getSourceSystem());
        po.setCreatedBy(user);
        po.setCreateDate(ts);
        po.setStatus(status == null ? RecordStatus.ACTIVE : status);

        return po;
    }

    /**
     * Creates new relation vistory record.
     * @param ctx the context
     * @param originId origin id of the relation
     * @param validFrom validity period start
     * @param validTo validity period end
     * @param data the data
     * @param status record status
     * @param shift data shift
     * @return record
     */
    public static RelationVistoryPO newRelationVistoryPO(
            UpsertRelationRequestContext ctx,
            String originId, Date validFrom, Date validTo, DataRecord data, RecordStatus status, DataShift shift) {

        String user = SecurityUtils.getCurrentUserName();
        RelationVistoryPO po = new RelationVistoryPO();
        Date ts = ctx.timestamp();
        OperationType operationType = ctx.operationType();

        po.setId(IdUtils.v1String());
        po.setOriginId(originId);
        po.setOperationId(ctx.getOperationId());
        po.setValidFrom(validFrom);
        po.setValidTo(validTo);
        po.setCreatedBy(user);
        po.setCreateDate(ts);
        po.setData(data);
        po.setStatus(status == null ? RecordStatus.ACTIVE : status);
        po.setShift(shift == null ? DataShift.PRISTINE : shift);
        po.setApproval(ApprovalState.APPROVED);
        po.setMajor(platformConfiguration.getPlatformMajor());
        po.setMinor(platformConfiguration.getPlatformMinor());
        po.setOperationType(operationType == null ? OperationType.DIRECT : operationType);

        return po;
    }
}

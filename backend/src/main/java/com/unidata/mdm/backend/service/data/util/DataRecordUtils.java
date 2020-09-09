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

package com.unidata.mdm.backend.service.data.util;

import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;
import com.unidata.mdm.backend.common.context.AbstractClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.ApprovalStateSettingContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.integration.exits.ExitConstants;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.VistoryOperationType;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
import com.unidata.mdm.backend.po.EtalonClassifierPO;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.po.EtalonTransitionPO;
import com.unidata.mdm.backend.po.OriginClassifierPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.po.OriginRelationPO;
import com.unidata.mdm.backend.po.OriginTransitionPO;
import com.unidata.mdm.backend.po.OriginsVistoryClassifierPO;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.service.data.merge.TransitionType;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;

/**
 * @author Mikhail Mikhailov
 * Various data utility methods.
 */
public class DataRecordUtils {

    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordUtils.class);
    /**
     * Meta model service instance taken from context.
     */
    private static MetaModelServiceExt metaModelService;
    /**
     * Origins component.
     */
    private static OriginRecordsComponent originRecordsComponent;
    /**
     * The platform fields.
     */
    private static PlatformConfiguration platformConfiguration;
    /**
     * Constructor. No instances allowed.
     */
    private DataRecordUtils() {
        super();
    }

    /**
     * Convenient init method.
     */
    public static void init(ApplicationContext ac) {
        try {
            metaModelService = ac.getBean(MetaModelServiceExt.class);
            platformConfiguration = ac.getBean(PlatformConfiguration.class);
            originRecordsComponent = ac.getBean(OriginRecordsComponent.class);
        } catch (Exception exc) {
            LOGGER.warn("Meta model service bean GET. Exception caught.", exc);
        }
    }

    /**
     * Calculate state for a record.
     * @param ctx the context
     * @param assignment current work flow assignment, if any
     * @return state
     */
    public static ApprovalState calculateRecordState(ApprovalStateSettingContext ctx, WorkflowAssignmentDTO assignment) {
        return originRecordsComponent.calculateRecordState(ctx, assignment);
    }

    /**
     * Calculate state for a version.
     * @param ctx the context
     * @param keys record keys
     * @param assignment current work flow assignment, if any
     * @return state
     */
    public static ApprovalState calculateVersionState(ApprovalStateSettingContext ctx, RecordKeys keys, WorkflowAssignmentDTO assignment) {
        return originRecordsComponent.calculateVersionState(ctx, keys, assignment);
    }

    /**
     * Converts a {@link OriginRecord} to a persistent object.
     * @param ctx
     *            the context
     * @param etalonId the etalon ID
     *
     * @return persistent object
     */
    public static OriginRecordPO createSystemOriginRecordPO(UpsertRequestContext ctx, String etalonId) {

        String user = SecurityUtils.getCurrentUserName();
        OriginRecordPO result = new OriginRecordPO();

        result.setId(IdUtils.v1String());
        result.setEtalonId(etalonId);
        result.setCreatedBy(user);
        result.setExternalId(IdUtils.v1String());
        result.setCreateDate(new Date());
        result.setSourceSystem(ctx.getSourceSystem());
        result.setName(ctx.getEntityName());
        result.setStatus(ctx.getOriginStatus() == null ? RecordStatus.ACTIVE : ctx.getOriginStatus());

        return result;
    }

    /**
     * Creates etalon record PO from a context.
     * @param ctx the context
     * @param isNew new or not
     * @return new etalon record
     */
    public static EtalonRecordPO createEtalonRecordPO(UpsertRequestContext ctx, RecordKeys keys, RecordStatus status) {

        boolean isNew = keys == null || keys.getEtalonKey() == null || keys.getEtalonKey().getId() == null;
        String user = SecurityUtils.getCurrentUserName();

        String entityName = keys != null && keys.getEntityName() != null
                ? keys.getEntityName()
                : ctx.getEntityName();

        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        EtalonRecordPO result = new EtalonRecordPO();
        result.setName(entityName);
        result.setStatus(status);
        result.setOperationId(ctx.getOperationId());
        if (!isNew) {

            result.setId(keys.getEtalonKey().getId());
            result.setUpdateDate(ts);
            result.setUpdatedBy(user);
        } else {

            WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS);

            result.setId(IdUtils.v1String());
            result.setCreateDate(ts);
            result.setCreatedBy(user);
            result.setVersion(1);
            result.setApproval(calculateRecordState(ctx, assignment));
        }

        return result;
    }

    /**
     * Creates etalon record PO from a context.
     * @param ctx the context
     * @param isNew new or not
     * @return new etalon record
     */
    public static EtalonRecordPO createEtalonRecordPO(DeleteRequestContext ctx, RecordKeys keys, RecordStatus status) {

        String user = SecurityUtils.getCurrentUserName();
        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        EtalonRecordPO result = new EtalonRecordPO();
        result.setName(keys.getEntityName());
        result.setStatus(status);
        result.setId(keys.getEtalonKey().getId());
        result.setUpdateDate(ts);
        result.setUpdatedBy(user);
        result.setOperationId(ctx.getOperationId());

        return result;
    }

    /**
     * Creates etalon record PO from a context.
     * @param ctx the context
     * @param isNew new or not
     * @return new etalon record
     */
    public static OriginRecordPO createOriginRecordPO(UpsertRequestContext ctx, RecordKeys keys, RecordStatus status) {

        boolean isNew = keys == null || keys.getOriginKey() == null || keys.getOriginKey().getId() == null;
        String user = SecurityUtils.getCurrentUserName();

        String entityName = keys.getEntityName() != null
                ? keys.getEntityName()
                : ctx.getEntityName();
        String externalId = keys.getOriginKey() != null && keys.getOriginKey().getExternalId() != null
                ? keys.getOriginKey().getExternalId()
                : ctx.getExternalId() == null && ctx.isEtalon() // Handle UD etalon upsert
                    ? IdUtils.v1String()
                    : ctx.getExternalId();
        String sourceSystem = keys.getOriginKey() != null && keys.getOriginKey().getSourceSystem() != null
                ? keys.getOriginKey().getSourceSystem()
                : ctx.getSourceSystem() == null && ctx.isEtalon() // Handle UD etalon upsert
                    ? metaModelService.getAdminSourceSystem().getName()
                    : ctx.getSourceSystem();

        OriginRecordPO result = new OriginRecordPO();
        result.setEtalonId(keys.getEtalonKey().getId());
        result.setExternalId(externalId);
        result.setSourceSystem(sourceSystem);
        result.setName(entityName);
        result.setEnrichment(ctx.isEnrichment());
        result.setStatus(status);

        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
        if (!isNew) {

            result.setId(keys.getOriginKey().getId());
            result.setUpdateDate(ctx.getLastUpdate() == null ? ts : ctx.getLastUpdate());
            result.setUpdatedBy(user);
        } else {

            result.setId(IdUtils.v1String());
            result.setCreateDate(ts);
            result.setCreatedBy(user);
            result.setVersion(1);
        }

        return result;
    }

    /**
     * Creates etalon record PO from a context.
     * @param ctx the context
     * @param isNew new or not
     * @return new etalon record
     */
    public static OriginRecordPO createOriginRecordPO(DeleteRequestContext ctx, RecordKeys keys, RecordStatus status) {

        String user = SecurityUtils.getCurrentUserName();
        String entityName = keys.getEntityName();
        String externalId = keys.getOriginKey().getExternalId();
        String sourceSystem = keys.getOriginKey().getSourceSystem();
        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        OriginRecordPO result = new OriginRecordPO();
        result.setEtalonId(keys.getEtalonKey().getId());
        result.setExternalId(externalId);
        result.setSourceSystem(sourceSystem);
        result.setName(entityName);
        result.setStatus(status);
        result.setId(keys.getOriginKey().getId());
        result.setUpdateDate(ts);
        result.setUpdatedBy(user);

        return result;
    }
    /**
     * Creates version (vistory) persistent object.
     * @param data the data to save
     * @param ctx the context
     * @param shift the {@link DataShift} indicator
     * @return new object
     */
    public static OriginsVistoryRecordPO createVistoryRecordPO(
            OriginRecord data, UpsertRequestContext ctx, DataShift shift) {

        RecordKeys keys = ctx.getFromStorage(StorageId.DATA_UPSERT_KEYS);
        String user = SecurityUtils.getCurrentUserName();

        OriginsVistoryRecordPO result = new OriginsVistoryRecordPO();

        result.setCreatedBy(ctx.isSetInUserContext(
                ExitConstants.OUT_UPSERT_CURRENT_RECORD_CREATED_BY.name())
                    ? ctx.getFromUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_CREATED_BY.name())
                    : user);
        result.setValidFrom(ctx.isSetInUserContext(
                ExitConstants.OUT_UPSERT_CURRENT_RECORD_VALID_FROM.name())
                    ? ctx.getFromUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_VALID_FROM.name())
                    : ctx.getValidFrom());
        result.setValidTo(ctx.isSetInUserContext(
                ExitConstants.OUT_UPSERT_CURRENT_RECORD_VALID_TO.name())
                    ? ctx.getFromUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_VALID_TO.name())
                    : ctx.getValidTo());
        result.setStatus(ctx.isSetInUserContext(
                ExitConstants.OUT_UPSERT_CURRENT_RECORD_STATUS.name())
                    ? ctx.getFromUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_STATUS.name())
                    : RecordStatus.ACTIVE);

        WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_UPSERT_WF_ASSIGNMENTS);

        // UN-1539
        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
        result.setCreateDate(ctx.getLastUpdate() != null ? ctx.getLastUpdate() : ts);
        result.setApproval(calculateVersionState(ctx, keys, assignment));
        result.setShift(shift);
        result.setData(data);
        result.setId(IdUtils.v1String());
        result.setOriginId(keys.getOriginKey().getId());
        result.setOperationId(ctx.getOperationId());
        result.setOperationType(ctx.getFromStorage(StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE));
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
    public static OriginsVistoryRecordPO createInactiveVistoryRecordPO(
            String originId, Date validFrom, Date validTo, OriginRecord prev, DeleteRequestContext ctx) {

        OriginRecord data;
        String id = IdUtils.v1String();
        if (prev != null) {
            data = prev;
        } else {
            data = new OriginRecordImpl();
        }

        String user = SecurityUtils.getCurrentUserName();
        WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.DATA_DELETE_WF_ASSIGNMENTS);
        RecordKeys keys = ctx.getFromStorage(StorageId.DATA_DELETE_KEYS);
        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
        VistoryOperationType operationType = ctx.getFromStorage(StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE);

        OriginsVistoryRecordPO result = new OriginsVistoryRecordPO();
        result.setStatus(RecordStatus.INACTIVE);
        result.setApproval(calculateVersionState(ctx, keys, assignment));
        result.setCreatedBy(user);
        result.setCreateDate(ts);
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
     * @param originId the origin ID
     * @param operationId the operation id
     * @param from date from
     * @param to date to
     * @param approvalState the state to set
     * @return new object
     */
    public static OriginsVistoryRelationsPO createInactiveRelationsVistoryRecordPO(
            String originId, String operationId, Date from, Date to, ApprovalState approvalState) {

        String user = SecurityUtils.getCurrentUserName();
        OriginsVistoryRelationsPO result = new OriginsVistoryRelationsPO();

        result.setId(IdUtils.v1String());
        result.setOriginId(originId);
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
     * @param name the name
     * @param fromKey from key
     * @param toKey to key
     * @return po
     */
    public static EtalonRelationPO newEtalonRelationPO(
            UpsertRelationRequestContext ctx, String name, String fromKey, String toKey, RecordStatus status) {

        String user = SecurityUtils.getCurrentUserName();
        WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS);

        EtalonRelationPO po = new EtalonRelationPO();
        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        po.setId(IdUtils.v1String());
        po.setEtalonIdFrom(fromKey);
        po.setEtalonIdTo(toKey);
        po.setName(name);
        po.setCreatedBy(user);
        po.setCreateDate(ts);
        po.setStatus(status == null ? RecordStatus.ACTIVE : status);
        po.setApproval(calculateRecordState(ctx, assignment));
        po.setOperationId(ctx.getOperationId());

        return po;
    }

    /**
     * Creates new etalons relation PO.
     * @param ctx the context
     * @param etalonId the etalon relation id
     * @param name the name
     * @param fromKey from key
     * @param toKey to key
     * @param sourceSystem the source system
     * @param status the status to set
     * @return po
     */
    public static OriginRelationPO newOriginsRelationPO(
            UpsertRelationRequestContext ctx, String etalonId, String name, String fromKey, String toKey, String sourceSystem, RecordStatus status) {

        String user = SecurityUtils.getCurrentUserName();
        OriginRelationPO po = new OriginRelationPO();
        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        po.setId(IdUtils.v1String());
        po.setEtalonId(etalonId);
        po.setOriginIdFrom(fromKey);
        po.setOriginIdTo(toKey);
        po.setName(name);
        po.setSourceSystem(sourceSystem);
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
    public static OriginsVistoryRelationsPO newRelationsVistoryRecordPO(
            UpsertRelationRequestContext ctx,
            String originId, Date validFrom, Date validTo, DataRecord data, RecordStatus status, DataShift shift) {

        RelationKeys relationKeys = ctx.relationKeys();
        RecordKeys fromRecordKeys = ctx.getFromStorage(StorageId.RELATIONS_FROM_KEY);
        WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS);

        RecordKeys wfKeys = relationKeys != null ? relationKeys.getFrom() : fromRecordKeys;
        String user = SecurityUtils.getCurrentUserName();
        OriginsVistoryRelationsPO po = new OriginsVistoryRelationsPO();
        Date ts = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
        VistoryOperationType operationType = ctx.getFromStorage(StorageId.DATA_UPSERT_VISTORY_OPERATION_TYPE);

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
        po.setApproval(calculateVersionState(ctx, wfKeys, assignment));
        po.setMajor(platformConfiguration.getPlatformMajor());
        po.setMinor(platformConfiguration.getPlatformMinor());
        po.setOperationType(operationType == null ? VistoryOperationType.DIRECT : operationType);

        return po;
    }

    /**
     * Creates new etalons classifier data PO.
     * @param ctx the context (either delete or upsert)
     * @param status the status to set
     * @param ts timestamp
     * @return po
     */
    public static EtalonClassifierPO newEtalonClassifierPO(AbstractClassifierDataRequestContext ctx, RecordStatus status, Date ts) {

        String user = SecurityUtils.getCurrentUserName();
        WorkflowAssignmentDTO assignment = ctx.getFromStorage(StorageId.COMMON_WF_ASSIGNMENTS);

        EtalonClassifierPO po = new EtalonClassifierPO();

        String classifierName = null;
        String recordEtalonId = null;
        if (Objects.isNull(ctx.classifierKeys())) {
            // Upsert
            classifierName = ctx.getClassifierName();
            recordEtalonId = ctx.keys().getEtalonKey().getId();
        } else {
            // Delete and others
            classifierName = ctx.classifierKeys().getName();
            recordEtalonId = ctx.classifierKeys().getRecord().getEtalonKey().getId();
        }

        po.setId(IdUtils.v1String());
        po.setOperationId(ctx.getOperationId());
        po.setEtalonIdRecord(recordEtalonId);
        po.setName(classifierName);
        po.setCreatedBy(user);
        po.setCreateDate(ts == null ? new Date(System.currentTimeMillis()) : ts);
        po.setUpdatedBy(user);
        po.setStatus(status == null ? RecordStatus.ACTIVE : status);
        po.setApproval(calculateRecordState((ApprovalStateSettingContext) ctx, assignment));

        return po;
    }

    /**
     * Creates new etalons relation PO.
     * @param etalonId the etalon relation id
     * @param name the name
     * @param nodeId node id
     * @param recordKey from key
     * @param sourceSystem the source system
     * @param status the status to set
     * @param ts the timestamp
     * @return po
     */
    public static OriginClassifierPO newOriginsClassifierPO(
            String etalonId, String name, String nodeId, String recordKey, String sourceSystem, RecordStatus status, Date ts) {

        String user = SecurityUtils.getCurrentUserName();
        OriginClassifierPO po = new OriginClassifierPO();

        po.setId(IdUtils.v1String());
        po.setEtalonId(etalonId);
        po.setOriginIdRecord(recordKey);
        po.setName(name);
        po.setNodeId(nodeId);
        po.setSourceSystem(sourceSystem);
        po.setCreatedBy(user);
        po.setCreateDate(ts == null ? new Date(System.currentTimeMillis()) : ts);
        po.setUpdatedBy(user);
        po.setStatus(status == null ? RecordStatus.ACTIVE : status);

        return po;
    }

    /**
     * Creates new relation vistory record.
     * @param ctx the context to use
     * @param validFrom validity period start
     * @param validTo validity period end
     * @param data the data
     * @param status record status
     * @param ctx the context
     * @return record
     */
    public static OriginsVistoryClassifierPO newOriginsVistoryClassifierPO(
            ApprovalStateSettingContext ctx, Date validFrom, Date validTo, DataRecord data, RecordStatus status) {

        AbstractClassifierDataRequestContext actx = (AbstractClassifierDataRequestContext) ctx;

        ClassifierKeys keys = actx.classifierKeys();
        String user = SecurityUtils.getCurrentUserName();
        WorkflowAssignmentDTO assignment = actx.getFromStorage(StorageId.COMMON_WF_ASSIGNMENTS);

        OriginsVistoryClassifierPO po = new OriginsVistoryClassifierPO();

        po.setId(IdUtils.v1String());
        po.setOriginId(keys.getOriginId());
        po.setOperationId(actx.getOperationId());
        po.setValidFrom(validFrom);
        po.setValidTo(validTo);
        po.setCreatedBy(user);
        po.setCreateDate(new Date(System.currentTimeMillis()));
        po.setData(data);
        po.setStatus(status == null ? RecordStatus.ACTIVE : status);
        po.setApproval(calculateVersionState(ctx, keys.getRecord(), assignment));
        po.setMajor(platformConfiguration.getPlatformMajor());
        po.setMinor(platformConfiguration.getPlatformMinor());

        return po;
    }

    /**
     * Creates new transition PO record.
     * @param etalonId the etalon id
     * @param operationId operation id
     * @param type type
     * @return po
     */
    public static EtalonTransitionPO newEtalonTransitionPO(
            String etalonId, String operationId, TransitionType type) {

        String user = SecurityUtils.getCurrentUserName();
        EtalonTransitionPO po = new EtalonTransitionPO();

        po.setId(IdUtils.v1String());
        po.setEtalonId(etalonId);
        po.setOperationId(operationId);
        po.setType(type);
        po.setCreatedBy(user);

        return po;
    }

    /**
     * Creates an origin transition PO record.
     * @param originId the origin id
     * @return po
     */
    public static OriginTransitionPO newOriginTransitionPO(String originId) {

        OriginTransitionPO po = new OriginTransitionPO();
        po.setOriginId(originId);

        return po;
    }
}

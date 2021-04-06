/**
 *
 */
package com.unidata.mdm.backend.service.wf.impl;

import static java.lang.Boolean.FALSE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext.DeleteRelationRequestContextBuilder;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext.DeleteRequestContextBuilder;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext.UpsertRequestContextBuilder;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.WorkflowException;
import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessEndState;
import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessSupport;
import com.unidata.mdm.backend.common.integration.wf.WorkflowVariables;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RelationSide;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.dao.DataRecordsDao;
import com.unidata.mdm.backend.dao.OriginsVistoryDao;
import com.unidata.mdm.backend.dao.RelationsDao;
import com.unidata.mdm.backend.exchange.StandaloneConfiguration;
import com.unidata.mdm.backend.po.ContributorPO;
import com.unidata.mdm.backend.po.EtalonDraftStatePO;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.EtalonRelationDraftStatePO;
import com.unidata.mdm.backend.po.EtalonRelationPO;
import com.unidata.mdm.backend.po.TimeIntervalPO;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.RecordsServiceComponent;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordLifecycleListener;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.conf.WorkflowProcessType;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Approve / decline listener.
 */
@Profile("!" + StandaloneConfiguration.STANDALONE_PROFILE_NAME)
@Component("workflowChangeListener")
public class WorkflowChangeListenerImpl implements ActivitiEventListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowChangeListenerImpl.class);

    /**
     * 'Accept' action listener qualifier name.
     */
    public static final String ACCEPT_RECORD_ACTION_LISTENER_QUALIFIER = "acceptRecordActionListener";

    /**
     * 'Reject' action listener qualifier name.
     */
    public static final String REJECT_RECORD_ACTION_LISTENER_QUALIFIER = "rejectRecordActionListener";

    /**
     * 'Accept period' action listener qualifier name.
     */
    public static final String ACCEPT_PERIOD_RECORD_ACTION_LISTENER_QUALIFIER = "acceptRecordPeriodActionListener";

    @SuppressWarnings("serial")
    private static final List<RecordStatus> STATUS_TAGS
        = new ArrayList<RecordStatus>() {
        {
            add(RecordStatus.ACTIVE);
            add(RecordStatus.INACTIVE);
        }
    };
    /**
     * Data record DAO.
     */
    @Autowired
    private DataRecordsDao dataRecordsDao;
    /**
     * Origins vistory DAO.
     */
    @Autowired
    private OriginsVistoryDao originsVistoryDao;
    /**
     * Relations DAO.
     */
    @Autowired
    private RelationsDao relationsDao;
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * Records component.
     */
    @Autowired
    private RecordsServiceComponent recordsServiceComponent;
    /**
     * Relations service component.
     */
    @Autowired
    private RelationsServiceComponent relationsServiceComponent;
    /**
     * Common records component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Common relations component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;
    /**
     * MMS.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * 'Accept' action listener.
     */
    @Autowired
    @Qualifier(value = ACCEPT_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<CommonRequestContext> acceptRecordActionListener;
    /**
     * 'Reject' action listener.
     */
    @Autowired
    @Qualifier(value = REJECT_RECORD_ACTION_LISTENER_QUALIFIER)
    private DataRecordLifecycleListener<CommonRequestContext> rejectRecordActionListener;
    /**
     * Constructor.
     */
    public WorkflowChangeListenerImpl() {
        super();
    }

    /**
     * Process completion support.
     * @param evt the event to process
     */
    public void completeProcess(ActivitiEvent evt) {

        ProcessInstance pi = evt.getEngineServices().getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(evt.getProcessInstanceId())
                .includeProcessVariables()
                .singleResult();

        LOGGER.info("Called for process completion of type [{}] for [{}] on event type [{}].",
                pi.getProcessDefinitionKey(),
                pi.getBusinessKey(),
                evt.getType().name());

        Map<String, Object> variables = pi.getProcessVariables();
        WorkflowProcessType processType = WorkflowProcessType.valueOf((String) variables.get(WorkflowVariables.VAR_PROCESS_TYPE.getValue()));
        String operationId = (String) variables.get(WorkflowVariables.VAR_OPERATION_ID.getValue());
        String etalonId = pi.getBusinessKey();
        if (etalonId == null) {
            final String message = "Process definition: {}, Business key: {}, Event name: {} - Cannot do {} record. One or more parameter(s) missing.";
            LOGGER.warn(message,
                    pi.getProcessDefinitionKey(),
                    pi.getBusinessKey(),
                    evt.getType().name(),
                    processType);
            throw new WorkflowException(message, ExceptionId.EX_WF_COMPLETE_RECORD_FAILED_PARAMS_MISSING,
                    processType);
        }

        WorkflowProcessSupport support = configurationService.getProcessSupportByProcessDefinitionId(pi.getProcessDefinitionKey());
        WorkflowProcessEndState state = support.processEnd(pi.getProcessDefinitionKey(), variables);

        if (processType == WorkflowProcessType.RECORD_DELETE) {
            deleteRecord(etalonId, operationId, state.isComplete());
        } else if (processType == WorkflowProcessType.RECORD_EDIT) {
            editRecord(etalonId, operationId, state.isComplete());
        }
    }

    /**
     * Decline everything on process cancel.
     * @param evt the event
     */
    public void cancelProcess(ActivitiEvent evt) {

        ProcessInstance pi = evt.getEngineServices().getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(evt.getProcessInstanceId())
                .includeProcessVariables()
                .singleResult();

        LOGGER.info("Called for process completion of type [{}] for [{}] on event type [{}].",
                pi.getProcessDefinitionKey(),
                pi.getBusinessKey(),
                evt.getType().name());

        Map<String, Object> variables = pi.getProcessVariables();
        String operationId = (String) variables.get(WorkflowVariables.VAR_OPERATION_ID.getValue());

        String etalonId = pi.getBusinessKey();
        if (etalonId == null) {
            final String message = "Process definition: {}, Business key: {}, Event name: {} - Cannot DECLINE record. One or more parameter(s) missing.";
            LOGGER.warn(message,
                    pi.getProcessDefinitionKey(),
                    pi.getBusinessKey(),
                    evt.getType().name());
            throw new WorkflowException(message, ExceptionId.EX_WF_DECLINE_RECORD_FAILED_PARAMS_MISSING,
                    pi.getProcessDefinitionKey(),
                    pi.getBusinessKey(),
                    evt.getType().name());
        }

        declineRecord(etalonId, operationId);
    }

    private void deleteRecord(String etalonId, String operationId, boolean approve) {

        final RecordKeys keys = commonRecordsComponent.identify(
                EtalonKey.builder().id(etalonId).build());

        if (approve) {

            // 1. Switch to inactive state (remove pending state and update index)
            DeleteRequestContext ctx = new DeleteRequestContextBuilder()
                    .approvalState(ApprovalState.APPROVED)
                    .cascade(true)
                    .etalonKey(etalonId)
                    .workflowAction(true)
                    .inactivateEtalon(true)
                    .build();

            ctx.setOperationId(operationId);

            recordsServiceComponent.deleteRecord(ctx);
        } else {

            // 1. Remove pending state
            dataRecordsDao.changeEtalonApproval(etalonId, ApprovalState.APPROVED);

            // 2. Update index
            Map<RecordHeaderField, Object> fields = Collections.singletonMap(RecordHeaderField.FIELD_PENDING, FALSE);
            searchService.mark(keys.getEntityName(), etalonId, fields);
        }

        dataRecordsDao.cleanupEtalonStateDrafts(etalonId);
    }

    /**
     * Finish 'edit record' type process.
     * @param etalonId the record id
     * @param operationId the operation id
     * @param approve approve if true, decline otherwise
     */
    private void editRecord(String etalonId, String operationId, boolean approve) {

        MeasurementPoint.start();
        try {

            if (approve) {
                approveRecord(etalonId, operationId);
            } else {
                declineRecord(etalonId, operationId);
            }

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Approve record changes.
     * @param etalonId the record id
     * @param operationId the operation id
     * @return true upon success, false otherwise
     */
    private void approveRecord(String etalonId, String operationId) {

        MeasurementPoint.start();
        try {

            // 1. Approve versions.
            updateRecordApprovalState(etalonId, ApprovalState.APPROVED);

            // 2. Collect keys
            RecordKeys keys = commonRecordsComponent.identify(
                    EtalonKey.builder().id(etalonId).build());

            // 3. Create etalon calculation context
            final UpsertRequestContext uCtx = new UpsertRequestContextBuilder()
                    .approvalState(ApprovalState.APPROVED)
                    .returnEtalon(true)
                    .recalculateWholeTimeline(true)
                    .build();

            uCtx.putToStorage(StorageId.DATA_UPSERT_KEYS, keys);
            uCtx.putToStorage(StorageId.DATA_UPSERT_EXACT_ACTION, UpsertAction.UPDATE);
            uCtx.setOperationId(operationId);

            // 4. Possibly reset record status
            EtalonDraftStatePO draft = dataRecordsDao.loadLastEtalonStateDraft(etalonId);
            if (Objects.nonNull(draft) && draft.getStatus() != keys.getEtalonStatus()) {

                // 5.1 Update status
                EtalonRecordPO po = DataRecordUtils.createEtalonRecordPO(uCtx, keys, draft.getStatus());
                dataRecordsDao.upsertEtalonRecords(Collections.singletonList(po), false);

                // 5.2 Reset keys
                keys = commonRecordsComponent.identify(keys.getEtalonKey());
                uCtx.putToStorage(StorageId.DATA_UPSERT_KEYS, keys);
            }

            // 5. Wipe records without active approved versions and return
            boolean hasActiveVersions = hasActiveRecordVersions(etalonId, keys.getEntityName());
            if (!hasActiveVersions) {

                // 5.1 Process relations (decline or approve?)
                approveRelations(keys, operationId, hasActiveVersions);

                // 5.2 Prepare context, log and wipe record
                DeleteRequestContext dCtx = new DeleteRequestContextBuilder()
                        .etalonKey(etalonId)
                        .cascade(true)
                        .wipe(true)
                        .workflowAction(true)
                        .build();

                dCtx.skipNotification();
                dCtx.setOperationId(operationId);
                dCtx.putToStorage(StorageId.DATA_DELETE_KEYS, keys);

                acceptRecordActionListener.before(dCtx);

                // 5.3 Delete record
                recordsServiceComponent.deleteRecord(dCtx);

                // 5.4 Cleanup drafts
                dataRecordsDao.cleanupEtalonStateDrafts(etalonId);

                acceptRecordActionListener.after(dCtx);

                // 5.5 Return
                return;
            }

            // 6. Run before executors
            acceptRecordActionListener.before(uCtx);

            // 7. Approve relations
            approveRelations(keys, operationId, hasActiveVersions);

            // 8. Calculate etalon
            recordsServiceComponent.calculateEtalons(uCtx);

            // 9. Cleanup drafts
            dataRecordsDao.cleanupEtalonStateDrafts(etalonId);

            // 10. Run after executors
            acceptRecordActionListener.after(uCtx);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Declines changes for an origin. Not really a public method
     * @param etalonId the etalon id
     * @return true, if succesful, false otherwise
     */
    private void declineRecord(String etalonId, String operationId) {

        MeasurementPoint.start();
        try {

            // 1. Update record state
            updateRecordApprovalState(etalonId, ApprovalState.DECLINED);

            // 2. Collect keys
            RecordKeys keys = commonRecordsComponent.identify(EtalonKey.builder().id(etalonId).build());

            // 3. Create context
            boolean hasActiveVersions = hasActiveRecordVersions(etalonId, keys.getEntityName());
            DeleteRequestContext ctx = new DeleteRequestContextBuilder()
                    .etalonKey(etalonId)
                    .inactivateEtalon(hasActiveVersions ? true : false)
                    .cascade(true)
                    .wipe(!hasActiveVersions)
                    .workflowAction(true)
                    .build();

            ctx.skipNotification();
            ctx.setOperationId(operationId);
            ctx.putToStorage(StorageId.DATA_DELETE_KEYS, keys);

            // 4. Run before executors
            rejectRecordActionListener.before(ctx);

            // 5. Decline relations
            declineRelations(keys, operationId, hasActiveVersions);

            // 6. Cleanup drafts
            dataRecordsDao.cleanupEtalonStateDrafts(etalonId);

            // 7. Change record state (wipe or just reset)
            if (hasActiveVersions) {
                // 7.1 Update index
                Map<RecordHeaderField, Object> fields = Collections.singletonMap(RecordHeaderField.FIELD_PENDING, FALSE);
                searchService.mark(keys.getEntityName(), etalonId, fields);
            } else {

                // 7.2 Wipe record
                recordsServiceComponent.deleteRecord(ctx);
            }

            // 8. Run after executors
            rejectRecordActionListener.after(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Check time line for having {@link ApprovalState#APPROVED} and {@link RecordStatus#ACTIVE} versions.
     * @param timeline the time line
     * @return true, if has, false otherwise
     */
    private boolean hasActiveVersions(List<TimeIntervalPO> timeline) {

        boolean hasActiveVersions = false;
        for (int i = 0; timeline != null && i < timeline.size(); i++) {
            TimeIntervalPO ti = timeline.get(i);
            for (int j = 0; ti.getContributors() != null && j < ti.getContributors().length; j++) {

                ContributorPO co = ti.getContributors()[j];
                if (co.getApproval() == ApprovalState.APPROVED
                 && co.getStatus() == RecordStatus.ACTIVE) {
                    hasActiveVersions = true;
                    break;
                }
            }

            if (hasActiveVersions) {
                break;
            }
        }

        return hasActiveVersions;
    }

    /**
     * Tells if the record etalon has active versions or not.
     * @param etalonId the etalon id
     * @param entityName the entity name
     * @return true, if has, false otherwise
     */
    private boolean hasActiveRecordVersions(String etalonId, String entityName) {

        List<TimeIntervalPO> timeline = originsVistoryDao
                .loadContributingRecordsTimeline(etalonId, entityName, true);
        return hasActiveVersions(timeline);
    }

    /**
     * Tells, whether this relation etalon has active versions or not.
     * @param etalonId etalon id
     * @param shift entity name
     * @return true, if has, false otherwise
     */
    private boolean hasActiveRelationVersions(String etalonId) {

        List<TimeIntervalPO> timeline
            = relationsDao.loadContributingRelationTimeline(etalonId, true);
        return hasActiveVersions(timeline);
    }

    /**
     * Approves relations.
     * @param keys the keys
     * @param operationId operation id
     * @param parentHasActiveVersions whether parent has active versions
     */
    private void approveRelations(RecordKeys keys, String operationId, boolean parentHasActiveVersions) {

        Map<RelationDef, EntityDef> rels
            = metaModelService.getEntityRelations(keys.getEntityName(), false, true);
        for (Iterator<RelationDef> i = rels.keySet().iterator(); i.hasNext(); ) {
            RelationDef rd = i.next();

            List<EtalonRelationPO> relationEtalons
                = relationsDao.loadEtalonRelations(keys.getEtalonKey().getId(),
                    rd.getName(), STATUS_TAGS, RelationSide.FROM);

            for (EtalonRelationPO po : relationEtalons) {

                if (po.getApproval() != ApprovalState.PENDING) {
                    continue;
                }

                updateRelationApprovalState(po.getId(), ApprovalState.APPROVED);
                EtalonRelationDraftStatePO draft = relationsDao.loadLastEtalonStateDraft(po.getId());

                // Guard against old data with no draft states
                if (Objects.nonNull(draft) && draft.getStatus() != po.getStatus()) {
                    po.setStatus(draft.getStatus());
                    po.setApproval(ApprovalState.APPROVED);
                    po.setUpdateDate(null);
                    po.setUpdatedBy(SecurityUtils.getCurrentUserName());
                    relationsDao.upsertEtalonRelation(po, false);
                }

                relationsDao.cleanupEtalonStateDrafts(po.getId());

                boolean doDelete = keys.getEtalonStatus() == RecordStatus.INACTIVE && po.getStatus() != RecordStatus.INACTIVE;
                boolean doWipe = !parentHasActiveVersions || !hasActiveRelationVersions(po.getId());
                if (doDelete || doWipe) {

                    DeleteRelationRequestContext dCtx = new DeleteRelationRequestContextBuilder()
                            .relationEtalonKey(po.getId())
                            .wipe(doWipe)
                            .workflowAction(true)
                            .build();

                    relationsServiceComponent.deleteRelation(dCtx);
                }

                if (rd.getRelType() == RelType.CONTAINS && !doWipe) {
                    approveRecord(po.getEtalonIdTo(), operationId);
                }
            }
        }
    }

    /**
     * Declines record's relations.
     * @param keys parent record keys
     * @param operationId operation id
     * @param parentHasActiveVersions whether parent has active versions
     */
    private void declineRelations(RecordKeys keys, String operationId, boolean parentHasActiveVersions) {

        Map<RelationDef, EntityDef> rels
            = metaModelService
                .getEntityRelations(keys.getEntityName(), false, true);
        for (Iterator<RelationDef> i = rels.keySet().iterator(); i.hasNext(); ) {
            RelationDef rd = i.next();

            List<EtalonRelationPO> relationEtalons
                = relationsDao.loadEtalonRelations(keys.getEtalonKey().getId(), rd.getName(),
                    STATUS_TAGS, RelationSide.FROM);

            for (EtalonRelationPO po : relationEtalons) {

                if (po.getApproval() != ApprovalState.PENDING) {
                    continue;
                }

                updateRelationApprovalState(po.getId(), ApprovalState.DECLINED);
                relationsDao.cleanupEtalonStateDrafts(po.getId());

                boolean doDelete = keys.getEtalonStatus() == RecordStatus.INACTIVE && po.getStatus() != RecordStatus.INACTIVE;
                boolean doWipe = !parentHasActiveVersions || !hasActiveRelationVersions(po.getId());
                if (doDelete || doWipe) {

                    DeleteRelationRequestContext dCtx = new DeleteRelationRequestContextBuilder()
                            .relationEtalonKey(po.getId())
                            .wipe(doWipe)
                            .workflowAction(true)
                            .build();

                    dCtx.setOperationId(operationId);
                    relationsServiceComponent.deleteRelation(dCtx);
                }

                // Run decline record, if other versions exist,
                // since otherwise the statement above should have the target record removed already.
                if (rd.getRelType() == RelType.CONTAINS && !doWipe) {
                    declineRecord(po.getEtalonIdTo(), operationId);
                }
            }
        }
    }

    /**
     * Set versions approval state.
     * @param etalonId record id
     * @param state approval state to set
     */
    private void updateRecordApprovalState(String etalonId, ApprovalState state) {

        // 1. Update versions
        if (!originsVistoryDao.updateApprovalState(etalonId, state)) {
            if (state == ApprovalState.APPROVED) {
                final String msg = "Approve failed. Pending version(s) cannot be updated.";
                LOGGER.warn(msg);
                throw new WorkflowException(msg, ExceptionId.EX_WF_APPROVE_RECORD_FAILED_VERSIONS_UPDATE_ERROR);
            } else if (state == ApprovalState.DECLINED) {
                final String msg = "Decline failed. Pending version(s) cannot be updated.";
                LOGGER.warn(msg);
                throw new WorkflowException(msg, ExceptionId.EX_WF_DECLINE_RECORD_FAILED_VERSIONS_UPDATE_ERROR);
            }
        }

        // 2. Update record
        commonRecordsComponent.changeApproval(etalonId, state == ApprovalState.PENDING ? state : ApprovalState.APPROVED);
    }

    /**
     * Approves pending versions.
     * @param relationEtalonId the etalonn id
     * @param state the state to set
     */
    private void updateRelationApprovalState(String relationEtalonId, ApprovalState state) {

        // 1. Update state of relations versions
        if (!relationsDao.updateApprovalState(relationEtalonId, state)) {
            final String message = "Approve failed. Pending relation versions cannot be updated.";
            LOGGER.warn(message);
            throw new WorkflowException(message, ExceptionId.EX_WF_APPROVE_RECORD_FAILED_RELATIONS_VERSIONS_UPDATE_ERROR);
        }

        // 2. Update relation record
        commonRelationsComponent.changeApproval(relationEtalonId, state == ApprovalState.PENDING ? state : ApprovalState.APPROVED);
    }

    /**
     * Process engine event.
     * @param event the event
     */
    @Override
    @Transactional
    public void onEvent(ActivitiEvent event) {

        switch (event.getType()) {
            case PROCESS_COMPLETED:
                completeProcess(event);
                break;
            case PROCESS_CANCELLED:
                cancelProcess(event);
                break;
            default:
                LOGGER.info("Skipping workflow event {}.", event.getType());
        }
    }

    /**
     * @see ActivitiEventListener#isFailOnException()
     */
    @Override
    public boolean isFailOnException() {
        return true;
    }
}

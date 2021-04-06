/**
 *
 */
package com.unidata.mdm.backend.service.data.listener;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext.GetRequestContextBuilder;
import com.unidata.mdm.backend.common.context.StartProcessRequestContext;
import com.unidata.mdm.backend.common.context.StartProcessRequestContext.StartProcessRequestContextBuilder;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowAssignmentDTO;
import com.unidata.mdm.backend.common.integration.auth.User;
import com.unidata.mdm.backend.common.integration.wf.WorkflowVariables;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.data.RecordsServiceComponent;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowService;

/**
 * @author Mikhail Mikhailov
 * Abstract workflow process start executor part.
 */
public abstract class AbstractWorkflowProcessStarterAfterExecutor {

    /**
     * Workflow service instance.
     */
    @Autowired(required = false)
    protected WorkflowService workflowService;
    /**
     * Security service.
     */
    @Autowired
    protected SecurityServiceExt securityService;
    /**
     * MMS.
     */
    @Autowired
    protected MetaModelServiceExt metaModelService;
    /**
     * Records component.
     */
    @Autowired
    protected RecordsServiceComponent recordsComponent;
    /**
     * Etalon component.
     */
    @Autowired
    protected EtalonRecordsComponent etalonsComponent;
    /**
     * Constructor.
     */
    public AbstractWorkflowProcessStarterAfterExecutor() {
        super();
    }

    /**
     * Creates workflow context.
     * @param assignment process assignment
     * @param keys record keys
     * @param etalon the etalon record
     * @param isPublished whether the record is published or not
     * @param isDelete is a delete context (upsert otherwise)
     * @param  validFrom affected period from date
     * @param  validToaffected period to date
     * @return workflow context
     */
    protected StartProcessRequestContext createStartWorkflowContext(WorkflowAssignmentDTO assignment,
            RecordKeys keys, EtalonRecord etalon, boolean isPublished, boolean isDelete,
            String operationId, Date validFrom, Date validTo) {

        User userInfo = securityService.getUserByToken(SecurityUtils.getCurrentUserToken());

        String typeTitle;
        String entityType;
        Collection<Pair<String, AttributeInfoHolder>> mainDisplayables;
        if (metaModelService.isEntity(keys.getEntityName())) {
            EntityWrapper entity = metaModelService.getValueById(keys.getEntityName(), EntityWrapper.class);
            typeTitle = entity.getEntity().getDisplayName();
            entityType = "Entity";
            mainDisplayables = entity.getMainDisplayableAttributes();
        } else {
            LookupEntityWrapper lookup = metaModelService.getValueById(keys.getEntityName(), LookupEntityWrapper.class);
            typeTitle = lookup.getEntity().getDisplayName();
            entityType = "LookupEntity";
            mainDisplayables = lookup.getMainDisplayableAttributes();
        }

        EtalonRecord recordToUse;
        if (Objects.isNull(etalon)) {
            recordToUse = materializeFirstActivePeriod(keys);
        } else {
            recordToUse = etalon;
        }

        String recordTitle = mainDisplayables.stream()
                .map(Pair::getKey)
                .map(recordToUse::getAttributeRecursive)
                .flatMap(Collection::stream)
                .filter(attr -> attr.getAttributeType() == AttributeType.SIMPLE || attr.getAttributeType() == AttributeType.CODE)
                .map(attr -> attr.getAttributeType() == AttributeType.SIMPLE ? ((SimpleAttribute<?>) attr).getValue() : ((CodeAttribute<?>) attr).getValue())
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining("|"));

        Map<String, Object> variables = new HashMap<>();
        variables.put(WorkflowVariables.VAR_PROCESS_TYPE.getValue(), assignment.getType().name());
        variables.put(WorkflowVariables.VAR_PROCESS_TRIGGER_TYPE.getValue(), assignment.getTriggerType() == null ? "" : assignment.getTriggerType().asString());
        variables.put(WorkflowVariables.VAR_ETALON_ID.getValue(), keys.getEtalonKey().getId());
        variables.put(WorkflowVariables.VAR_ENTITY_NAME.getValue(), keys.getEntityName());
        variables.put(WorkflowVariables.VAR_PUBLISHED_STATE.getValue(), isPublished);
        variables.put(WorkflowVariables.VAR_ENTITY_TYPE.getValue(), entityType);
        variables.put(WorkflowVariables.VAR_ENTITY_TYPE_TITLE.getValue(), typeTitle);
        variables.put(WorkflowVariables.VAR_ETALON_RECORD_TITLE.getValue(), recordTitle == null ? "No main displayable attribute" : recordTitle);
        variables.put(WorkflowVariables.VAR_INITIATOR.getValue(), userInfo.getLogin());
        variables.put(WorkflowVariables.VAR_INITIATOR_EMAIL.getValue(), userInfo.getEmail());
        variables.put(WorkflowVariables.VAR_INITIATOR_NAME.getValue(), userInfo.getName());
        variables.put(WorkflowVariables.VAR_WF_CREATE_DATE.getValue(), new Date());
        variables.put(WorkflowVariables.VAR_OPERATION_ID.getValue(), operationId);

        // Use variables set by the system at process start stage
        variables.put(WorkflowVariables.VAR_FROM.getValue(), validFrom);
        variables.put(WorkflowVariables.VAR_TO.getValue(), validTo);

        // save delete flag
        variables.put(WorkflowVariables.VAR_DELETE_PERIOD_OPERATION.getValue(), isDelete);

        return new StartProcessRequestContextBuilder()
            .processDefinitionId(assignment.getProcessName())
            .variables(variables)
            .initiator(userInfo.getLogin())
            .processKey(keys.getEtalonKey().getId())
            .build();
    }

    /**
     * Materializes data for first active period.
     * @param keys
     * @return
     */
    private EtalonRecord materializeFirstActivePeriod(RecordKeys keys) {

        final GetRequestContext ctx = new GetRequestContextBuilder()
                .tasks(false)
                .includeDrafts(false)
                .build();

        ctx.putToStorage(StorageId.DATA_GET_KEYS, keys);

        TimelineDTO timeline = recordsComponent.loadRecordsTimeline(ctx);
        for (TimeIntervalDTO interval : timeline.getIntervals()) {
            if (interval.isActive()) {
                Date point = interval.getValidFrom() != null ? interval.getValidFrom() : interval.getValidTo();
                return etalonsComponent.loadEtalonData(keys.getEtalonKey().getId(), point, null, null, null, false, false);
            }
        }

        return null;
    }
}

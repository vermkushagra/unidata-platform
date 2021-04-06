package com.unidata.mdm.backend.service.wf.standard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.integration.wf.WorkflowAction;
import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessEndState;
import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessStartState;
import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessSupport;
import com.unidata.mdm.backend.common.integration.wf.WorkflowTaskCompleteState;
import com.unidata.mdm.backend.common.integration.wf.WorkflowTaskGate;
import com.unidata.mdm.backend.common.integration.wf.WorkflowVariables;
import com.unidata.mdm.backend.common.security.SecurityToken;
import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.common.service.ServiceUtils;


/**
 * @author Mikhail Mikhailov
 * Standard record approval process support.
 */
public class StandardRecordApprovalProcessSupport implements WorkflowProcessSupport, WorkflowTaskGate {

    /**
     * Approval group 1 role name.
     */
    private static final String APPROVAL_GROUP_1 = "approvalGroup1";
    /**
     * Approval group 2 role name.
     */
    private static final String APPROVAL_GROUP_2 = "approvalGroup2";

    /**
     * Default actions.
     */
    @SuppressWarnings("serial")
    private List<WorkflowAction> defaultActions
        = new ArrayList<WorkflowAction>(2) {
        {
            add(new WorkflowAction("APPROVED", "Подтвердить изменения", "Подтвердить изменения записи"));
            add(new WorkflowAction("DECLINED", "Отклонить изменения", "Отклонить изменения записи"));
        }
    };

    /**
     * Constructor.
     */
    public StandardRecordApprovalProcessSupport() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.integration.wf.WorkflowTaskGate#getActions(java.lang.String, java.util.Map)
     */
    @Override
    public List<WorkflowAction> getActions(String taskDefinitionId, Map<String, Object> variables) {
        // Ignore task definition id ignored,
        // since there are always only two action for every user task
        return defaultActions;
    }
    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.common.integration.wf.WorkflowTaskGate#complete(java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    public WorkflowTaskCompleteState complete(String taskDefinitionId, Map<String, Object> variables,
            String actionCode) {

        return "APPROVED".equals(actionCode)
                ? new WorkflowTaskCompleteState(true, "Approve task.")
                : new WorkflowTaskCompleteState(true, "Decline task.");
    }

    /*
     * (non-Javadoc)
     * @see com.unidata.mdm.backend.common.integration.wf.WorkflowProcessSupport#processComplete(java.lang.String, java.util.Map)
     */
    @Override
    public WorkflowProcessEndState processEnd(String processDefinitionId, Map<String, Object> variables) {
        WorkflowProcessEndState state = new WorkflowProcessEndState();
        if (!CollectionUtils.isEmpty(variables)) {
            String approvalState = (String) variables.get(WorkflowVariables.VAR_APPROVAL_STATE.getValue());
            if ("APPROVED".equals(approvalState)) {
                state.setComplete(true);
                state.setMessage("Submit changes.");
            } else {
                state.setComplete(false);
                state.setMessage("Decline changes.");
            }

        } else {
            state.setComplete(false);
            state.setMessage("Cannot determine exit state.");
        }

        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowProcessStartState processStart(String processDefinitionId, Map<String, Object> variables) {

        WorkflowProcessStartState state = new WorkflowProcessStartState(true, "");

        Map<String, Object> additionalProcessVariables = new HashMap<>();
        SecurityService securityService = ServiceUtils.getSecurityService();
        SecurityToken token = securityService.getTokenObjectByToken(securityService.getCurrentUserToken());

        Boolean skipStep1 = token.hasRole(APPROVAL_GROUP_1);
        Boolean skipStep2 = token.hasRole(APPROVAL_GROUP_2);

        additionalProcessVariables.put("skipStep1", skipStep1);
        additionalProcessVariables.put("skipStep2", skipStep2);

        state.setAdditionalProcessVariables(additionalProcessVariables);

        return state;
    }
}

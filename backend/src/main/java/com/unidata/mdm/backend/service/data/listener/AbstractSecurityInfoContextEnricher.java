package com.unidata.mdm.backend.service.data.listener;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.CommonRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.WorkflowService;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Does various enrichments, related to security in the request context scope.
 */
public abstract class AbstractSecurityInfoContextEnricher<T extends CommonRequestContext> {

    /**
     * Workflow service instance.
     */
    @Autowired(required = false)
    protected WorkflowService workflowService;

    /**
     * Constructor.
     */
    public AbstractSecurityInfoContextEnricher() {
        super();
    }

    /**
     * Adds security info to context.
     * @param t the context
     * @param id the id
     * @param resourceName name of the resource
     */
    protected void putResourceRights(T t, StorageId id, String resourceName) {
        t.putToStorage(id, SecurityUtils.getRightsForResourceWithDefault(resourceName));
    }
    /**
     * Adds workflow information to the given context.
     * @param t the context
     * @param id the id
     * @param resourceName name of the resource
     * @param processType type of the process
     */
    protected void putWorkflowAssignments(T t, StorageId id, String resourceName, WorkflowProcessType processType) {
        if(workflowService != null){
            t.putToStorage(id, workflowService.getAssignmentsByEntityNameAndType(resourceName, processType));
        }
    }
}

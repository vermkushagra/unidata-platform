package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowCompletionStateRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowCompletionStateDTO;

/**
 * @author Mikhail Mikhailov
 * WF completion state converter.
 */
public class WorkflowCompletionStateConverter {

    /**
     * Constructor.
     */
    private WorkflowCompletionStateConverter() {
        super();
    }

    /**
     * From internal to REST.
     * @param source the source
     * @return REST object
     */
    public static WorkflowCompletionStateRO to(WorkflowCompletionStateDTO source) {

        WorkflowCompletionStateRO result = new WorkflowCompletionStateRO();
        result.setComplete(source.isComplete());
        result.setMessage(source.getMessage());

        return result;
    }
}

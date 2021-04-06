/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowStateRO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowStateDTO;

/**
 * @author Mikhail Mikhailov
 * WF state converter.
 */
public class WorkflowStateConverter {

    /**
     * Constructor.
     */
    private WorkflowStateConverter() {
        super();
    }

    /**
     * From internal to REST
     * @param source the source
     * @return REST state
     */
    public static WorkflowStateRO to(WorkflowStateDTO source) {

        if (source == null) {
            return null;
        }

        WorkflowStateRO target = new WorkflowStateRO();
        target.setTotalCount(source.getTotalCount());
        target.setTasks(WorkflowTaskConverter.to(source.getTasks()));

        return target;
    }
}

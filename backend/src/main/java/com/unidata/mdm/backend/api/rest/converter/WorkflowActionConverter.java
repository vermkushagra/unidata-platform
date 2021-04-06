package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowActionRO;
import com.unidata.mdm.backend.common.integration.wf.WorkflowAction;

/**
 * @author Mikhail Mikhailov
 * Actions converter.
 */
public class WorkflowActionConverter {

    /**
     * Constructor.
     */
    private WorkflowActionConverter() {
        super();
    }

    /**
     * From internal to REST.
     * @param action the action
     * @return REST
     */
    public static List<WorkflowActionRO> to(List<WorkflowAction> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<WorkflowActionRO> result = new ArrayList<>();
        for (WorkflowAction action : source) {
            result.add(to(action));
        }

        return result;
    }

    /**
     * From internal to REST.
     * @param action the action
     * @return REST
     */
    public static WorkflowActionRO to(WorkflowAction source) {

        WorkflowActionRO result = new WorkflowActionRO();
        result.setCode(source.getCode());
        result.setDescription(source.getDescription());
        result.setName(source.getName());

        return result;
    }
}

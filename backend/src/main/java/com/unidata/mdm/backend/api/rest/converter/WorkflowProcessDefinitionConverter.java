package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.wf.WorkflowProcessDefinitionRO;
import com.unidata.mdm.conf.WorkflowProcessDefinition;

/**
 * @author Mikhail Mikhailov
 * Process definition converter.
 */
public class WorkflowProcessDefinitionConverter {

    /**
     * Constructor.
     */
    private WorkflowProcessDefinitionConverter() {
        super();
    }

    /**
     * To external type.
     * @param source the source
     * @return REST object
     */
    public static WorkflowProcessDefinitionRO to(WorkflowProcessDefinition source) {

        if (source == null) {
            return null;
        }

        WorkflowProcessDefinitionRO target = new WorkflowProcessDefinitionRO();
        target.setDescription(source.getDescription());
        target.setId(source.getId());
        target.setName(source.getName());
        target.setPath(source.getPath());
        target.setType(source.getType() != null ? source.getType().name() : null);

        return target;
    }

    /**
     * To external type.
     * @param source the source
     * @return REST object
     */
    public static List<WorkflowProcessDefinitionRO> to(List<WorkflowProcessDefinition> source) {

        if (source != null && !source.isEmpty()) {

            List<WorkflowProcessDefinitionRO> result = new ArrayList<>();
            for (WorkflowProcessDefinition wpd : source) {
                result.add(WorkflowProcessDefinitionConverter.to(wpd));
            }

            return result;
        }

        return Collections.emptyList();
    }
}

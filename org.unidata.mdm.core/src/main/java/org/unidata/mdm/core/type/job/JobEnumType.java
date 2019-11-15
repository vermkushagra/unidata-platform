package org.unidata.mdm.core.type.job;

import java.util.List;

/**
 * @author Denis Kostovarov
 */
public class JobEnumType {
    private boolean multiSelect;
    private JobParameterType parameterType;

    private List<?> parameters;

    public JobParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(JobParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public List<?> getParameters() {
        return parameters;
    }

    public void setParameters(List<?> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the multiSelect
     */
    public boolean isMultiSelect() {
        return multiSelect;
    }

    /**
     * @param multiSelect the multiSelect to set
     */
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
}

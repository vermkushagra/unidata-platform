package org.unidata.mdm.core.dto.job;

import org.unidata.mdm.core.type.job.JobParameterType;

/**
 * @author Denis Kostovarov
 */
public class JobTemplateParameterDTO {
    private Long id;

    private String name;

    private JobParameterType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobParameterType getType() {
        return type;
    }

    public void setType(JobParameterType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

package com.unidata.mdm.backend.dto.job;

/**
 * @author Denis Kostovarov
 */
public class JobTriggerDTO {
    private Long id;
    private Long finishJobId;
    private Long startJobId;
    private Boolean successRule;
    private String name;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFinishJobId() {
        return finishJobId;
    }

    public void setFinishJobId(Long finishJobId) {
        this.finishJobId = finishJobId;
    }

    public Long getStartJobId() {
        return startJobId;
    }

    public void setStartJobId(Long startJobId) {
        this.startJobId = startJobId;
    }

    public Boolean getSuccessRule() {
        return successRule;
    }

    public void setSuccessRule(Boolean successRule) {
        this.successRule = successRule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

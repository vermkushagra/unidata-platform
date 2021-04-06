/**
 * Date: 29.04.2016
 */

package com.unidata.mdm.backend.dto.job;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class StepExecutionFilter {
    private Long jobExecutionId;
    private Long fromInd;
    private Integer itemCount;

    public Long getJobExecutionId() {
        return jobExecutionId;
    }

    public void setJobExecutionId(Long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
    }

    public Long getFromInd() {
        return fromInd;
    }

    public void setFromInd(Long fromInd) {
        this.fromInd = fromInd;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
}

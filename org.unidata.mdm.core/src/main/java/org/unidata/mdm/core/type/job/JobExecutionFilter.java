/**
 * Date: 29.04.2016
 */

package org.unidata.mdm.core.type.job;

import java.util.List;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobExecutionFilter {
    private List<Long> jobInstanceIds;
    private Long fromInd;
    private Integer itemCount;

    public List<Long> getJobInstanceIds() {
        return jobInstanceIds;
    }

    public void setJobInstanceIds(List<Long> jobInstanceIds) {
        this.jobInstanceIds = jobInstanceIds;
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

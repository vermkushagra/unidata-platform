/**
 * Date: 29.04.2016
 */

package com.unidata.mdm.backend.api.rest.dto.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class PaginatedJobExecutionsResultRO {
    private List<JobExecutionRO> content;

    @JsonProperty(value = "total_count")
    private int totalCount;

    public List<JobExecutionRO> getContent() {
        return content;
    }

    public void setContent(List<JobExecutionRO> content) {
        this.content = content;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}

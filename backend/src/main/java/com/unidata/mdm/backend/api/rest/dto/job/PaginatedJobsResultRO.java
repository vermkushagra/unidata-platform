/**
 * Date: 05.05.2016
 */

package com.unidata.mdm.backend.api.rest.dto.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class PaginatedJobsResultRO {
    private List<JobRO> content;

    @JsonProperty(value = "total_count")
    private int totalCount;

    public List<JobRO> getContent() {
        return content;
    }

    public void setContent(List<JobRO> content) {
        this.content = content;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}

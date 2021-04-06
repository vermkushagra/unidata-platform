/**
 * Date: 05.05.2016
 */

package com.unidata.mdm.backend.dto.job;

/**
 * FIXDOC: add file description.
 *
 * @author amagdenko
 */
public class JobFilter {
    private Long fromInd;
    private Integer itemCount;
    private Boolean enabled;

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

/**
 * Date: 29.04.2016
 */

package com.unidata.mdm.backend.common.dto;

import java.util.List;

/**
 * Common class used to return one page with overall items count.
 *
 * @author amagdenko
 */
public abstract class PaginatedResultDTO<T> {
    /**
     * The page - data portion.
     */
    private List<T> page;
    /**
     * Total count of the items.
     */
    private int totalCount;
    /**
     * Gets current page.
     * @return 'page' list
     */
    public List<T> getPage() {
        return page;
    }
    /**
     * Sets the page.
     * @param list the page to set
     */
    public void setPage(List<T> list) {
        this.page = list;
    }
    /**
     * Gets total items count.
     * @return total count
     */
    public int getTotalCount() {
        return totalCount;
    }
    /**
     * Sets total items count.
     * @param overallCount the total count to set
     */
    public void setTotalCount(int overallCount) {
        this.totalCount = overallCount;
    }
}

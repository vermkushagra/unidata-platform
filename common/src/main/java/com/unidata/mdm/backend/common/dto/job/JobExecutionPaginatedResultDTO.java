package com.unidata.mdm.backend.common.dto.job;

import com.unidata.mdm.backend.common.dto.PaginatedResultDTO;

/**
 * @author Mikhail Mikhailov
 * Paginated execution state of a job.
 */
public class JobExecutionPaginatedResultDTO<T> extends PaginatedResultDTO<T> {
    /**
     * Failed count.
     */
    private int finishedCount;
    /**
     * Constructor.
     */
    public JobExecutionPaginatedResultDTO() {
        super();
    }
    /**
     * @return the finishedCount
     */
    public int getFinishedCount() {
        return finishedCount;
    }
    /**
     * @param finishedCount the finishedCount to set
     */
    public void setFinishedCount(int finishedCount) {
        this.finishedCount = finishedCount;
    }
}

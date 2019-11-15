package org.unidata.mdm.core.dto.job;

import org.unidata.mdm.core.dto.PaginatedResultDTO;

/**
 * @author Mikhail Mikhailov
 *
 */
public class StepExecutionPaginatedResultDTO<T> extends PaginatedResultDTO<T> {
    /**
     * Failed count.
     */
    private int finishedCount;
    /**
     * Constructor.
     */
    public StepExecutionPaginatedResultDTO() {
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

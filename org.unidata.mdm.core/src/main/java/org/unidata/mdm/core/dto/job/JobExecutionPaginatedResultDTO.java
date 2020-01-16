/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.unidata.mdm.core.dto.job;

import org.unidata.mdm.core.dto.PaginatedResultDTO;

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

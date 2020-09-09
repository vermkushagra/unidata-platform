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

package com.unidata.mdm.backend.service.job.exchange.out;

/**
 * @author Mikhail Mikhailov
 * Statistic page.
 */
public class ExportDataStatisticPage {
    /**
     * Failed.
     */
    private long failed = 0L;
    /**
     * Skept.
     */
    private long skept = 0L;
    /**
     * Updated.
     */
    private long updated = 0L;
    /**
     * Inserted.
     */
    private long inserted = 0L;
    /**
     * @param failed the failed to set
     */
    public void incrementFailed(long failed) {
        this.failed += failed;
    }
    /**
     * @param skept the skept to set
     */
    public void incrementSkept(long skept) {
        this.skept += skept;
    }
    /**
     * @param updated the updated to set
     */
    public void incrementUpdated(long updated) {
        this.updated += updated;
    }
    /**
     * @param inserted the inserted to set
     */
    public void incrementInserted(long inserted) {
        this.inserted += inserted;
    }
    /**
     * @return the failed
     */
    public long getFailed() {
        return failed;
    }
    /**
     * @return the skept
     */
    public long getSkept() {
        return skept;
    }
    /**
     * @return the updated
     */
    public long getUpdated() {
        return updated;
    }
    /**
     * @return the inserted
     */
    public long getInserted() {
        return inserted;
    }
}
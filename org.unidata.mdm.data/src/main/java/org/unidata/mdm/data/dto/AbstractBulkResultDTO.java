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

package org.unidata.mdm.data.dto;

import org.unidata.mdm.system.dto.AbstractCompositeResult;

/**
 * @author Mikhail Mikhailov on Dec 16, 2019
 */
public class AbstractBulkResultDTO extends AbstractCompositeResult {
    /**
     * Updated count. Delete period writes to this variable.
     */
    protected long updated = 0L;
    /**
     * General operation failure count.
     */
    protected long failed = 0L;
    /**
     * Skipped due to NO_ACTION or the like.
     */
    protected long skipped = 0L;
    /**
     * Number of inserted
     */
    protected long inserted = 0L;
    /**
     * Number of deleted.
     */
    protected long deleted = 0L;
    /**
     * @return the updated
     */
    public long getUpdated() {
        return updated;
    }
    /**
     * @param updated the updated to set
     */
    public void setUpdated(long updated) {
        this.updated = updated;
    }
    /**
     * @return the failed
     */
    public long getFailed() {
        return failed;
    }
    /**
     * @param failed the failed to set
     */
    public void setFailed(long failed) {
        this.failed = failed;
    }
    /**
     * @return the skipped
     */
    public long getSkipped() {
        return skipped;
    }
    /**
     * @param skipped the skipped to set
     */
    public void setSkipped(long skipped) {
        this.skipped = skipped;
    }
    /**
     * @return the inserted
     */
    public long getInserted() {
        return inserted;
    }
    /**
     * @param inserted the inserted to set
     */
    public void setInserted(long inserted) {
        this.inserted = inserted;
    }
    /**
     * @return the deleted
     */
    public long getDeleted() {
        return deleted;
    }
    /**
     * @param deleted the deleted to set
     */
    public void setDeleted(long deleted) {
        this.deleted = deleted;
    }
}

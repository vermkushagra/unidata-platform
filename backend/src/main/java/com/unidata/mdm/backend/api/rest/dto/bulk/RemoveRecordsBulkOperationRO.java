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

package com.unidata.mdm.backend.api.rest.dto.bulk;

import com.unidata.mdm.backend.common.types.BulkOperationType;

public class RemoveRecordsBulkOperationRO extends BulkOperationBaseRO {

    /**
     * wipe
     */
    private boolean wipe = false;

    /**
     * Constructor.
     */
    public RemoveRecordsBulkOperationRO() {
        super();
    }

    /**
     * Bulk operation type.
     *
     * @return type
     */
    @Override
    public BulkOperationType getType() {
        return BulkOperationType.REMOVE_RECORDS;
    }

    public void setWipe(boolean wipe) {
        this.wipe = wipe;
    }

    public boolean isWipe() {
        return wipe;
    }
}

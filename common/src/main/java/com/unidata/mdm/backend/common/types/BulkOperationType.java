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

package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Bulk operation types.
 */
public enum BulkOperationType {

    /**
     * Modify several records at once.
     */
    MODIFY_RECORDS("app.data.bulk.operation.modify"),
    /**
     * Republish several records to a selected notification target.
     */
    REPUBLISH_RECORDS("app.data.bulk.operation.republish"),
    /**
     * Import records from XLS.
     */
    IMPORT_RECORDS_FROM_XLS("app.data.bulk.operation.import"),
    /**
     * Export records to XLS.
     */
    EXPORT_RECORDS_TO_XLS("app.data.bulk.operation.export"),
    /**
     * Logical remove records
     */
    REMOVE_RECORDS("app.data.bulk.operation.remove"),
    /**
     * Remove connections
     */
    REMOVE_RELATIONS_FROM("app.data.bulk.operation.remove.relations.from");
    /**
     * Constructor.
     * @param description the description
     */
    BulkOperationType(String description) {
        this.decsription = description;
    }
    /**
     * Description field.
     */
    private final String decsription;
    /**
     * @return the decsription
     */
    public String getDecsription() {
        return decsription;
    }
}

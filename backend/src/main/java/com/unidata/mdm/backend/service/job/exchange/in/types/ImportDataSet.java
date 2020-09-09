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

package com.unidata.mdm.backend.service.job.exchange.in.types;

import java.util.Date;

import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;

/**
 * @author Mikhail Mikhailov
 * Base class for import data set.
 */
public class ImportDataSet {
    /**
     * The data of the main record to import.
     */
    private DataRecord data;
    /**
     * Valid from field.
     */
    private Date validFrom;
    /**
     * Valid to field.
     */
    private Date validTo;
    /**
     * The status to set.
     */
    private RecordStatus status;
    /**
     * Import row num
     */
    private int importRowNum;
    /**
     * Constructor.
     * @param data the data
     */
    public ImportDataSet(DataRecord data) {
        this.data = data;
    }
    /**
     * @return the data
     */
    public DataRecord getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(DataRecord data) {
        this.data = data;
    }
    /**
     * @return the validFrom
     */
    public Date getValidFrom() {
        return validFrom;
    }
    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }
    /**
     * @return the validTo
     */
    public Date getValidTo() {
        return validTo;
    }
    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }
    /**
     * @return the status
     */
    public RecordStatus getStatus() {
        return status;
    }
    /**
     * @param status the status to set
     */
    public void setStatus(RecordStatus status) {
        this.status = status;
    }
    /**
     * @param importRowNum - import row number
     */
    public void setImportRowNum(int importRowNum) {
        this.importRowNum = importRowNum;
    }
    /**
     * @return import row number
     */
    public int getImportRowNum() {
        return importRowNum;
    }
    /**
     * Is record or not.
     * @return true, if relation, false otherwise
     */
    public boolean isRelation() {
        return false;
    }
    /**
     * Is record or not.
     * @return true, if record, false otherwise
     */
    public boolean isRecord() {
        return false;
    }
}

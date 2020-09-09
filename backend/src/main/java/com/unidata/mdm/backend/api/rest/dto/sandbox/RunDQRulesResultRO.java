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

package com.unidata.mdm.backend.api.rest.dto.sandbox;

import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.data.DQErrorRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;

public class RunDQRulesResultRO {
    private EtalonRecordRO record;
    private List<DQErrorRO> dqErrors;

    public RunDQRulesResultRO() {
    }

    public RunDQRulesResultRO(EtalonRecordRO record, List<DQErrorRO> dqErrors) {
        this.record = record;
        this.dqErrors = dqErrors;
    }

    public List<DQErrorRO> getDqErrors() {
        return dqErrors;
    }

    public void setDqErrors(List<DQErrorRO> dqErrors) {
        this.dqErrors = dqErrors;
    }

    /**
     * @return the record
     */
    public EtalonRecordRO getRecord() {
        return record;
    }

    /**
     * @param record the record to set
     */
    public void setRecord(EtalonRecordRO record) {
        this.record = record;
    }
}

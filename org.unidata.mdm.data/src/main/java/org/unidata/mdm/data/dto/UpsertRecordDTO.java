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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.system.dto.AbstractCompositeResult;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 * Upsert result DTO.
 */
public class UpsertRecordDTO extends AbstractCompositeResult implements RecordDTO, EtalonRecordDTO, PipelineOutput {
    /**
     * Record keys for short upsert.
     */
    private RecordKeys recordKeys;
    /**
     * Etalon record for this period or null.
     */
    private EtalonRecord etalon;
    /**
     * All periods.
     */
    private Collection<EtalonRecord> periods;
    /**
     * Actual action.
     */
    private UpsertAction action;
    /**
     * Number of duplicates.
     */
    private List<String> duplicateIds;
    /**
     * list of errors
     */
    private List<ErrorInfoDTO> errors;
    /**
     * Constructor.
     * @param type action performed
     */
    public UpsertRecordDTO() {
        this(UpsertAction.NO_ACTION);
    }
    /**
     * Constructor.
     * @param type action performed
     */
    public UpsertRecordDTO(UpsertAction type) {
        this(type, null, null);
    }
    /**
     * Constructor.
     * @param type action performed
     * @param etalon the etalon record to upsert
     * @param duplicateIds list of duplicate ids
     */
    public UpsertRecordDTO(UpsertAction type, EtalonRecord etalon, List<String> duplicateIds) {
        super();
        this.etalon = etalon;
        this.action = type;
        this.duplicateIds = duplicateIds;
    }

    /**
     * @return the action
     */
    public UpsertAction getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(UpsertAction action) {
        this.action = action;
    }

    /**
     * @return the duplicateIds
     */
    public List<String> getDuplicateIds() {
        if (duplicateIds == null) {
            duplicateIds = new ArrayList<>();
        }

        return duplicateIds;
    }

    /**
     * @return true, if result is a golden record
     */
    public boolean isEtalon() {
        return etalon != null;
    }
    /**
     * @return the golden record
     */
    @Override
    public EtalonRecord getEtalon() {
        return etalon;
    }
    /**
     * @param etalon the golden to set
     */
    public void setEtalon(EtalonRecord etalon) {
        this.etalon = etalon;
    }

    /**
     * @return the periods
     */
    public Collection<EtalonRecord> getPeriods() {
        return periods;
    }
    /**
     * @param periods the periods to set
     */
    public void setPeriods(Collection<EtalonRecord> periods) {
        this.periods = periods;
    }
    /**
     * @return the keys
     */
    @Override
    public RecordKeys getRecordKeys() {
        return recordKeys;
    }
    /**
     * @param keys the keys to set
     */
    public void setRecordKeys(RecordKeys keys) {
        this.recordKeys = keys;
    }

    public List<ErrorInfoDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfoDTO> errors) {
        this.errors = errors;
    }
}

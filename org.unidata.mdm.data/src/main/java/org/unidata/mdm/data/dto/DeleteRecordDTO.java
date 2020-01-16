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

import java.util.List;
import java.util.Objects;

import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;

/**
 * @author Mikhail Mikhailov
 */
public class DeleteRecordDTO implements RecordDTO, PipelineOutput {
    /**
     * Record keys for short upsert.
     */
    private RecordKeys recordKeys;

    private List<ErrorInfoDTO> errors;
    /**
     * Constructor for failures.
     */
    public DeleteRecordDTO() {
        super();
    }
    /**
     * Constructor for keys.
     */
    public DeleteRecordDTO(RecordKeys recordKeys) {
        super();
        this.recordKeys = recordKeys;
    }
    /**
     * @return the originKey
     */
    public RecordOriginKey getOriginKey() {
        return Objects.nonNull(recordKeys) ? recordKeys.getOriginKey() : null;
    }
    /**
     * @return the goldenKey
     */
    public RecordEtalonKey getEtalonKey() {
        return Objects.nonNull(recordKeys) ? recordKeys.getEtalonKey() : null;
    }
    /**
     * @return the golden
     */
    public boolean wasSuccess() {
        return Objects.nonNull(recordKeys);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys getRecordKeys() {
        return recordKeys;
    }
    /**
     * @param recordKeys the recordKeys to set
     */
    public void setRecordKeys(RecordKeys recordKeys) {
        this.recordKeys = recordKeys;
    }

    public List<ErrorInfoDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfoDTO> errors) {
        this.errors = errors;
    }
}

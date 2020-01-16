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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.system.type.pipeline.PipelineOutput;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragment;

/**
 * The all-in-one bulk ops result transfer object.
 * @author Dmitry Kopin on 14.02.2019.
 */
public class RecordsBulkResultDTO extends AbstractBulkResultDTO implements PipelineOutput, OutputFragment<RecordsBulkResultDTO> {
    /**
     * The id.
     */
    public static final FragmentId<RecordsBulkResultDTO> ID = new FragmentId<>("RECORDS_BULK_RESULT", RecordsBulkResultDTO::new);
    /**
     * Upserted info.
     */
    private List<UpsertRecordDTO> upsertRecords;
    /**
     * Deleted info.
     */
    private List<DeleteRecordDTO> deleteRecords;
    /**
     * Gets upserted.
     * @return upserted
     */
    public List<UpsertRecordDTO> getUpsertRecords() {
        return Objects.isNull(upsertRecords) ? Collections.emptyList() : upsertRecords;
    }
    /**
     * Set upserted.
     * @param records
     */
    public void setUpsertRecords(List<UpsertRecordDTO> records) {
        this.upsertRecords = records;
    }
    /**
     * @return the deleteRecords
     */
    public List<DeleteRecordDTO> getDeleteRecords() {
        return Objects.isNull(deleteRecords) ? Collections.emptyList() : deleteRecords;
    }
    /**
     * @param deleteRecords the deleteRecords to set
     */
    public void setDeleteRecords(List<DeleteRecordDTO> deleteRecords) {
        this.deleteRecords = deleteRecords;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentId<RecordsBulkResultDTO> fragmentId() {
        return ID;
    }
}

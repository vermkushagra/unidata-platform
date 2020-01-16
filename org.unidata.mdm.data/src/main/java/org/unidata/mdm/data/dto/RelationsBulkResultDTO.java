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
public class RelationsBulkResultDTO extends AbstractBulkResultDTO implements PipelineOutput, OutputFragment<RelationsBulkResultDTO> {
    /**
     * The id.
     */
    public static final FragmentId<RelationsBulkResultDTO> ID = new FragmentId<>("RELATIONS_BULK_RESULT", RelationsBulkResultDTO::new);
    /**
     * Upserted rels info.
     */
    private List<UpsertRelationsDTO> upsertRelations;
    /**
     * Deleted rels info.
     */
    private List<DeleteRelationsDTO> deleteRelations;
    /**
     * Gets rels upserts.
     * @return upserts
     */
    public List<UpsertRelationsDTO> getUpsertRelations() {
        return Objects.isNull(upsertRelations) ? Collections.emptyList() : upsertRelations;
    }
    /**
     * Sets rels upserts.
     * @param relations
     */
    public void setUpsertRelations(List<UpsertRelationsDTO> relations) {
        this.upsertRelations = relations;
    }
    /**
     * Gets rel deletes.
     * @return deletes
     */
    public List<DeleteRelationsDTO> getDeleteRelations() {
        return Objects.isNull(deleteRelations) ? Collections.emptyList() : deleteRelations;
    }
    /**
     * Sets rels deletes.
     * @param deleteRelations
     */
    public void setDeleteRelations(List<DeleteRelationsDTO> deleteRelations) {
        this.deleteRelations = deleteRelations;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentId<RelationsBulkResultDTO> fragmentId() {
        return ID;
    }
}

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

/**
 *
 */
package org.unidata.mdm.data.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidata.mdm.system.dto.ExecutionResult;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragment;

/**
 * @author Mikhail Mikhailov
 *
 */
public class UpsertRelationsDTO implements RelationsDTO<UpsertRelationDTO>, OutputFragment<UpsertRelationsDTO>, PipelineOutput, ExecutionResult {
    /**
     * This fragment ID.
     */
    public static final FragmentId<UpsertRelationsDTO> ID
        = new FragmentId<>("UPSERT_RELATIONS_RESULT", UpsertRelationsDTO::new);
    /**
     * Relations upsert result.
     */
    private final Map<RelationStateDTO, List<UpsertRelationDTO>> relations;
    /**
     * Constructor.
     */
    public UpsertRelationsDTO(Map<RelationStateDTO, List<UpsertRelationDTO>> relations) {
        super();
        this.relations = relations;
    }
    /**
     * Constructor.
     */
    public UpsertRelationsDTO() {
        super();
        this.relations = new HashMap<>();
    }
    /**
     * @return the relations
     */
    @Override
    public Map<RelationStateDTO, List<UpsertRelationDTO>> getRelations() {
        return relations;
    }

    @Override
    public FragmentId<UpsertRelationsDTO> fragmentId() {
        return ID;
    }
}

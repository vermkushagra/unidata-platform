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

import java.util.List;
import java.util.Map;

import org.unidata.mdm.system.dto.ExecutionResult;
import org.unidata.mdm.system.type.pipeline.PipelineOutput;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragment;

/**
 * @author Mikhail Mikhailov
 * Mass delete result DTO.
 */
public class DeleteRelationsDTO implements RelationsDTO<DeleteRelationDTO>, OutputFragment<DeleteRelationsDTO>, PipelineOutput, ExecutionResult {
    /**
     * This fragment ID.
     */
    public static final FragmentId<DeleteRelationsDTO> ID
        = new FragmentId<>("DELETE_RELATIONS_RESULT", DeleteRelationsDTO::new);
    /**
     * Deleted relations.
     */
    private Map<RelationStateDTO, List<DeleteRelationDTO>> relations;
    /**
     * Constructor.
     */
    public DeleteRelationsDTO() {
        super();
    }
    /**
     * Constructor.
     */
    public DeleteRelationsDTO(Map<RelationStateDTO, List<DeleteRelationDTO>> relations) {
        this();
        this.relations = relations;
    }

    /**
     * @return the deleted
     */
    @Override
    public Map<RelationStateDTO, List<DeleteRelationDTO>> getRelations() {
        return relations;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public FragmentId<DeleteRelationsDTO> fragmentId() {
        return ID;
    }
}

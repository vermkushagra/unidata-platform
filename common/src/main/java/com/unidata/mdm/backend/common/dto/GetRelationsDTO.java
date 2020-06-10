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
package com.unidata.mdm.backend.common.dto;

import java.util.List;
import java.util.Map;

/**
 * @author Mikhail Mikhailov
 * Top level relations DTO.
 */
public class GetRelationsDTO implements RelationsDTO<GetRelationDTO> {
    /**
     * The relations.
     */
    private final Map<RelationStateDTO, List<GetRelationDTO>> relations;
    /**
     * Constructor.
     */
    public GetRelationsDTO(Map<RelationStateDTO, List<GetRelationDTO>> relations) {
        super();
        this.relations = relations;
    }
    /**
     * @return the relations
     */
    @Override
    public Map<RelationStateDTO, List<GetRelationDTO>> getRelations() {
        return relations;
    }
}

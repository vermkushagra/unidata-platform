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

package com.unidata.mdm.backend.common.dto;

import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.RelationType;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Delete relation DTO.
 */
public class DeleteRelationDTO implements RelationDTO {
    /**
     * The keys.
     */
    private RelationKeys relationKeys;
    /**
     * Relation name.
     */
    private final String relName;
    /**
     * Relation type.
     */
    private final RelationType type;
    /**
     * list of errors
     */
    private List<ErrorInfoDTO> errors;

    /**
     * Constructor.
     */
    public DeleteRelationDTO(RelationKeys relationKeys, String relName, RelationType relType) {
        super();
        this.relationKeys = relationKeys;
        this.relName = relName;
        this.type = relType;
    }

    /**
     * @return the relationKeys
     */
    @Override
    public RelationKeys getRelationKeys() {
        return relationKeys;
    }

    /**
     * @param relationKeys the relationKeys to set
     */
    public void setRelationKeys(RelationKeys relationKeys) {
        this.relationKeys = relationKeys;
    }

    /**
     * @return the relName
     */
    public String getRelName() {
        return relName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationType getRelationType() {
        return type;
    }

    /**
     * list of errors
     */
    public List<ErrorInfoDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorInfoDTO> errors) {
        this.errors = errors;
    }
}

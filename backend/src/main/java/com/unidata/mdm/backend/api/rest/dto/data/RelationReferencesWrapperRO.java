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

package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.List;

/**
 * Wrapper object for references relation (only diff)
 *
 * @author Dmitry Kopin on 19.06.2018.
 */
public class RelationReferencesWrapperRO {
    /**
     * records for update
     */
    private List<EtalonRelationToRO> toUpdate;
    /**
     * records for delete
     */
    private List<RelationDeleteWrapperRO> toDelete;

    public List<EtalonRelationToRO> getToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(List<EtalonRelationToRO> toUpdate) {
        this.toUpdate = toUpdate;
    }

    public List<RelationDeleteWrapperRO> getToDelete() {
        return toDelete;
    }

    public void setToDelete(List<RelationDeleteWrapperRO> toDelete) {
        this.toDelete = toDelete;
    }
}

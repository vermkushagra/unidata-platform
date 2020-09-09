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

package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.Collection;

public class FilledEntityGroupNode extends EntityGroupNode {

    private Collection<GroupEntityDefinition> lookupEntities;

    private Collection<GroupEntityDefinition> entities;

    public Collection<GroupEntityDefinition> getLookupEntities() {
        return lookupEntities;
    }

    public FilledEntityGroupNode(String title, String groupName, Collection<GroupEntityDefinition> lookupEntities, Collection<GroupEntityDefinition> entities) {
        super(title, groupName);
        this.lookupEntities = lookupEntities;
        this.entities = entities;
    }

    public FilledEntityGroupNode() {
    }

    public void setLookupEntities(Collection<GroupEntityDefinition> lookupEntities) {
        this.lookupEntities = lookupEntities;
    }

    public Collection<GroupEntityDefinition> getEntities() {
        return entities;
    }

    public void setEntities(Collection<GroupEntityDefinition> entities) {
        this.entities = entities;
    }
}

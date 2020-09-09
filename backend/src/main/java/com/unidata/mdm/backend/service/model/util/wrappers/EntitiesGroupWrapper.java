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

package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Collection;
import java.util.HashMap;

import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

public class EntitiesGroupWrapper extends ModelWrapper {

    private final EntitiesGroupDef entitiesGroupDef;

    private final String wrapperId;

    private final HashMap<String, EntityDef> nestedEntities = new HashMap<>();

    private final HashMap<String, LookupEntityDef> nestedLookupEntities = new HashMap<>();

    public EntitiesGroupWrapper(EntitiesGroupDef entitiesGroupDef, String wrapperId) {
        this.entitiesGroupDef = entitiesGroupDef;
        this.wrapperId = wrapperId;
    }

    @Override
    public String getUniqueIdentifier() {
        return entitiesGroupDef.getGroupName();
    }

    @Override
    public Long getVersionOfWrappedElement() {
        return entitiesGroupDef.getVersion();
    }

    public void addLookupEntityToGroup(LookupEntityDef lookupEntityDef) {
        nestedLookupEntities.put(lookupEntityDef.getName(), lookupEntityDef);
    }

    public void addEntityToGroup(EntityDef entity) {
        nestedEntities.put(entity.getName(), entity);
    }

    public Collection<EntityDef> getNestedEntites() {
        return nestedEntities.values();
    }

    public Collection<LookupEntityDef> getNestedLookupEntities() {
        return nestedLookupEntities.values();
    }

    public boolean removeEntity(String entityName) {
        return nestedEntities.remove(entityName) != null;
    }

    public boolean removeLookupEntity(String lookupEntityName) {
        return nestedLookupEntities.remove(lookupEntityName) != null;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public EntitiesGroupDef getEntitiesGroupDef() {
        return entitiesGroupDef;
    }
}

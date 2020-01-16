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

package org.unidata.mdm.meta.type.info.impl;

import java.util.Collection;
import java.util.HashMap;

import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.LookupEntityDef;

public class EntitiesGroupWrapper implements IdentityModelElement {

    private final EntitiesGroupDef entitiesGroupDef;

    private final String wrapperId;

    private final HashMap<String, EntityDef> nestedEntities = new HashMap<>();

    private final HashMap<String, LookupEntityDef> nestedLookupEntities = new HashMap<>();

    public EntitiesGroupWrapper(EntitiesGroupDef entitiesGroupDef, String wrapperId) {
        this.entitiesGroupDef = entitiesGroupDef;
        this.wrapperId = wrapperId;
    }

    @Override
    public String getId() {
        return entitiesGroupDef.getGroupName();
    }

    @Override
    public Long getVersion() {
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

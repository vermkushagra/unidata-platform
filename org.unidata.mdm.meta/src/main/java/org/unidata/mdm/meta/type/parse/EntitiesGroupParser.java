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

package org.unidata.mdm.meta.type.parse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.Model;
import org.unidata.mdm.meta.service.impl.facades.EntitiesGroupModelElementFacade;
import org.unidata.mdm.meta.type.info.impl.EntitiesGroupWrapper;

public class EntitiesGroupParser implements ModelParser<EntitiesGroupWrapper> {

    @Override
    public Map<String, EntitiesGroupWrapper> parse(Model model) {
        EntitiesGroupDef rootGroup = model.getEntitiesGroup();
        if (rootGroup == null) {
            rootGroup = EntitiesGroupModelElementFacade.DEFAULT_ROOT_GROUP;
        }

        Map<String, EntitiesGroupWrapper> groups = recursiveParse(rootGroup.getInnerGroups(), rootGroup.getGroupName());
        EntitiesGroupWrapper rootWrapper = new EntitiesGroupWrapper(rootGroup, rootGroup.getGroupName());
        groups.put(rootGroup.getGroupName(), rootWrapper);

        model.getEntities().stream()
                .filter(entity -> entity.getGroupName() != null && groups.get(entity.getGroupName()) != null)
                .forEach(entity -> {
            EntitiesGroupWrapper wrapper = groups.get(entity.getGroupName());
            wrapper.addEntityToGroup(entity);
        });

        model.getLookupEntities().stream()
                .filter(entity -> entity.getGroupName() != null && groups.get(entity.getGroupName()) != null)
                .forEach(entity -> {
            EntitiesGroupWrapper wrapper = groups.get(entity.getGroupName());
            wrapper.addLookupEntityToGroup(entity);
        });
        return groups;
    }

    private Map<String, EntitiesGroupWrapper> recursiveParse(List<EntitiesGroupDef> groups, String parentPath) {
        Map<String, EntitiesGroupWrapper> result = new ConcurrentHashMap<>();
        if (groups.isEmpty()) return result;
        for (EntitiesGroupDef entitiesGroup : groups) {
            String wrapperId = EntitiesGroupModelElementFacade.getFullPath(parentPath, entitiesGroup.getGroupName());
            EntitiesGroupWrapper wrapper = new EntitiesGroupWrapper(entitiesGroup, wrapperId);
            result.put(wrapperId, wrapper);
            result.putAll(recursiveParse(entitiesGroup.getInnerGroups(), wrapperId));
        }
        return result;
    }

    @Override
    public Class<EntitiesGroupWrapper> getValueType() {
        return EntitiesGroupWrapper.class;
    }
}

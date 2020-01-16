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

package org.unidata.mdm.meta.service.impl.facades;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.service.impl.ModelCache;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.info.impl.EntitiesGroupWrapper;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.meta.type.info.impl.LookupInfoHolder;
import org.unidata.mdm.meta.util.MetaJaxbUtils;

@Component
public class EntitiesGroupModelElementFacade extends AbstractModelElementFacade<EntitiesGroupWrapper, EntitiesGroupDef> {

    public static final EntitiesGroupDef DEFAULT_ROOT_GROUP = new EntitiesGroupDef()
            .withGroupName("ROOT")
            .withTitle("ПоменяйтеИмяДефолтнойГруппы")
            .withVersion(0L);

    private static final String GROUP_SEPARATOR = ".";
    private static final String ESCAPED_GROUP_SEPARATOR = "\\.";

    @Nonnull
    public static String getFullPath(@Nonnull String parentPath, @Nonnull String groupName) {
        if (StringUtils.isBlank(parentPath)) {
            return groupName;
        } else {
            return parentPath + GROUP_SEPARATOR + groupName;
        }
    }

    @Nonnull
    public static String[] getSplitPath(@Nonnull String fullPath) {
        return fullPath.split(ESCAPED_GROUP_SEPARATOR);
    }

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.ENTITIES_GROUP;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull EntitiesGroupDef modelElement) {
        return modelElement.getGroupName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull EntitiesGroupDef modelElement) {
        return MetaJaxbUtils.marshalEntitiesGroup(modelElement);
    }

    @Nullable
    @Override
    public EntitiesGroupWrapper convertToWrapper(@Nonnull EntitiesGroupDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        return new EntitiesGroupWrapper(modelElement, modelElement.getGroupName());
    }

    @Override
    public void verifyModelElement(EntitiesGroupDef modelElement) {
        super.verifyModelElement(modelElement);
        modelElement.getInnerGroups().forEach(this::verifyModelElement);
    }

    @Override
    public void changeCacheBeforeUpdate(@Nonnull EntitiesGroupDef modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache) {
        super.changeCacheBeforeUpdate(modelElement, ctx, modelCache);
        modelCache.getCache().get(EntitiesGroupWrapper.class).clear();
    }

    @Override
    public void changeCacheAfterUpdate(@Nonnull EntitiesGroupDef modelElement, @Nonnull UpdateModelRequestContext ctx, @Nonnull ModelCache modelCache) {
        super.changeCacheAfterUpdate(modelElement, ctx, modelCache);
        EntitiesGroupWrapper cachedModelElement = (EntitiesGroupWrapper) modelCache.getCache().get(EntitiesGroupWrapper.class).get(getModelElementId(modelElement));
        Map<String, EntitiesGroupWrapper> flatGroups = recursiveParse(cachedModelElement.getEntitiesGroupDef().getInnerGroups(), cachedModelElement.getEntitiesGroupDef().getGroupName());
        flatGroups.put(modelElement.getGroupName(), cachedModelElement);

        modelCache.getCache().get(EntityInfoHolder.class).values().stream()
                .map(wrapper -> (EntityInfoHolder) wrapper)
                .map(EntityInfoHolder::getEntity)
                .forEach(entity -> {
                            if (entity.getGroupName() != null) {
                                EntitiesGroupWrapper wrapper = flatGroups.get(entity.getGroupName());
                                wrapper.addEntityToGroup(entity);
                            } else {
                                cachedModelElement.addEntityToGroup(entity);
                            }
                        }
                );

        modelCache.getCache().get(LookupInfoHolder.class).values().stream()
                .map(wrapper -> (LookupInfoHolder) wrapper)
                .map(LookupInfoHolder::getEntity)
                .forEach(entity -> {
                            if (entity.getGroupName() != null) {
                                EntitiesGroupWrapper wrapper = flatGroups.get(entity.getGroupName());
                                wrapper.addLookupEntityToGroup(entity);
                            } else {
                                cachedModelElement.addLookupEntityToGroup(entity);
                            }
                        }
                );

        modelCache.getCache().put(EntitiesGroupWrapper.class, flatGroups);
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
}

package com.unidata.mdm.backend.service.model.util.facades;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.ModelCache;
import com.unidata.mdm.backend.service.model.util.wrappers.EntitiesGroupWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.EntitiesGroupDef;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class EntitiesGroupModelElementFacade extends AbstractModelElementFacade<EntitiesGroupWrapper, EntitiesGroupDef> {
    public static final EntitiesGroupDef DEFAULT_ROOT_GROUP = new EntitiesGroupDef().withGroupName("ROOT").withTitle("ПоменяйтеИмяДефолтнойГруппы").withVersion(0L);

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
        return JaxbUtils.marshalEntitiesGroup(modelElement);
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

        modelCache.getCache().get(EntityWrapper.class).values().stream()
                .map(wrapper -> (EntityWrapper) wrapper)
                .map(EntityWrapper::getEntity)
                .forEach(entity -> {
                            if (entity.getGroupName() != null) {
                                EntitiesGroupWrapper wrapper = flatGroups.get(entity.getGroupName());
                                wrapper.addEntityToGroup(entity);
                            } else {
                                cachedModelElement.addEntityToGroup(entity);
                            }
                        }
                );

        modelCache.getCache().get(LookupEntityWrapper.class).values().stream()
                .map(wrapper -> (LookupEntityWrapper) wrapper)
                .map(LookupEntityWrapper::getEntity)
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

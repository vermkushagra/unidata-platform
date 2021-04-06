package com.unidata.mdm.backend.service.model.util.parsers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.service.model.util.facades.EntitiesGroupModelElementFacade;
import com.unidata.mdm.backend.service.model.util.wrappers.EntitiesGroupWrapper;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.Model;

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

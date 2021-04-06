package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.api.rest.dto.meta.EntityGroupNode;
import com.unidata.mdm.backend.api.rest.dto.meta.FlatGroupMapping;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.model.util.facades.EntitiesGroupModelElementFacade;
import com.unidata.mdm.meta.EntitiesGroupDef;

public class FlatGroupMappingToEntitiesGroupDefConverter {

    public static EntitiesGroupDef convert(FlatGroupMapping flatGroupMapping) {
        Map<String, Collection<EntitiesGroupDef>> flatMap = new HashMap<>();
        Collection<? extends EntityGroupNode> entityGroupNodes = flatGroupMapping.getGroupNodes();
        for (EntityGroupNode groupNode : entityGroupNodes) {
            String[] splitPath = EntitiesGroupModelElementFacade.getSplitPath(groupNode.getGroupName());
            String groupName = splitPath[splitPath.length - 1];
            if (Objects.isNull(groupName) || Objects.isNull(groupNode.getTitle())) {
                throw new SystemRuntimeException("Group doesn't contain name or title" , ExceptionId.EX_META_GROUP_NAME_OR_TITLE_ABSENT);
            }
            EntitiesGroupDef groupDef = new EntitiesGroupDef().withGroupName(groupName).withTitle(groupNode.getTitle());
            String mapKey = splitPath.length == 1 ? null : groupNode.getGroupName().substring(0, groupNode.getGroupName().length() - groupName.length() - 1);
            Collection<EntitiesGroupDef> groupDefs = flatMap.get(mapKey);
            if (groupDefs == null) {
                groupDefs = new ArrayList<>();
                flatMap.put(mapKey, groupDefs);
            }
            groupDefs.add(groupDef);
        }
        assemble(flatMap.get(null), flatMap, StringUtils.EMPTY);
        return flatMap.get(null).iterator().next();
    }

    private static void assemble(Collection<EntitiesGroupDef> groupDefs, Map<String, Collection<EntitiesGroupDef>> flatMap, String parentPath) {
        for (EntitiesGroupDef groupDef : groupDefs) {
            Collection<EntitiesGroupDef> innerGroups = flatMap.getOrDefault(EntitiesGroupModelElementFacade.getFullPath(parentPath, groupDef.getGroupName()), new ArrayList<>());
            groupDef.withInnerGroups(innerGroups);
            assemble(innerGroups, flatMap, groupDef.getGroupName());
        }
    }

}

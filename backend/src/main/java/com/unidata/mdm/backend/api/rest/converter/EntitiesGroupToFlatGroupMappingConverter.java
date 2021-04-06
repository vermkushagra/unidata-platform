package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.meta.EntityGroupNode;
import com.unidata.mdm.backend.api.rest.dto.meta.FilledEntityGroupNode;
import com.unidata.mdm.backend.api.rest.dto.meta.FlatGroupMapping;
import com.unidata.mdm.backend.api.rest.dto.meta.GroupEntityDefinition;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesGroupsDTO;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

public class EntitiesGroupToFlatGroupMappingConverter {

    private static final Comparator<EntityGroupNode> groupComparator = (e1, e2) -> e1.getTitle().toLowerCase().compareTo(e2.getTitle().toLowerCase());

    private static final Comparator<GroupEntityDefinition> definitionComparator = (e1, e2) -> e1.getDisplayName().toLowerCase().compareTo(e2.getDisplayName().toLowerCase());

    public static FlatGroupMapping convertToFlatGroup(GetEntitiesGroupsDTO groupDefs) {
        Collection<EntityGroupNode> groupNodes = groupDefs.getGroups().entrySet().stream()
                .map(group -> new EntityGroupNode(
                     group.getValue().getTitle(),
                     group.getKey()))
                .sorted(groupComparator)
                .collect(Collectors.toList());
        return new FlatGroupMapping(groupNodes);
    }

    public static FlatGroupMapping convertToFullFilledFlatGroup(GetEntitiesGroupsDTO groupDefs) {

        List<FilledEntityGroupNode> groupNodes = new ArrayList<>();
        for (Entry<String, EntitiesGroupDef> wrapper : groupDefs.getGroups().entrySet()) {
            FilledEntityGroupNode filledEntityGroupNode = new FilledEntityGroupNode();
            filledEntityGroupNode.setGroupName(wrapper.getKey());
            filledEntityGroupNode.setTitle(wrapper.getValue().getTitle());

            List<EntityDef> nestedEntities = groupDefs.getNestedEntities(wrapper.getKey());
            Collection<GroupEntityDefinition> entityDefinitions = nestedEntities.stream()
                    .map(entityDef -> new GroupEntityDefinition(entityDef.getName(), entityDef.getDisplayName(), entityDef.isDashboardVisible()))
                    .sorted(definitionComparator)
                    .collect(Collectors.toList());

            List<LookupEntityDef> nestedLookups = groupDefs.getNestedLookupEntities(wrapper.getKey());
            Collection<GroupEntityDefinition> lookupEntityDefinitions = nestedLookups.stream()
                    .map(entityDef -> new GroupEntityDefinition(entityDef.getName(), entityDef.getDisplayName(), entityDef.isDashboardVisible()))
                    .sorted(definitionComparator)
                    .collect(Collectors.toList());
            filledEntityGroupNode.setEntities(entityDefinitions);
            filledEntityGroupNode.setLookupEntities(lookupEntityDefinitions);
            groupNodes.add(filledEntityGroupNode);
        }
        Collections.sort(groupNodes, groupComparator);
        return new FlatGroupMapping(groupNodes);
    }
}

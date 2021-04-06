/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.api.rest.dto.meta.EntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.RelationDefinition;
import com.unidata.mdm.backend.common.dto.data.model.GetEntitiesByRelationSideDTO;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 *
 */
public class EntitiesDefFilteredByRelationSideConverter {

    /**
     * Constructor.
     */
    private EntitiesDefFilteredByRelationSideConverter() {
        super();
    }

    /**
     * Converter method.
     * @param dto the dto to convert
     * @return list of entity definitions
     */
    public static List<EntityDefinition> convert(GetEntitiesByRelationSideDTO dto) {

        if (dto == null || dto.getEntities() == null || dto.getEntities().isEmpty()) {
            return Collections.emptyList();
        }

        List<EntityDefinition> result = new ArrayList<>();
        for (Entry<EntityDef, Pair<List<NestedEntityDef>, List<RelationDef>>> e
                : dto.getEntities().entrySet()) {

            EntityDef source = e.getKey();
            EntityDefinition target = new EntityDefinition();
            List<NestedEntityDef> refs = e.getValue().getLeft();

            EntityDefinitionConverter.copyAbstractEntityData(source, target);
            target.getSimpleAttributes().addAll(
                    EntityDefinitionConverter.to(source.getSimpleAttribute(), source.getName()));
            target.getArrayAttributes().addAll(
                    EntityDefinitionConverter.to(source.getArrayAttribute(), source.getName(), true));
            target.getComplexAttributes().addAll(
                    EntityDefinitionConverter.to(source.getComplexAttribute(), refs, source.getName()));

            target.setDashboardVisible(source.isDashboardVisible());
            target.setMergeSettings(MergeSettingsConverter.to(source.getMergeSettings()));

            List<RelationDefinition> relations = new ArrayList<>();
            for (RelationDef relationDef : e.getValue().getRight()) {
                relations.add(RelationDefConverter.convert(relationDef));
            }

            target.setRelations(relations);

            result.add(target);
        }

        return result;
    }
}

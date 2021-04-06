package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.meta.RelType;
import com.unidata.mdm.backend.api.rest.dto.meta.RelationDefinition;
import com.unidata.mdm.meta.RelationDef;

public class RelationDefConvertor extends AbstractEntityDefinitionConvertor {

    public static RelationDefinition convert(RelationDef source) {
        if (source == null) {
            return null;
        }
        RelationDefinition target = new RelationDefinition();
        target.setFromEntity(source.getFromEntity());
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setRelType(RelType.fromValue(source.getRelType().value()));
        target.setRequired(source.isRequired());
        target.setToEntity(source.getToEntity());
        target.setToEntityDefaultDisplayAttributes(source.getToEntityDefaultDisplayAttributes());
        target.setUseAttributeNameForDisplay(source.isUseAttributeNameForDisplay());
        target.setSimpleAttributes(to(source.getSimpleAttribute(), target.getName()));
        target.setExternalIdGenerationStrategy(ExternalIdGenerationStrategyConverter.to(source.getExternalIdGenerationStrategy()));
        return target;
    }

    public static List<RelationDefinition> convert(List<RelationDef> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<RelationDefinition> result = new ArrayList<>();
        for (RelationDef def : source) {
            if (Objects.isNull(def)) {
                continue;
            }

            result.add(convert(def));
        }

        return result;
    }
}

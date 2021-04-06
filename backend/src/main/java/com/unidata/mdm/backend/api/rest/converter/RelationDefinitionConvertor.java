package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.meta.RelationDefinition;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;

public class RelationDefinitionConvertor {

    public static RelationDef convert(RelationDefinition source) {
        if (source == null) {
            return null;
        }

        RelationDef target = new RelationDef();
        target.setFromEntity(source.getFromEntity());
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setRelType(RelType.fromValue(source.getRelType().value()));
        target.setRequired(source.isRequired());
        target.setToEntity(source.getToEntity());
        target.setUseAttributeNameForDisplay(source.isUseAttributeNameForDisplay());
        target.setExternalIdGenerationStrategy(ExternalIdGenerationStrategyConverter.from(source.getExternalIdGenerationStrategy()));

        if(CollectionUtils.isNotEmpty(source.getToEntityDefaultDisplayAttributes())){
            target.getToEntityDefaultDisplayAttributes().addAll(source.getToEntityDefaultDisplayAttributes());
        }

        List<SimpleAttributeDef> attrs = new ArrayList<>();
        SimpleAttributeDefConverter.copySimpleAttributeDataList(source.getSimpleAttributes(), attrs);

        target.withSimpleAttribute(attrs);
        return target;
    }

    public static List<RelationDef> convert(List<RelationDefinition> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<RelationDef> result = new ArrayList<>();
        for (RelationDefinition def : source) {
            if (Objects.isNull(def)) {
                continue;
            }

            result.add(convert(def));
        }

        return result;
    }
}

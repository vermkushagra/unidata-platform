package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.meta.EntityInfoDefinition;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.AbstractEntityDef;

/**
 * @author Michael Yashin. Created on 26.05.2015.
 */
@ConverterQualifier
@Component
public class AbstractEntityDefToEntityListElementConverter implements Converter<AbstractEntityDef, EntityInfoDefinition> {

    public static<T extends AbstractEntityDef> List<EntityInfoDefinition> to(List<T> source) {

        if (CollectionUtils.isEmpty(source)) {
            Collections.emptyList();
        }

        List<EntityInfoDefinition> result = new ArrayList<>();
        for (AbstractEntityDef a : source) {
            result.add(to(a));
        }

        return result;
    }

    public static EntityInfoDefinition to(AbstractEntityDef source) {

        if (Objects.isNull(source)) {
            return null;
        }

        EntityInfoDefinition element = new EntityInfoDefinition();
        element.setName(source.getName());
        element.setDisplayName(source.getDisplayName());
        element.setDescription(source.getDescription());

        return element;
    }

    @Override
    public EntityInfoDefinition convert(AbstractEntityDef source) {
        return to(source);
    }
}

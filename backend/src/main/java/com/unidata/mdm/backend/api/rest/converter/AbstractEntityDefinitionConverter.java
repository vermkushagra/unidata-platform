package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.ArrayDataType;
import com.unidata.mdm.backend.api.rest.dto.SimpleDataType;
import com.unidata.mdm.backend.api.rest.dto.meta.AbstractAttributeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.AbstractEntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.ArrayAttributeDefinitionRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ComplexAttributeDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.NestedEntityDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.SimpleAttributeDefinition;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;
import com.unidata.mdm.backend.common.integration.auth.Right;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.CustomPropertyDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.SimpleAttributeDef;

/**
 * @author Mikhail Mikhailov
 * Abstract entity converter.
 */
public abstract class AbstractEntityDefinitionConverter {

    /**
     * Constructor.
     */
    protected AbstractEntityDefinitionConverter() {
        super();
    }

    /**
     * Copy abstract entity data from internal to REST.
     *
     * @param source
     *            internal
     * @param target
     *            REST
     */
    public static void copyAbstractEntityData(AbstractEntityDef source, AbstractEntityDefinition target) {
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());
        target.setCustomProperties(to(source.getCustomProperties()));
    }

    /**
     * Copy abstract attribute data from internal to REST.
     *
     * @param source
     *            internal
     * @param target
     *            REST
     */
    public static void copyAbstractAttributeData(AbstractAttributeDef source, AbstractAttributeDefinition target, String securityPath) {

        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setDescription(source.getDescription());

        // 1. Check for data admin object first
        Right adminRights = SecurityUtils.getRightsForResource(SecurityUtils.ADMIN_DATA_MANAGEMENT_RESOURCE_NAME);
        Right resourceRights = SecurityUtils.getRightsForResource(String.join(".", securityPath, source.getName()));

        target.setRights(RolesConverter.convertRightDTO(resourceRights));
        if (SecurityUtils.isAdminUser() || adminRights != null || resourceRights == null) {
            target.setHidden(source.isHidden());
            target.setReadOnly(source.isReadOnly());
        } else {
            target.setHidden(!resourceRights.isRead());
            target.setReadOnly(!resourceRights.isCreate() || !resourceRights.isDelete() || !resourceRights.isUpdate());
        }

        target.setCustomProperties(to(source.getCustomProperties()));
    }

    /**
     * Copies simple attributes from list to list.
     *
     * @param source
     *            the source
     */
    public static List<SimpleAttributeDefinition> to(List<SimpleAttributeDef> source, String securityPath) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<SimpleAttributeDefinition> target = new ArrayList<>();
        for (SimpleAttributeDef sourceAttr : source) {
            target.add(to(sourceAttr, securityPath));
        }

        return target;
    }

    /**
     * Copy simple attributes data from REST to internal.
     *
     * @param source
     *            REST source
     */
    public static SimpleAttributeDefinition to(SimpleAttributeDef source, String securityPath) {

        SimpleAttributeDefinition target =  new SimpleAttributeDefinition();

        copyAbstractAttributeData(source, target, securityPath);

        target.setMask(source.getMask());
        target.setEnumDataType(source.getEnumDataType());
        target.setLookupEntityType(source.getLookupEntityType());
        target.setLookupEntityCodeAttributeType(getSimpleDataType(source.getLookupEntityCodeAttributeType()));
        target.setLookupEntityDisplayAttributes(new ArrayList<>(source.getLookupEntityDisplayAttributes()));
        target.setLinkDataType(source.getLinkDataType());
        target.setNullable(source.isNullable());
        target.setSimpleDataType(getSimpleDataType(source.getSimpleDataType()));

        target.setUnique(source.isUnique());
        if (source.getOrder() != null) {
            target.setOrder(source.getOrder().intValue());
        }

        target.setSearchable(source.isSearchable());
        target.setSearchMorphologically(source.isSearchMorphologically());
        target.setDisplayable(source.isDisplayable());
        target.setMainDisplayable(source.isMainDisplayable());
        if(source.getMeasureSettings() != null){
            target.setDefaultUnitId(source.getMeasureSettings().getDefaultUnitId());
            target.setValueId(source.getMeasureSettings().getValueId());
        }

        return target;
    }

    private static SimpleDataType getSimpleDataType(com.unidata.mdm.meta.SimpleDataType innerType) {
        if (innerType == null) {
            return null;
        }
        if (innerType == com.unidata.mdm.meta.SimpleDataType.MEASURED) {
            return SimpleDataType.NUMBER;
        } else {
            return SimpleDataType.fromValue(innerType.value());
        }
    }

    /**
     * Copies simple attributes from list to list.
     *
     * @param source
     *            the source
     */
    public static List<ArrayAttributeDefinitionRO> to(List<ArrayAttributeDef> source, String securityPath, boolean arrayFlag) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<ArrayAttributeDefinitionRO> target = new ArrayList<>();
        for (ArrayAttributeDef sourceAttr : source) {
            target.add(to(sourceAttr, securityPath));
        }

        return target;
    }

    /**
     * Copy simple attributes data from REST to internal.
     *
     * @param source
     *            REST source
     */
    public static ArrayAttributeDefinitionRO to(ArrayAttributeDef source, String securityPath) {

        ArrayAttributeDefinitionRO target =  new ArrayAttributeDefinitionRO();

        copyAbstractAttributeData(source, target, securityPath);

        target.setMask(source.getMask());
        target.setNullable(source.isNullable());
        target.setArrayDataType(getArrayValueType(source.getArrayValueType()));

        if (source.getOrder() != null) {
            target.setOrder(source.getOrder().intValue());
        }

        target.setSearchable(source.isSearchable());
        target.setSearchMorphologically(source.isSearchMorphologically());
        target.setLookupEntityType(source.getLookupEntityType());
        target.setLookupEntityCodeAttributeType(getArrayValueType(source.getLookupEntityCodeAttributeType()));
        target.setLookupEntityDisplayAttributes(new ArrayList<>(source.getLookupEntityDisplayAttributes()));
        target.setExchangeSeparator(source.getExchangeSeparator());

        return target;
    }

    private static ArrayDataType getArrayValueType(com.unidata.mdm.meta.ArrayValueType innerType) {

        if (innerType == null) {
            return null;
        }

        return ArrayDataType.fromValue(innerType.value().value());
    }

    /**
     * Copy list of internal complex attributes to REST target
     * @param source internal
     * @param refs the references
     */
    public static List<ComplexAttributeDefinition> to(List<ComplexAttributeDef> source, List<NestedEntityDef> refs,
            String securityPath) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<ComplexAttributeDefinition> target = new ArrayList<>();
        for (ComplexAttributeDef attr : source) {
            target.add(to(attr, refs, securityPath));
        }

        return target;
    }

    /**
     * Convert complex attributes.
     * @param source internal
     * @param refs the model
     */
    public static ComplexAttributeDefinition to(ComplexAttributeDef source, List<NestedEntityDef> refs,
            String securityPath) {

        ComplexAttributeDefinition result = new ComplexAttributeDefinition();

        copyAbstractAttributeData(source, result, securityPath);

        if (source.getMinCount() != null) {
            result.setMinCount(source.getMinCount().longValue());
        }
        if (source.getMaxCount() != null) {
            result.setMaxCount(source.getMaxCount().longValue());
        }
        if (source.getOrder() != null) {
            result.setOrder(source.getOrder().intValue());
        }
        result.setSubEntityKeyAttribute(source.getSubEntityKeyAttribute());

        if (StringUtils.isNotEmpty(source.getNestedEntityName())) {

            NestedEntityDef nestedEntity
                = refs.stream()
                    .filter(ent -> source.getNestedEntityName().equals(ent.getName()))
                    .findFirst()
                    .orElseGet(null);

            result.setNestedEntity(to(nestedEntity, refs, String.join(".", securityPath, source.getName())));
        }

        return result;
    }

    /**
     * Copy nested entity internal to REST.
     *
     * @param source
     *            internal
     * @param refs
     *            the references
     * @return REST
     */
    public static NestedEntityDefinition to(NestedEntityDef source, List<NestedEntityDef> refs,
            String securityPath) {

        NestedEntityDefinition result = new NestedEntityDefinition();

        copyAbstractEntityData(source, result);

        result.getSimpleAttributes().addAll(to(source.getSimpleAttribute(), securityPath));
        result.getArrayAttributes().addAll(to(source.getArrayAttribute(), securityPath, true));
        result.getComplexAttributes().addAll(to(source.getComplexAttribute(), refs, securityPath));

        return result;
    }

    public static List<CustomPropertyDefinition> to(List<CustomPropertyDef> customProperties) {
        return customProperties.stream().map(entityDefCustomProperty ->
                new CustomPropertyDefinition(entityDefCustomProperty.getName(), entityDefCustomProperty.getValue())
        ).collect(Collectors.toList());
    }
}

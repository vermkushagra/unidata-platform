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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.unidata.mdm.core.service.UPathService;
import org.unidata.mdm.core.type.measurement.MeasurementValue;
import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.AttributeMeasurementSettingsDef;
import org.unidata.mdm.meta.CustomPropertyDef;
import org.unidata.mdm.meta.SimpleAttributeDef;
import org.unidata.mdm.meta.SimpleDataType;
import org.unidata.mdm.meta.VersionedObjectDef;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.po.MetaModelPO;
import org.unidata.mdm.meta.service.MetaMeasurementService;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.service.impl.ModelCache;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.system.exception.PlatformBusinessException;

public abstract class AbstractModelElementFacade<W extends IdentityModelElement, V extends VersionedObjectDef>
    implements ModelElementElementFacade<W, V> {

    static final long INITIAL_VERSION = 1L;
    static final String DOT = ".";

    public static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-z][a-z0-9_-]*$", Pattern.CASE_INSENSITIVE);

    public static final List<String> RESERVED_NAMES = Arrays.asList("model", "audit", "classifier");

    @Autowired
    protected MetaModelService metaModelService;

    @Autowired
    private MetaMeasurementService metaMeasurementService;

    @Autowired
    protected UPathService upathService;

    @Nonnull
    public abstract ModelType getModelType();

    @SuppressWarnings("unchecked")
    private Class<W> getWrapperClass() {
        return (Class<W>) getModelType().getWrapperClass();
    }

    //todo replace Jaxb logic here!
    @Nonnull
    protected abstract String getMarshaledData(@Nonnull V modelElement);

    /**
     * Creates a new PO object.
     *
     * @param storageId    storage ID
     * @param user         user name
     * @param modelElement element from meta model.
     * @return {@link MetaModelPO} object
     */
    @Override
    @Nullable
    public MetaModelPO convertToPersistObject(@Nonnull V modelElement, @Nonnull String storageId, @Nonnull String user) {
        MetaModelPO po = new MetaModelPO();
        po.setId(getModelElementId(modelElement));
        po.setStorageId(storageId);
        po.setType(getModelType());
        po.setData(getMarshaledData(modelElement));
        po.setVersion(modelElement.getVersion());

        // New
        if (modelElement.getVersion() == 1L) {
            po.setCreateDate(new Date(System.currentTimeMillis()));
            po.setCreatedBy(user);
            // Update
        } else {
            po.setUpdateDate(new Date(System.currentTimeMillis()));
            po.setUpdatedBy(user);
        }

        return po;
    }

    @Override
    public boolean isUniqueModelElementId(V modelElement) {
        String modelElementId = getModelElementId(modelElement);
        return Arrays.stream(ModelType.values())
                .filter(modelType -> !modelType.equals(getModelType()))
                .allMatch(modelType -> metaModelService.getValueById(modelElementId, modelType.getWrapperClass()) == null);
    }

    @Override
    public void verifyModelElement(V modelElement) {
        String id = getModelElementId(modelElement);
        if (StringUtils.isBlank(id)) {
            throw new PlatformBusinessException(
                    "Unique identifier is not presented. In " + getModelType().getTag(),
                    MetaExceptionIds.EX_META_MODEL_ELEMENT_WITHOUT_ID,
                    getModelType().getTag()
            );
        }
        if (id.contains(DOT)) {
            throw new PlatformBusinessException(
                    "Unique identifier contains an unsupported symbol. In " + getModelType().getTag(),
                    MetaExceptionIds.EX_META_MODEL_ELEMENT_NOT_VALID,
                    Collections.singletonList(DOT), id
            );
        }
    }

    private void throwSimpleAttributeIsIncorrect(SimpleAttributeDef sDef, String entityDisplayName) {
        throw new PlatformBusinessException("Simple attribute is incorrect.",
                MetaExceptionIds.EX_META_SIMPLE_ATTRIBUTE_IS_INCORRECT,
                sDef.getDisplayName(), entityDisplayName);
    }

    protected void checkSimpleAttribute(SimpleAttributeDef sDef, String entityDisplayName) {
        boolean isSimpleDataTypeAttribute = sDef.getSimpleDataType() != null;
        boolean isLinkType = sDef.getSimpleDataType() == SimpleDataType.STRING || sDef.getSimpleDataType() == null;
        boolean isLinks = !isBlank(sDef.getEnumDataType()) || !isBlank(sDef.getLookupEntityType()) || !isBlank(sDef.getLinkDataType());

        if (!isSimpleDataTypeAttribute && !isLinks) {
            throwSimpleAttributeIsIncorrect(sDef, entityDisplayName);
        }

        if (isLinks && !isLinkType) {
            throwSimpleAttributeIsIncorrect(sDef, entityDisplayName);
        }

        if (sDef.getName().contains(DOT)) {
            throwSimpleAttributeIsIncorrect(sDef, entityDisplayName);
        }

        if (sDef.getSimpleDataType() == SimpleDataType.MEASURED) {

            AttributeMeasurementSettingsDef measurementDef = sDef.getMeasureSettings();
            String valueId = measurementDef == null ? null : measurementDef.getValueId();
            String unitId = measurementDef == null ? null : measurementDef.getDefaultUnitId();
            if (measurementDef == null || isBlank(valueId) || isBlank(unitId)) {
                throw new PlatformBusinessException("Measured attribute should have measurement settings.",
                        MetaExceptionIds.EX_META_MEASUREMENT_SETTINGS_SHOULD_BE_DEFINED, sDef.getDisplayName(),
                        entityDisplayName);
            }

            MeasurementValue value = metaMeasurementService.getValueById(valueId);
            if (value == null) {
                throw new PlatformBusinessException("Measurement value was not found.",
                        MetaExceptionIds.EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINED_VALUE, sDef.getDisplayName(),
                        valueId, entityDisplayName);
            }
            if (!value.present(unitId)) {
                throw new PlatformBusinessException("Measurement unit was not found.",
                        MetaExceptionIds.EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINED_UNIT, sDef.getDisplayName(), unitId,
                        entityDisplayName);
            }
        } else {
            if (sDef.getMeasureSettings() != null) {
                throw new PlatformBusinessException("Only measured attributes can have measurement settings",
                        MetaExceptionIds.EX_META_MEASUREMENT_SETTINGS_NOT_ALLOWED, sDef.getDisplayName(), entityDisplayName);
            }
        }
        //UN-4534
        if (!sDef.isNullable() && sDef.isReadOnly()) {
            throw new PlatformBusinessException("A Requared attr can be read only in the same time ",
                    MetaExceptionIds.EX_META_ATTR_CAN_NOT_BE_REQUIRED_AND_READ_ONLY, sDef.getDisplayName(),
                    entityDisplayName);
        }
        if (sDef.isHidden() && !sDef.isReadOnly()) {
            throw new PlatformBusinessException("A Hidden attr must be also read only",
                    MetaExceptionIds.EX_META_ATTR_CAN_NOT_BE_HIDDEN_AND_NOT_READ_ONLY, sDef.getDisplayName(),
                    entityDisplayName);
        }
        if (sDef.isMainDisplayable() && !sDef.isDisplayable()) {
            throw new PlatformBusinessException("A Main displayable attr must be displayable in the same time",
                    MetaExceptionIds.EX_META_ATTR_CAN_NOT_BE_MAIN_DISPLAYABLE_AND_NOT_DISPLAYABLE, sDef.getDisplayName(),
                    entityDisplayName);
        }
        if (sDef.isDisplayable() && sDef.isHidden()) {
            throw new PlatformBusinessException("A displayable attr can not be hidden in the same time",
                    MetaExceptionIds.EX_META_ATTR_CAN_NOT_BE_DISPLAYABLE_AND_HIDDEN, sDef.getDisplayName(),
                    entityDisplayName);
        }
    }

    public static void validateCustomProperties(List<CustomPropertyDef> customProperties) {
        if (CollectionUtils.isEmpty(customProperties)) {
            return;
        }
        final Set<String> invalidNames = new HashSet<>();
        final Set<String> duplicatedNames = new HashSet<>();
        final Set<String> propertiesNames = new HashSet<>();
        customProperties.forEach(property -> {
            final String propertyName = property.getName();
            if (!NAME_PATTERN.matcher(propertyName).matches()) {
                invalidNames.add(propertyName);
            }
            if (!propertiesNames.add(propertyName)) {
                duplicatedNames.add(propertyName);
            }
        });
        if (!invalidNames.isEmpty()) {
            throw new PlatformBusinessException("Invalid properties names: " + invalidNames,
                    MetaExceptionIds.EX_CUSTOM_PROPERTY_INVALID_NAMES,
                    invalidNames
            );
        }
        if (!duplicatedNames.isEmpty()) {
            throw new PlatformBusinessException("Duplicated properties names: " + duplicatedNames,
                    MetaExceptionIds.EX_CUSTOM_PROPERTY_DUPLICATED_NAMES,
                    duplicatedNames
            );
        }
    }

    @Override
    public void updateVersion(V modelElement) {
        W valueWrapper = getWrapperFromCache(modelElement);
        if (valueWrapper == null) {
            setInitialVersion(modelElement);
        } else {
            modelElement.setVersion(valueWrapper.getVersion() + 1);
        }
    }

    @Override
    public void setInitialVersion(V modelElement) {
        modelElement.setVersion(INITIAL_VERSION);
    }

    private W getWrapperFromCache(V modelElement) {
        return metaModelService.getValueById(getModelElementId(modelElement), getWrapperClass());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public W removeFromCache(
            @Nonnull String uniqueIdentifier,
            @Nonnull DeleteModelRequestContext deleteModelRequestContext,
            @Nonnull ModelCache modelCache) {
        return (W) modelCache.getCache().get(getWrapperClass()).remove(uniqueIdentifier);
    }
}

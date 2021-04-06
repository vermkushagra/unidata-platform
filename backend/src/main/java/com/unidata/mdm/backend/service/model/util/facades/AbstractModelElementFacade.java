package com.unidata.mdm.backend.service.model.util.facades;

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

import com.unidata.mdm.meta.CustomPropertyDef;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.po.MetaModelPO;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.ModelCache;
import com.unidata.mdm.backend.service.model.util.wrappers.ModelWrapper;
import com.unidata.mdm.meta.AttributeMeasurementSettingsDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;
import com.unidata.mdm.meta.VersionedObjectDef;
import org.springframework.util.CollectionUtils;

public abstract class AbstractModelElementFacade<W extends ModelWrapper, V extends VersionedObjectDef> implements ModelElementElementFacade<W, V> {

    static final long INITIAL_VERSION = 1L;
    static final String DOT = ".";

    public static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-z][a-z0-9_-]*$", Pattern.CASE_INSENSITIVE);

    @Autowired
    protected MetaModelServiceExt metaModelService;

    @Autowired
    private MetaMeasurementService metaMeasurementService;

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
            throw new BusinessException(
                    "Unique identifier is not presented. In " + getModelType().getTag(),
                    ExceptionId.EX_META_MODEL_ELEMENT_WITHOUT_ID,
                    getModelType().getTag()
            );
        }
        if (id.contains(DOT)) {
            throw new BusinessException(
                    "Unique identifier contains an unsupported symbol. In " + getModelType().getTag(),
                    ExceptionId.EX_META_MODEL_ELEMENT_NOT_VALID,
                    Collections.singletonList(DOT),
                    id
            );
        }
    }

    protected void checkSimpleAttribute(SimpleAttributeDef sDef, String entityDisplayName) {
        boolean isSimpleDataTypeAttribute = sDef.getSimpleDataType() != null;
        boolean isLinkType = sDef.getSimpleDataType() == SimpleDataType.STRING || sDef.getSimpleDataType() == null;
        boolean isLinks = !isBlank(sDef.getEnumDataType()) || !isBlank(sDef.getLookupEntityType()) || !isBlank(sDef.getLinkDataType());
        if (!isSimpleDataTypeAttribute && !isLinks) {
            throw new BusinessException("Simple attribute is incorrect", ExceptionId.EX_META_SIMPLE_ATTRIBUTE_IS_INCORRECT, sDef.getDisplayName(), entityDisplayName);
        }
        if (isLinks && !isLinkType) {
            throw new BusinessException("Simple attribute is incorrect", ExceptionId.EX_META_SIMPLE_ATTRIBUTE_IS_INCORRECT, sDef.getDisplayName(), entityDisplayName);
        }
        if (sDef.getName().contains(DOT)) {
            throw new BusinessException("Simple attribute is incorrect", ExceptionId.EX_META_SIMPLE_ATTRIBUTE_IS_INCORRECT, sDef.getDisplayName(), entityDisplayName);
        }

        if (sDef.getSimpleDataType() == SimpleDataType.MEASURED) {
            AttributeMeasurementSettingsDef measurementDef = sDef.getMeasureSettings();
            String valueId = measurementDef == null ? null : measurementDef.getValueId();
            String unitId = measurementDef == null ? null : measurementDef.getDefaultUnitId();
            if (measurementDef == null || isBlank(valueId) || isBlank(unitId)) {
                throw new BusinessException("Measured attribute should have measurement settings",
                        ExceptionId.EX_META_MEASUREMENT_SETTINGS_SHOULD_BE_DEFINE, sDef.getDisplayName(),
                        entityDisplayName);
            }
            MeasurementValue value = metaMeasurementService.getValueById(valueId);
            if (value == null) {
                throw new BusinessException("Measurement value is not present in system",
                        ExceptionId.EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINE_VALUE, sDef.getDisplayName(),
                        valueId, entityDisplayName);
            }
            if (!value.present(unitId)) {
                throw new BusinessException("Measurement unit is not present in system",
                        ExceptionId.EX_META_MEASUREMENT_SETTINGS_REFER_TO_UNDEFINE_UNIT, sDef.getDisplayName(), unitId,
                        entityDisplayName);
            }
        } else {
            if (sDef.getMeasureSettings() != null) {
                throw new BusinessException("Only measured attributes can have measurement settings",
                        ExceptionId.EX_META_MEASUREMENT_SETTINGS_NOT_ALLOW, sDef.getDisplayName(), entityDisplayName);
            }
        }
        //UN-4534
        if (!sDef.isNullable() && sDef.isReadOnly()) {
            throw new BusinessException("A Requared attr can be read only in the same time ",
                    ExceptionId.EX_META_ATTR_CAN_NOT_BE_REQUARED_AND_READ_ONLY, sDef.getDisplayName(),
                    entityDisplayName);
        }
        if (sDef.isHidden() && !sDef.isReadOnly()) {
            throw new BusinessException("A Hidden attr must be also read only",
                    ExceptionId.EX_META_ATTR_CAN_NOT_BE_HIDDEN_AND_NOT_READ_ONLY, sDef.getDisplayName(),
                    entityDisplayName);
        }
        if (sDef.isMainDisplayable() && !sDef.isDisplayable()) {
            throw new BusinessException("A Main displayable attr must be displayable in the same time",
                    ExceptionId.EX_META_ATTR_CAN_NOT_BE_MAIN_DISPLAYABLE_AND_NOT_DISPLAYABLE, sDef.getDisplayName(),
                    entityDisplayName);
        }
        if (sDef.isDisplayable() && sDef.isHidden()) {
            throw new BusinessException("A displayable attr can not be hidden in the same time",
                    ExceptionId.EX_META_ATTR_CAN_NOT_BE_DISPLAYABLE_AND_HIDDEN, sDef.getDisplayName(),
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
            throw new BusinessException(
                    "Invalid properties names: " + invalidNames,
                    ExceptionId.EX_CUSTOM_PROPERTY_INVALID_NAMES,
                    invalidNames
            );
        }
        if (!duplicatedNames.isEmpty()) {
            throw new BusinessException(
                    "Duplicated properties names: " + duplicatedNames,
                    ExceptionId.EX_CUSTOM_PROPERTY_DUPLICATED_NAMES,
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
            modelElement.setVersion(valueWrapper.getVersionOfWrappedElement() + 1);
        }
    }

    @Override
    public void setInitialVersion(V modelElement) {
        modelElement.setVersion(INITIAL_VERSION);
    }

    private W getWrapperFromCache(V modelElement) {
        return metaModelService.getValueById(getModelElementId(modelElement), getWrapperClass());
    }

    @Nullable
    @Override
    public W removeFromCache(@Nonnull String uniqueIdentifier, @Nonnull DeleteModelRequestContext deleteModelRequestContext, @Nonnull ModelCache modelCache) {
        return (W) modelCache.getCache().get(getWrapperClass()).remove(uniqueIdentifier);
    }
}

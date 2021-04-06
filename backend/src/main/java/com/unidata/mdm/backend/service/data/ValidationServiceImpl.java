package com.unidata.mdm.backend.service.data;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_ATTRIBUTE_MEASURED_UNIT_NOT_PRESENT;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_ATTRIBUTE_MEASURED_VALUE_NOT_PRESENT;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_ENTITY_NOT_FOUND_BY_NAME;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_ENUM_ATTRIBUTE_INCORRECT;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_IN_RANGE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_LOWER_BOUND;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_LARGE_OBJECT_VALUE_UNAVAILABLE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_MISSING_ATTRIBUTE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_NO_ENTITY_NAME;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_REQUIRED_ATTRS_IS_NOT_PRESENTED;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_WRONG_SPECIAL_ATTRIBUTE_TYPE;
import static com.unidata.mdm.meta.SimpleDataType.BOOLEAN;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.context.FetchLargeObjectRequestContext;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.impl.AbstractLargeValue;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.service.data.binary.LargeObjectsServiceComponent;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.meta.AbstractSimpleAttributeDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.ArrayValueType;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.EnumerationValue;
import com.unidata.mdm.meta.SimpleAttributeDef;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayValue;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * The Class ValidationService.
 *
 * @author Ruslan Trachuk
 */
@Service
public class ValidationServiceImpl implements ValidationServiceExt{

    /**
     * Logger.
     */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationServiceImpl.class);
    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * MetaModel service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * DataRecord service.
     */
    @Autowired
    private DataRecordsService dataRecordsService;
    /**
     * Measured service
     */
    @Autowired
    private MetaMeasurementService measurementService;

    /**
     * LOB component.
     */
    @Autowired
    private LargeObjectsServiceComponent lobComponent;

    @Nullable
    @Override
    public Multimap<AttributeInfoHolder, Object> getMissedLinkedLookupEntities(String etalonId, Date asOf) {

        GetRequestContext ctx = new GetRequestContext.GetRequestContextBuilder()
                .etalonKey(etalonId)
                .forDate(asOf)
                .fetchRelations(false)
                .build();

        GetRecordDTO record = dataRecordsService.getRecord(ctx);
        if (record.getEtalon() == null) {
            return null;
        }

        Map<LookupEntityDef, Set<AttributeInfoHolder>> toLookups = Collections.emptyMap();
        String entityName = record.getEtalon().getInfoSection().getEntityName();
        if (metaModelService.isLookupEntity(entityName)) {
            toLookups = metaModelService.getLookupEntityToLinkedLookups(entityName);
        } else if (metaModelService.isEntity(entityName)) {
            toLookups = metaModelService.getEntityToLinkedLookups(entityName);
        }

        if (toLookups.isEmpty()) {
            return null;
        }

        Multimap<AttributeInfoHolder, Object> result = HashMultimap.create();
        //todo rewrite! (complex search request + reduce number of loops)
        for (Map.Entry<LookupEntityDef, Set<AttributeInfoHolder>> entry : toLookups.entrySet()) {
            for (AttributeInfoHolder attributeHolder : entry.getValue()) {
                Collection<Attribute> attrs = record.getEtalon().getAttributeRecursive(attributeHolder.getPath());
                for (Attribute attribute : attrs) {
                    switch (attribute.getAttributeType()) {
                    case SIMPLE:
                        SimpleAttribute simpleAttribute = (SimpleAttribute) attribute;
                        Object ref = simpleAttribute.getValue();
                        if (ref == null) {
                            break;
                        }
                        SearchRequestContext requestContext = getLookupEntityValueSearchRequest(entry.getKey(), ref);
                        if (searchService.search(requestContext).getTotalCount() == 0) {
                            result.put(attributeHolder, ref);
                        }
                        break;
                    case ARRAY:
                        ArrayAttribute arrayAttribute = (ArrayAttribute) attribute;
                        for (Object arrayRef : arrayAttribute) {
                            ArrayValue arrayValue = (ArrayValue) arrayRef;
                            if (arrayValue.getValue() == null) {
                                continue;
                            }
                            SearchRequestContext requestCtx = getLookupEntityValueSearchRequest(entry.getKey(),
                                    arrayValue.getValue());
                            if (searchService.search(requestCtx).getTotalCount() == 0) {
                                result.put(attributeHolder, arrayValue.getValue());
                            }
                        }
                    }

                }
            }
        }
        return result;
    }

    /**
     * Search the specified lookup entities by code attribute value.
     *
     * @param lookupEntityDef    lookup entity definition
     * @param codeAttrValue code attribute value
     * @return search request
     */
    private SearchRequestContext getLookupEntityValueSearchRequest(LookupEntityDef lookupEntityDef, Object codeAttrValue) {
        SimpleDataType simpleDataType = lookupEntityDef.getCodeAttribute().getSimpleDataType();
        String codeAttrName = lookupEntityDef.getCodeAttribute().getName();
        FormField codeAttrForm = FormField.strictValue(simpleDataType, codeAttrName, codeAttrValue);
        FormField deletedForm = FormField.strictValue(BOOLEAN, RecordHeaderField.FIELD_DELETED.getField(), false);
        FormField published = FormField.strictValue(BOOLEAN, RecordHeaderField.FIELD_PUBLISHED.getField(), true);
        FormFieldsGroup groupFormFields = FormFieldsGroup.createAndGroup(codeAttrForm, deletedForm, published);
        return forEtalonData(lookupEntityDef.getName()).search(SearchRequestType.TERM)
                                                       .returnFields(Collections.singletonList(codeAttrName))
                                                       .operator(SearchRequestOperator.OP_AND)
                                                       .totalCount(true)
                                                       .countOnly(true)
                                                       .onlyQuery(true)
                                                       .count(1)
                                                       .page(0)
                                                       .form(groupFormFields)
                                                       .build();
    }


    @Override
    public void checkDataRecord(DataRecord record, String entityName){
        if (entityName == null) {
            final String message = "Invalid upsert request context. No entity name was supplied. Upsert rejected.";
            LOGGER.warn(message, entityName);
            throw new DataProcessingException(message, EX_DATA_UPSERT_NO_ENTITY_NAME);
        }

        EntityWrapper entityWrapper = metaModelService.getValueById(entityName, EntityWrapper.class);
        LookupEntityWrapper lookupEntityWrapper = metaModelService.getValueById(entityName, LookupEntityWrapper.class);
        if (entityWrapper == null && lookupEntityWrapper == null) {
            final String message = "Invalid upsert request context. Entity was not found by name. Upsert rejected.";
            LOGGER.warn(message, entityName);
            throw new DataProcessingException(message, EX_DATA_UPSERT_ENTITY_NOT_FOUND_BY_NAME, entityName);
        }

        Map<String, AttributeInfoHolder> attrs = entityWrapper == null ? lookupEntityWrapper.getAttributes() : entityWrapper.getAttributes();
        checkDataRecord(record, attrs, StringUtils.EMPTY, 0);
    }

    /**
     * Checks attributes for validity
     *
     * @param record the record
     * @param attrs  the attributes
     * @param prefix the prefix
     * @param level  current level
     * @throws DataProcessingException
     */
    private void checkDataRecord(DataRecord record, Map<String, AttributeInfoHolder> attrs, String prefix, int level) {

        Collection<String> requiredInLevelAttrs = attrs.entrySet().stream()
                .filter(attr -> attr.getValue().getLevel() == level)
                .filter(attr -> attr.getValue().isOfPath(prefix))
                .filter(attr -> isRequiredAttr(attr.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (Attribute attr : record.getAllAttributes()) {
            if (attr.getAttributeType() == Attribute.AttributeType.COMPLEX) {
                continue;
            }

            String attrPath = ModelUtils.getAttributePath(level, prefix, attr.getName());
            AttributeInfoHolder infoHolder = attrs.get(attrPath);
            checkFlatAttribute(attr, infoHolder, attrPath);

            if (valueExists(attr)) {
                requiredInLevelAttrs.remove(attrPath);
            }
        }

        if (!requiredInLevelAttrs.isEmpty()) {
            final String message = "Some required attributes is not presented. {}";
            LOGGER.warn(message, requiredInLevelAttrs);
            throw new BusinessException(message, EX_DATA_UPSERT_REQUIRED_ATTRS_IS_NOT_PRESENTED,
                    requiredInLevelAttrs.stream().map(name -> attrs.get(name).getAttribute().getDisplayName()).collect(Collectors.toList()));
        }

        Map<String, Integer> count = new HashMap<>();
        for (ComplexAttribute attr : record.getComplexAttributes()) {
            String attrPath = ModelUtils.getAttributePath(level, prefix, attr.getName());
            for (DataRecord nested : attr.getRecords()) {
                checkDataRecord(nested, attrs, attrPath, level + 1);
            }
            count.putIfAbsent(attrPath, 0);
            count.put(attrPath, count.get(attrPath) + attr.getRecords().size());
        }

        attrs.entrySet().stream()
                .filter(entity -> entity.getValue().getLevel() == level)
                .filter(entity -> entity.getValue().isOfPath(prefix))
                .filter(entity -> entity.getValue().isComplex())
                .forEach(entity -> checkCountOfComplexAttrs(count.get(entity.getKey()), (ComplexAttributeDef) entity.getValue().getAttribute()));
    }

    private boolean valueExists(Attribute attr) {

        if (attr.getAttributeType() == Attribute.AttributeType.ARRAY) {
            return !((ArrayAttribute<?>) attr).isEmpty();
        } else if (attr.getAttributeType() == Attribute.AttributeType.CODE) {
            return ((CodeAttribute<?>) attr).getValue() != null;
        } else if (attr.getAttributeType() == Attribute.AttributeType.SIMPLE) {
            return ((SimpleAttribute<?>) attr).getValue() != null;
        }

        return false;
    }

    private boolean isRequiredAttr(AttributeInfoHolder holder) {
        if (holder.isSimple()) {
            return !((SimpleAttributeDef) holder.getAttribute()).isNullable();
        } else if (holder.isCode()) {
            return !((CodeAttributeDef) holder.getAttribute()).isNullable();
        } else if(holder.isArray()) {
            return !((ArrayAttributeDef) holder.getAttribute()).isNullable();
        }
        return false;
    }

    /**
     * Check number of complex attributes.
     *
     * @param realCount        real number of complex attributes in entity
     * @param complexAttribute - definition of complex attributes
     */
    private void checkCountOfComplexAttrs(@Nullable Integer realCount, @Nonnull ComplexAttributeDef complexAttribute) {
        BigInteger count = realCount == null ? BigInteger.ZERO : BigInteger.valueOf(realCount);
        BigInteger minCount = complexAttribute.getMinCount();
        BigInteger maxCount = complexAttribute.getMaxCount();
        if (count.compareTo(minCount) < 0 || (maxCount != null && count.compareTo(maxCount) > 0)) {
            final String message = "Quantity of complex attributes '{}' should be in range {} - {} but current value is {}. Upsert rejected.";
            LOGGER.warn(message, complexAttribute.getName(), minCount, maxCount, count);
            if (maxCount == null) {
                throw new BusinessException(message, EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_LOWER_BOUND,
                        complexAttribute.getDisplayName(), minCount, count);
            } else {
                throw new BusinessException(message, EX_DATA_UPSERT_INCORRECT_QUANTITY_OF_COMPLEX_ATTRIBUTES_IN_RANGE,
                        complexAttribute.getDisplayName(), minCount, maxCount, count);
            }
        }
    }

    /**
     * Check simple attribute.
     *
     * @param attr - value
     * @param attrDef  -  attribute definition
     * @param attr path
     */
    @SuppressWarnings("unchecked")
    private void checkFlatAttribute(Attribute attr, AttributeInfoHolder attrDef, String attrPath) {

        if (attrDef == null) {
            final String message = "Attribute '{}' supplied for upsert is missing in the model. Upsert rejected.";
            LOGGER.warn(message, attrPath);
            throw new DataProcessingException(message, EX_DATA_UPSERT_MISSING_ATTRIBUTE,
                    attrPath);
        }

        boolean wrongType =
                (attrDef.isSimple() && attr.getAttributeType() != Attribute.AttributeType.SIMPLE)
                        || (attrDef.isCode() && attr.getAttributeType() != Attribute.AttributeType.CODE)
                        || (attrDef.isArray() && attr.getAttributeType() != Attribute.AttributeType.ARRAY);

        if (wrongType) {
            final String message = "Attribute {} supplied for upsert is of the wrong type {} compared to the model (type {} is expected). Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getAttributeType().name(),
                    attrDef.isSimple()
                            ? Attribute.AttributeType.SIMPLE.name()
                            : attrDef.isCode() ? Attribute.AttributeType.CODE.name() : Attribute.AttributeType.ARRAY.name());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_UPSERT_WRONG_ATTRIBUTE_TYPE,
                    attrPath,
                    attr.getAttributeType().name(),
                    attrDef.isSimple()
                            ? Attribute.AttributeType.SIMPLE.name()
                            : attrDef.isCode() ? Attribute.AttributeType.CODE.name() : Attribute.AttributeType.ARRAY.name());
        }

        if (attrDef.isSimple()) {

            SimpleAttributeDef sDef = attrDef.narrow();
            boolean isDataType = sDef.getSimpleDataType() != null;
            if (isDataType) {
                checkSimpleAttributeValue((SimpleAttribute<?>) attr, sDef.getSimpleDataType(), attrPath);

                if (sDef.getSimpleDataType() == SimpleDataType.MEASURED) {
                    checkMeasuredAttr((MeasuredSimpleAttributeImpl) attr, sDef);
                }

                if(sDef.getSimpleDataType() == SimpleDataType.BLOB || sDef.getSimpleDataType() == SimpleDataType.CLOB){
                    checkLargeObjectAttr((SimpleAttribute<AbstractLargeValue>) attr, sDef);
                }
            } else {
                if (attrDef.isEnumValue() || attrDef.isLinkTemplate()) {
                    checkStringValueAndType((SimpleAttribute<?>) attr, attrPath);
                }

                if (attrDef.isEnumValue()) {
                    checkEnumAttr((SimpleAttribute<?>) attr, sDef);
                }

                if (attrDef.isLookupLink()) {
                    checkValueForLookupTarget((SimpleAttribute<?>) attr, sDef);
                }
            }
        } else if (attrDef.isCode()) {
            checkCodeAttributeValue((CodeAttribute<?>) attr, ((AbstractSimpleAttributeDef) attrDef.getAttribute()).getSimpleDataType(), attrPath);
        } else if (attrDef.isArray()) {
            checkArrayAttributeValue((ArrayAttribute<?>) attr, attrDef.narrow(), attrPath);
        }
    }

    /**
     * TODO remove this as soon specific types exist.
     * @param attr
     * @param sDef
     */
    private void checkValueForLookupTarget(SimpleAttribute<?> attr, SimpleAttributeDef sDef) {

        String targetName =  sDef.getLookupEntityType();
        if (isBlank(targetName)) {
            return;
        }

        LookupEntityWrapper ew = metaModelService.getValueById(targetName, LookupEntityWrapper.class);
        SimpleDataType targetType = ew.getEntity().getCodeAttribute().getSimpleDataType();
        if ((targetType == SimpleDataType.INTEGER && attr.getDataType() != SimpleAttribute.DataType.INTEGER)
                || (targetType == SimpleDataType.STRING && attr.getDataType() != SimpleAttribute.DataType.STRING)) {
            final String message = "Wrong code attribute link value type {}, referencing {}. Upsert rejected.";
            LOGGER.warn(message, attr.getValue(), targetName);
            throw new DataProcessingException(message,
                    ExceptionId.EX_DATA_UPSERT_WRONG_SIMPLE_CODE_ATTRIBUTE_REFERENCE_VALUE,
                    attr.getDataType().name(), targetName);
        }
    }

    //check is it enum, link or reference
    private void checkStringValueAndType(SimpleAttribute<?> attr, String attrPath) {

        boolean notAStringAttrType = attr.getDataType() != null && attr.getDataType() != SimpleAttribute.DataType.STRING;
        if (notAStringAttrType) {
            final String message =
                    "Attribute '{}' supplied for upsert is either Enumeration or calculated Link in the model, "
                            + "and has to have type 'String' "
                            + "while type attribute '{}' is supplied for upsert. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name());
            throw new DataProcessingException(message, EX_DATA_UPSERT_WRONG_SPECIAL_ATTRIBUTE_TYPE, attrPath,
                    attr.getDataType().name());
        }
    }

    // check is enum value present
    private void checkEnumAttr(SimpleAttribute<?> attr, SimpleAttributeDef sDef) {
        String value = attr.castValue();
        String enumId = sDef.getEnumDataType();
        if (isBlank(enumId)) {
            return;
        }
        EnumerationDataType enumeration = metaModelService.getEnumerationById(enumId);
        Collection<EnumerationValue> enumValues = enumeration == null ? emptyList() : enumeration.getEnumVal();
        boolean isEnumPresent = value == null ? true : enumValues.stream().anyMatch(val -> val.getName().equals(value));
        if (!isEnumPresent) {
            throw new BusinessException("Enum value doesn't present", EX_DATA_UPSERT_ENUM_ATTRIBUTE_INCORRECT,
                    value, enumId);
        }
    }

    private void checkMeasuredAttr(MeasuredSimpleAttributeImpl measAttr, SimpleAttributeDef sDef) {
        String valueAttrId = measAttr.getValueId();
        String unitId = measAttr.getInitialUnitId();
        if (valueAttrId == null && unitId == null) {
            return;
        }
        String valueId = sDef.getMeasureSettings().getValueId();
        MeasurementValue measurementValue = measurementService.getValueById(valueId);
        if (measurementValue == null) {
            return;
        }
        if (valueAttrId != null && !valueAttrId.equals(valueId)) {
            throw new DataProcessingException("measured value is not present",
                    EX_DATA_ATTRIBUTE_MEASURED_VALUE_NOT_PRESENT, measAttr.getName());
        }
        if (unitId != null && !measurementValue.present(unitId)) {
            throw new DataProcessingException("measured unit is not present",
                    EX_DATA_ATTRIBUTE_MEASURED_UNIT_NOT_PRESENT, measAttr.getName());
        }
    }

    /**
     * @param attr     - value
     * @param type     - attr type
     * @param attrPath - path to value
     */
    private void checkCodeAttributeValue(@Nonnull CodeAttribute<?> attr, @Nonnull SimpleDataType type, @Nonnull String attrPath) {

        CodeAttribute.CodeDataType expectedType = CodeAttribute.CodeDataType.valueOf(type.name());
        if (attr.getDataType() != expectedType) {
            final String message = "Code attribute {} supplied for upsert has type {} "
                    + "while type attribute {} is expected from the model. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
            throw new DataProcessingException(message, ExceptionId.EX_DATA_UPSERT_WRONG_CODE_ATTRIBUTE_VALUE_TYPE,
                    attrPath,
                    attr.getDataType().name(),
                    expectedType.name());
        }
    }

    /**
     * @param attr     - value
     * @param type     - attr type
     * @param attrPath - path to value
     */
    private void checkSimpleAttributeValue(@Nonnull SimpleAttribute<?> attr, @Nonnull SimpleDataType type, @Nonnull String attrPath) {
        SimpleAttribute.DataType expectedType = SimpleAttribute.DataType.valueOf(type.name());
        if (attr.getDataType() != expectedType) {
            final String message = "Attribute {} supplied for upsert has type {} "
                    + "while type attribute {} is expected from the model. Upsert rejected.";
            LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
            throw new DataProcessingException(message, EX_DATA_UPSERT_WRONG_SIMPLE_ATTRIBUTE_VALUE_TYPE,
                    attrPath,
                    attr.getDataType().name(),
                    expectedType.name());
        }
    }
    /**
     * Check expected value and type attribute.
     *
     * @param attr         the attribute
     * @param def  attribute definition
     * @param attrPath attribute path
     */
    private void checkArrayAttributeValue(ArrayAttribute<?> attr, ArrayAttributeDef def, String attrPath) {

        ArrayValueType modelType = def.getArrayValueType();
        if (modelType != null) {
            ArrayAttribute.ArrayDataType expectedType = ArrayAttribute.ArrayDataType.valueOf(modelType.name());
            if (attr.getDataType() != expectedType) {
                final String message = "Array attribute {} supplied for upsert has type {} "
                        + "while type attribute {} is expected from the model. Upsert rejected.";
                LOGGER.warn(message, attrPath, attr.getDataType().name(), expectedType.name());
                throw new DataProcessingException(message, ExceptionId.EX_DATA_UPSERT_WRONG_ARRAY_ATTRIBUTE_VALUE_TYPE,
                        attrPath,
                        attr.getDataType().name(),
                        expectedType.name());
            }
        } else {
            String targetName =  def.getLookupEntityType();
            if (isBlank(targetName)) {
                return;
            }

            LookupEntityWrapper ew = metaModelService.getValueById(targetName, LookupEntityWrapper.class);
            SimpleDataType targetType = ew.getEntity().getCodeAttribute().getSimpleDataType();
            if ((targetType == SimpleDataType.INTEGER && attr.getDataType() != ArrayAttribute.ArrayDataType.INTEGER)
                    || (targetType == SimpleDataType.STRING && attr.getDataType() != ArrayAttribute.ArrayDataType.STRING)) {
                final String message = "Wrong array code attribute link value type {}, referencing {}. Upsert rejected.";
                LOGGER.warn(message, attr.getValue(), targetName);
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_UPSERT_WRONG_ARRAY_CODE_ATTRIBUTE_REFERENCE_VALUE,
                        attr.getDataType().name(), targetName);
            }
        }
    }


    private void checkLargeObjectAttr(SimpleAttribute<AbstractLargeValue> attr, SimpleAttributeDef sDef) {

        boolean isExist = lobComponent.checkExistLargeObject(new FetchLargeObjectRequestContext.FetchLargeObjectRequestContextBuilder()
                .recordKey(attr.getValue().getId())
                .binary(sDef.getSimpleDataType() == SimpleDataType.BLOB)
                .build());

        if(!isExist){
            throw new DataProcessingException("Can't find large object for attribute", EX_DATA_UPSERT_LARGE_OBJECT_VALUE_UNAVAILABLE, attr.getName());
        }
    }
}

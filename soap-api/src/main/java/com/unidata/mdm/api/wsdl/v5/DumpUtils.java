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

package com.unidata.mdm.api.wsdl.v5;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.api.v5.AliasCodeAttributePointerDef;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.ReferenceAliasKey;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.AbstractRelationInfoSection;
import com.unidata.mdm.backend.common.types.ArrayValue;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.CodeAttributeAlias;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonClassifierInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;
import com.unidata.mdm.backend.common.types.InfoSection;
import com.unidata.mdm.backend.common.types.OriginClassifierInfoSection;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.OriginRelationInfoSection;
import com.unidata.mdm.backend.common.types.impl.AbstractLargeValue;
import com.unidata.mdm.backend.common.types.impl.BinaryLargeValueImpl;
import com.unidata.mdm.backend.common.types.impl.BlobSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.CharacterLargeValueImpl;
import com.unidata.mdm.backend.common.types.impl.ClobSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.ComplexAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DateArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DateArrayValue;
import com.unidata.mdm.backend.common.types.impl.DateSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerArrayValue;
import com.unidata.mdm.backend.common.types.impl.IntegerCodeAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberArrayValue;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringArrayValue;
import com.unidata.mdm.backend.common.types.impl.StringCodeAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimeArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimeArrayValue;
import com.unidata.mdm.backend.common.types.impl.TimeSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampArrayValue;
import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;
import com.unidata.mdm.data.v5.AbstractAttribute;
import com.unidata.mdm.data.v5.ApprovalState;
import com.unidata.mdm.data.v5.ArrayAttribute;
import com.unidata.mdm.data.v5.ArrayDataType;
import com.unidata.mdm.data.v5.BlobValue;
import com.unidata.mdm.data.v5.ClobValue;
import com.unidata.mdm.data.v5.CodeAttribute;
import com.unidata.mdm.data.v5.ComplexAttribute;
import com.unidata.mdm.data.v5.DataQualityStatusType;
import com.unidata.mdm.data.v5.EtalonClassifierRecord;
import com.unidata.mdm.data.v5.EtalonRecord;
import com.unidata.mdm.data.v5.EtalonRecordInfoSection;
import com.unidata.mdm.data.v5.IntegralRecord;
import com.unidata.mdm.data.v5.MeasuredValue;
import com.unidata.mdm.data.v5.NestedRecord;
import com.unidata.mdm.data.v5.ObjectFactory;
import com.unidata.mdm.data.v5.OriginClassifierRecord;
import com.unidata.mdm.data.v5.OriginRecord;
import com.unidata.mdm.data.v5.OriginRecordInfoSection;
import com.unidata.mdm.data.v5.RecordStatus;
import com.unidata.mdm.data.v5.RelationBase;
import com.unidata.mdm.data.v5.RelationTo;
import com.unidata.mdm.data.v5.RelationToInfoSection;
import com.unidata.mdm.data.v5.SimpleAttribute;



/**
 * The Class DumpUtils.
 *
 * @author Mikhail Mikhailov
 * Temporary JAXB -> internal dump stuff.
 * Subject for removal.
 */
public class DumpUtils {

    private static final ObjectFactory V5_DATA_FACTORY = new ObjectFactory();
    /**
     * Constructor.
     */
    private DumpUtils() {
        super();
    }

    /**
     * From.
     *
     * @param soapKey the soap key
     * @return the etalon key
     */
    public static EtalonKey from(com.unidata.mdm.data.v5.EtalonKey soapKey) {

        if (Objects.isNull(soapKey)) {
            return null;
        }

        // UN-5331 UUID string in invalid format causes SQLException.
        // UUID string will be replaced with native UUID representation later.
        String uuidAsString = null;
        try {
            uuidAsString = UUID.fromString(soapKey.getId()).toString();
        } catch (IllegalArgumentException e) {
            throw new DataProcessingException("Wrong UUID format.", e, ExceptionId.EX_DATA_V4_ETALON_ID_UUID_INVALID);
        }

        return EtalonKey.builder().id(uuidAsString).build();
    }

    /**
     * From.
     *
     * @param soapKey the soap key
     * @return the origin key
     */
    public static OriginKey from(com.unidata.mdm.data.v5.OriginKey soapKey) {

        if (Objects.isNull(soapKey)) {
            return null;
        }

        // UN-5331 UUID string in invalid format causes SQLException.
        // UUID string will be replaced with native UUID representation later.
        String uuidAsString = null;
        if (Objects.nonNull(soapKey.getId())) {
            try {
                uuidAsString = UUID.fromString(soapKey.getId()).toString();
            } catch (IllegalArgumentException e) {
                throw new DataProcessingException("Wrong UUID format.", e, ExceptionId.EX_DATA_V4_ORIGIN_ID_UUID_INVALID);
            }
        }

        return OriginKey.builder()
                .id(uuidAsString)
                .entityName(soapKey.getEntityName())
                .externalId(soapKey.getExternalId())
                .sourceSystem(soapKey.getSourceSystem())
                .build();
    }

    /**
     * From.
     *
     * @param soapKey the soap key
     * @return the reference alias key
     */
    public static ReferenceAliasKey from(com.unidata.mdm.api.v5.ReferenceAliasKey soapKey) {
        if (Objects.isNull(soapKey)) {
            return null;
        }

        return ReferenceAliasKey.builder()
                .entityAttributeName(soapKey.getEntityAttributeName())
                .value(soapKey.getValue())
                .build();
    }

    /**
     * To.
     *
     * @param systemKey the system key
     * @return the com.unidata.mdm.data.v 3 . etalon key
     */
    public static com.unidata.mdm.data.v5.EtalonKey to(EtalonKey systemKey) {
        if (Objects.isNull(systemKey)) {
            return null;
        }

        return JaxbUtils.getDataObjectFactory().createEtalonKey().withId(systemKey.getId());
    }

    /**
     * To.
     *
     * @param systemKey the system key
     * @return the com.unidata.mdm.data.v 3 . origin key
     */
    public static com.unidata.mdm.data.v5.OriginKey to(OriginKey systemKey) {
        if (Objects.isNull(systemKey)) {
            return null;
        }

        return JaxbUtils.getDataObjectFactory().createOriginKey()
                .withId(systemKey.getId())
                .withEntityName(systemKey.getEntityName())
                .withExternalId(systemKey.getExternalId())
                .withSourceSystem(systemKey.getSourceSystem());
    }

    /**
     * To.
     *
     * @param error the error
     * @return the com.unidata.mdm.data.v 3 . data quality error
     */
    public static com.unidata.mdm.data.v5.DataQualityError to(DataQualityError error) {

        if (Objects.isNull(error)) {
            return null;
        }

        return new com.unidata.mdm.data.v5.DataQualityError()
                .withCategory(error.getCategory())
                .withCreateDate(JaxbUtils.dateToXMGregorianCalendar(error.getCreateDate()))
                .withErrorId(error.getId())
                .withMessage(error.getMessage())
                .withRuleName(error.getRuleName())
                .withSeverity(error.getSeverity() == null ? null : error.getSeverity().name())
                .withStatus(error.getStatus() == null ? null : DataQualityStatusType.valueOf(error.getStatus().name()))
                .withUpdateDate(JaxbUtils.dateToXMGregorianCalendar(error.getUpdateDate()))
                .withValues(error.getValues().stream()
                        .map(v -> V5_DATA_FACTORY.createDataQualityErrorValue()
                                .withPath(v.getKey())
                                .withCause(to(v.getValue())))
                        .collect(Collectors.toList()));
    }

    /**
     * To.
     *
     * @param error the error
     * @return the list
     */
    public static List<com.unidata.mdm.data.v5.DataQualityError> to(List<DataQualityError> error) {

        if (CollectionUtils.isEmpty(error)) {
            return Collections.emptyList();
        }

        List<com.unidata.mdm.data.v5.DataQualityError> result = new ArrayList<>();
        for (DataQualityError dqe : error) {
            result.add(to(dqe));
        }

        return result;
    }

    /**
     * To.
     *
     * @param attr the attr
     * @return the simple attribute
     */
    public static SimpleAttribute to(com.unidata.mdm.backend.common.types.SimpleAttribute<?> attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        SimpleAttribute sa = JaxbUtils.getDataObjectFactory().createSimpleAttribute()
                .withName(attr.getName());

        switch (attr.getDataType()) {
        case BLOB:
            BlobValue blob = null;
            if (attr.getValue() != null) {
                BinaryLargeValue blv = attr.castValue();
                blob = JaxbUtils.getDataObjectFactory().createBlobValue()
                        .withFileName(blv.getFileName())
                        .withId(blv.getId())
                        .withMimeType(blv.getMimeType())
                        .withSize(blv.getSize())
                        .withData(blv instanceof AbstractLargeValue && ((AbstractLargeValue) blv).getData() != null
                                ? ((AbstractLargeValue) blv).getData()
                                : null);
            }
            sa.setBlobValue(JaxbUtils.getDataObjectFactory(), blob);
            break;
        case BOOLEAN:
            sa.setBoolValue(JaxbUtils.getDataObjectFactory(), attr.castValue());
            break;
        case CLOB:
            ClobValue clob = null;
            if (attr.getValue() != null) {
                CharacterLargeValue clv = attr.castValue();
                clob = JaxbUtils.getDataObjectFactory().createClobValue()
                        .withFileName(clv.getFileName())
                        .withId(clv.getId())
                        .withMimeType(clv.getMimeType())
                        .withSize(clv.getSize())
                        .withData(clv instanceof AbstractLargeValue && ((AbstractLargeValue) clv).getData() != null
                                ? new String(Base64.getEncoder().encode(((AbstractLargeValue) clv).getData()), StandardCharsets.UTF_8)
                                : null);
            }
            sa.setClobValue(JaxbUtils.getDataObjectFactory(), clob);
            break;
        case DATE:
            XMLGregorianCalendar dateVal = null;
            if (attr.getValue() != null) {
                LocalDate attrVal = attr.castValue();
                dateVal = JaxbUtils.getDatatypeFactory().newXMLGregorianCalendarDate(
                        attrVal.getYear(), attrVal.getMonthValue(), attrVal.getDayOfMonth(),
                            DatatypeConstants.FIELD_UNDEFINED);
            }
            sa.setDateValue(JaxbUtils.getDataObjectFactory(), dateVal);
            break;
        case LINK:
        case ENUM:
        case STRING:
            sa.setStringValue(JaxbUtils.getDataObjectFactory(), attr.castValue());
            break;
        case INTEGER:
            sa.setIntValue(JaxbUtils.getDataObjectFactory(), attr.castValue());
            break;
        case NUMBER:
            sa.setNumberValue(JaxbUtils.getDataObjectFactory(), attr.castValue());
            break;
        case MEASURED:
            MeasuredSimpleAttributeImpl number = (MeasuredSimpleAttributeImpl) attr;
            Double value = number.getInitialValue();
            sa.setMeasuredValue(JaxbUtils.getDataObjectFactory(), value == null ? null :
                    JaxbUtils.getDataObjectFactory().createMeasuredValue()
                             .withValue(value)
                             .withMeasurementValueId(number.getValueId())
                             .withMeasurementUnitId(number.getInitialUnitId()));
            break;
        case TIME:
            XMLGregorianCalendar timeVal = null;
            if (attr.getValue() != null) {
                LocalTime attrVal = attr.castValue();
                timeVal = JaxbUtils.getDatatypeFactory().newXMLGregorianCalendarTime(
                    attrVal.getHour(), attrVal.getMinute(), attrVal.getSecond(),
                    (int) TimeUnit.MILLISECONDS.convert(attrVal.getNano(), TimeUnit.NANOSECONDS),
                        DatatypeConstants.FIELD_UNDEFINED);
            }
            sa.setTimeValue(JaxbUtils.getDataObjectFactory(), timeVal);
            break;
        case TIMESTAMP:
            XMLGregorianCalendar tsVal = null;
            if (attr.getValue() != null) {
                LocalDateTime attrVal = attr.castValue();
                tsVal = JaxbUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        attrVal.getYear(), attrVal.getMonthValue(), attrVal.getDayOfMonth(),
                        attrVal.getHour(), attrVal.getMinute(), attrVal.getSecond(),
                        (int) TimeUnit.MILLISECONDS.convert(attrVal.getNano(), TimeUnit.NANOSECONDS),
                            DatatypeConstants.FIELD_UNDEFINED);
            }
            sa.setTimestampValue(JaxbUtils.getDataObjectFactory(), tsVal);
            break;
        default:
            break;
        }

        return sa;
    }

    public static CodeAttribute to(com.unidata.mdm.backend.common.types.CodeAttribute<?> attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        CodeAttribute ca = JaxbUtils.getDataObjectFactory().createCodeAttribute()
                .withName(attr.getName());

        switch (attr.getDataType()) {
        case STRING:
            ca.setStringValue(attr.castValue());
            ca.withSupplementaryStringValues(CollectionUtils.isEmpty(attr.getSupplementary())
                    ? Collections.emptyList()
                    : attr.getSupplementary().stream().map(v -> (String) v).collect(Collectors.toList()));
            break;
        case INTEGER:
            ca.setIntValue(attr.castValue());
            ca.withSupplementaryIntValues(CollectionUtils.isEmpty(attr.getSupplementary())
                    ? Collections.emptyList()
                    : attr.getSupplementary().stream().map(v -> (Long) v).collect(Collectors.toList()));
            break;
        default:
            break;
        }

        return ca;
    }

    @SuppressWarnings("unchecked")
    public static ArrayAttribute to(com.unidata.mdm.backend.common.types.ArrayAttribute<?> attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        ArrayAttribute aa = JaxbUtils.getDataObjectFactory().createArrayAttribute()
                .withName(attr.getName());

        switch (attr.getDataType()) {
        case DATE:
            List<XMLGregorianCalendar> dateVal = null;
            if (!attr.isEmpty()) {
                com.unidata.mdm.backend.common.types.ArrayAttribute<LocalDate> dateValues
                    = (com.unidata.mdm.backend.common.types.ArrayAttribute<LocalDate>) attr;

                dateVal = new ArrayList<>(dateValues.getValue().size());
                for (ArrayValue<LocalDate> attrVal : dateValues) {
                    XMLGregorianCalendar converted
                        = JaxbUtils.getDatatypeFactory().newXMLGregorianCalendarDate(
                                attrVal.getValue().getYear(), attrVal.getValue().getMonthValue(), attrVal.getValue().getDayOfMonth(),
                                    DatatypeConstants.FIELD_UNDEFINED);
                    dateVal.add(converted);
                }
            }
            aa.withDateValue(dateVal);
            aa.setType(ArrayDataType.DATE);
            break;
        case TIME:
            List<XMLGregorianCalendar> timeVal = null;
            if (!attr.isEmpty()) {
                com.unidata.mdm.backend.common.types.ArrayAttribute<LocalTime> timeValues
                    = (com.unidata.mdm.backend.common.types.ArrayAttribute<LocalTime>) attr;

                timeVal = new ArrayList<>(timeValues.getValue().size());
                for (ArrayValue<LocalTime> attrVal : timeValues) {
                    XMLGregorianCalendar converted = JaxbUtils.getDatatypeFactory().newXMLGregorianCalendarTime(
                        attrVal.getValue().getHour(), attrVal.getValue().getMinute(), attrVal.getValue().getSecond(),
                        (int) TimeUnit.MILLISECONDS.convert(attrVal.getValue().getNano(), TimeUnit.NANOSECONDS),
                            DatatypeConstants.FIELD_UNDEFINED);
                    timeVal.add(converted);
                }
            }
            aa.withTimeValue(timeVal);
            aa.setType(ArrayDataType.TIME);
            break;
        case TIMESTAMP:
            List<XMLGregorianCalendar> tsVal = null;
            if (!attr.isEmpty()) {
                com.unidata.mdm.backend.common.types.ArrayAttribute<LocalDateTime> tsValues
                    = (com.unidata.mdm.backend.common.types.ArrayAttribute<LocalDateTime>) attr;

                tsVal= new ArrayList<>(tsValues.getValue().size());
                for (ArrayValue<LocalDateTime> attrVal : tsValues) {
                    XMLGregorianCalendar converted = JaxbUtils.getDatatypeFactory().newXMLGregorianCalendar(
                            attrVal.getValue().getYear(), attrVal.getValue().getMonthValue(), attrVal.getValue().getDayOfMonth(),
                            attrVal.getValue().getHour(), attrVal.getValue().getMinute(), attrVal.getValue().getSecond(),
                            (int) TimeUnit.MILLISECONDS.convert(attrVal.getValue().getNano(), TimeUnit.NANOSECONDS),
                                DatatypeConstants.FIELD_UNDEFINED);
                    tsVal.add(converted);
                }
            }
            aa.withTimestampValue(tsVal);
            aa.setType(ArrayDataType.TIMESTAMP);
            break;
        case STRING:
            com.unidata.mdm.backend.common.types.ArrayAttribute<String> stringValues
                = (com.unidata.mdm.backend.common.types.ArrayAttribute<String>) attr;

            aa.withStringValue(stringValues.isEmpty()
                    ? null
                    : stringValues.getValue().stream()
                        .map(ArrayValue::getValue)
                        .collect(Collectors.toList()));
            aa.setType(ArrayDataType.STRING);
            break;
        case INTEGER:
            com.unidata.mdm.backend.common.types.ArrayAttribute<Long> intValues
                = (com.unidata.mdm.backend.common.types.ArrayAttribute<Long>) attr;

            aa.withIntValue(intValues.isEmpty()
                    ? null
                    : intValues.getValue().stream()
                        .map(ArrayValue::getValue)
                        .collect(Collectors.toList()));
            aa.setType(ArrayDataType.INTEGER);
            break;
        case NUMBER:
            com.unidata.mdm.backend.common.types.ArrayAttribute<Double> numberValues
                = (com.unidata.mdm.backend.common.types.ArrayAttribute<Double>) attr;

            aa.withNumberValue(numberValues.isEmpty()
                    ? null
                    : numberValues.getValue().stream()
                        .map(ArrayValue::getValue)
                        .collect(Collectors.toList()));
            aa.setType(ArrayDataType.NUMBER);
            break;
        default:
            break;
        }

        return aa;
    }

    /**
     * To.
     *
     * @param attr the attr
     * @return the complex attribute
     */
    public static ComplexAttribute to(com.unidata.mdm.backend.common.types.ComplexAttribute attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        ComplexAttribute val = JaxbUtils.getDataObjectFactory().createComplexAttribute();
        val.setName(attr.getName());

        for (DataRecord record : attr) {
            val.getNestedRecord().add(to(record));
        }

        return val;
    }

    public static AbstractAttribute to(Attribute attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }

        switch (attribute.getAttributeType()) {
        case ARRAY:
            return to((com.unidata.mdm.backend.common.types.ArrayAttribute<?>) attribute);
        case CODE:
            return to((com.unidata.mdm.backend.common.types.CodeAttribute<?>) attribute);
        case COMPLEX:
            return to((com.unidata.mdm.backend.common.types.ComplexAttribute) attribute);
        case SIMPLE:
            return to((com.unidata.mdm.backend.common.types.SimpleAttribute<?>) attribute);
        default:
            break;
        }

        return null;
    }
    /**
     * To.
     *
     * @param data the data
     * @return the nested record
     */
    public static NestedRecord to (DataRecord data) {

        if (Objects.isNull(data)) {
            return null;
        }

        List<SimpleAttribute> simple = new ArrayList<>();
        List<CodeAttribute> code = new ArrayList<>();
        List<ArrayAttribute> array = new ArrayList<>();
        List<ComplexAttribute> complex = new ArrayList<>();

        for (Attribute attr : data.getAllAttributes()) {
            if (attr.getAttributeType() == AttributeType.SIMPLE) {
                simple.add(to((com.unidata.mdm.backend.common.types.SimpleAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.CODE) {
                code.add(to((com.unidata.mdm.backend.common.types.CodeAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.ARRAY) {
                array.add(to((com.unidata.mdm.backend.common.types.ArrayAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.COMPLEX) {
                complex.add(to((com.unidata.mdm.backend.common.types.ComplexAttribute) attr));
            }
        }

        NestedRecord val = JaxbUtils.getDataObjectFactory().createNestedRecord();
        val.withSimpleAttributes(simple)
           .withCodeAttributes(code)
           .withArrayAttributes(array)
           .withComplexAttributes(complex);

        return val;
    }

    /**
     * To.
     *
     * @param is the is
     * @return the origin record info section
     */
    public static OriginRecordInfoSection to(com.unidata.mdm.backend.common.types.OriginRecordInfoSection is) {
        if (is == null) {
            return null;
        }

        return JaxbUtils.getDataObjectFactory().createOriginRecordInfoSection()
                .withApproval(ApprovalState.valueOf(is.getApproval().name()))
                .withCreateDate(JaxbUtils.dateToXMGregorianCalendar(is.getCreateDate()))
                .withCreatedBy(is.getCreatedBy())
                .withEntityName(is.getOriginKey().getEntityName())
                .withRangeFrom(JaxbUtils.dateToXMGregorianCalendar(is.getValidFrom()))
                .withRangeTo(JaxbUtils.dateToXMGregorianCalendar(is.getValidTo()))
                .withRevision(is.getRevision())
                .withStatus(RecordStatus.valueOf(is.getStatus().name()))
                .withUpdateDate(JaxbUtils.dateToXMGregorianCalendar(is.getUpdateDate()))
                .withUpdatedBy(is.getUpdatedBy());
    }

    /**
     * To.
     *
     * @param is the is
     * @return the etalon record info section
     */
    public static EtalonRecordInfoSection to(com.unidata.mdm.backend.common.types.EtalonRecordInfoSection is) {
        if (is == null) {
            return null;
        }

        return JaxbUtils.getDataObjectFactory().createEtalonRecordInfoSection()
                .withApproval(ApprovalState.valueOf(is.getApproval().name()))
                .withCreateDate(JaxbUtils.dateToXMGregorianCalendar(is.getCreateDate()))
                .withCreatedBy(is.getCreatedBy())
                .withEntityName(is.getEntityName())
                .withRangeFrom(JaxbUtils.dateToXMGregorianCalendar(is.getValidFrom()))
                .withRangeTo(JaxbUtils.dateToXMGregorianCalendar(is.getValidTo()))
                .withStatus(RecordStatus.valueOf(is.getStatus().name()))
                .withUpdateDate(JaxbUtils.dateToXMGregorianCalendar(is.getUpdateDate()))
                .withUpdatedBy(is.getUpdatedBy());
    }

    /**
     * To.
     *
     * @param is the is
     * @return the relation to info section
     */
    public static RelationToInfoSection to(com.unidata.mdm.backend.common.types.EtalonRelationInfoSection is) {
        if (is == null) {
            return null;
        }

        return JaxbUtils.getDataObjectFactory().createRelationToInfoSection()
                .withApproval(ApprovalState.valueOf(is.getApproval().name()))
                .withCreateDate(JaxbUtils.dateToXMGregorianCalendar(is.getCreateDate()))
                .withCreatedBy(is.getCreatedBy())
                .withRangeFrom(JaxbUtils.dateToXMGregorianCalendar(is.getValidFrom()))
                .withRangeTo(JaxbUtils.dateToXMGregorianCalendar(is.getValidTo()))
                .withStatus(RecordStatus.valueOf(is.getStatus().name()))
                .withUpdateDate(JaxbUtils.dateToXMGregorianCalendar(is.getUpdateDate()))
                .withUpdatedBy(is.getUpdatedBy());
    }

    /**
     * To.
     *
     * @param <T> the generic type
     * @param data the data
     * @param infoSection the info section
     * @param klass the klass
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public static <T> T to (DataRecord data, InfoSection infoSection, Class<T> klass) {

        if (Objects.isNull(data)) {
            return null;
        }

        List<SimpleAttribute> simple = new ArrayList<>();
        List<CodeAttribute> code = new ArrayList<>();
        List<ArrayAttribute> array = new ArrayList<>();
        List<ComplexAttribute> complex = new ArrayList<>();

        for (Attribute attr : data.getAllAttributes()) {
            if (attr.getAttributeType() == AttributeType.SIMPLE) {
                simple.add(to((com.unidata.mdm.backend.common.types.SimpleAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.CODE) {
                code.add(to((com.unidata.mdm.backend.common.types.CodeAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.ARRAY) {
                array.add(to((com.unidata.mdm.backend.common.types.ArrayAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.COMPLEX) {
                complex.add(to((com.unidata.mdm.backend.common.types.ComplexAttribute) attr));
            }
        }

        if (klass == RelationTo.class) {
            RelationTo relation = JaxbUtils.getDataObjectFactory().createRelationTo();
            relation.getSimpleAttributes().addAll(simple);
            relation.getComplexAttributes().addAll(complex);
            relation.withRelName(((EtalonRelationInfoSection) infoSection).getRelationName());
            relation.withToEtalonKey(to((((EtalonRelationInfoSection) infoSection).getToEtalonKey())));
            relation.withInfoSection(to((EtalonRelationInfoSection) infoSection));
            return (T) relation;
        } else if (klass == IntegralRecord.class) {
            IntegralRecord relation = JaxbUtils.getDataObjectFactory().createIntegralRecord();
            relation.withRelName(((AbstractRelationInfoSection) infoSection).getRelationName());
            relation.withEtalonRecord(EtalonRelation.class.isInstance(data)
                    ? JaxbUtils.getDataObjectFactory().createEtalonRecord()
                        .withSimpleAttributes(simple)
                        .withCodeAttributes(code)
                        .withArrayAttributes(array)
                        .withComplexAttributes(complex)
                        .withEtalonKey(to((((EtalonRelationInfoSection) infoSection).getToEtalonKey())))
                        .withInfoSection(JaxbUtils.getDataObjectFactory().createEtalonRecordInfoSection()
				                .withApproval(ApprovalState.valueOf(((EtalonRelationInfoSection) infoSection).getApproval().name()))
				                .withCreateDate(JaxbUtils.dateToXMGregorianCalendar(((EtalonRelationInfoSection) infoSection).getCreateDate()))
				                .withCreatedBy(((EtalonRelationInfoSection) infoSection).getCreatedBy())
				                .withEntityName(((EtalonRelationInfoSection) infoSection).getToEntityName())
				                .withRangeFrom(JaxbUtils.dateToXMGregorianCalendar(((EtalonRelationInfoSection) infoSection).getValidFrom()))
				                .withRangeTo(JaxbUtils.dateToXMGregorianCalendar(((EtalonRelationInfoSection) infoSection).getValidTo()))
				                .withStatus(RecordStatus.valueOf(((EtalonRelationInfoSection) infoSection).getStatus().name()))
				                .withUpdateDate(JaxbUtils.dateToXMGregorianCalendar(((EtalonRelationInfoSection) infoSection).getUpdateDate()))
				                .withUpdatedBy(((EtalonRelationInfoSection) infoSection).getUpdatedBy()))
                        .withEtalonKey(to(((EtalonRelationInfoSection) infoSection).getToEtalonKey()))
                    : null);

            relation.withOriginRecord(OriginRelation.class.isInstance(data)
                    ? JaxbUtils.getDataObjectFactory().createOriginRecord()
                        .withSimpleAttributes(simple)
                        .withCodeAttributes(code)
                        .withArrayAttributes(array)
                        .withComplexAttributes(complex)
                        .withOriginKey(to(((OriginRelationInfoSection) infoSection).getToOriginKey()))
                    : null);
            return (T) relation;
        } else if (klass == OriginRecord.class) {
            OriginRecord origin = JaxbUtils.getDataObjectFactory().createOriginRecord();
            origin.getSimpleAttributes().addAll(simple);
            origin.getArrayAttributes().addAll(array);
            origin.getCodeAttributes().addAll(code);
            origin.getComplexAttributes().addAll(complex);

            if (infoSection != null) {
                origin.setOriginKey(to(((com.unidata.mdm.backend.common.types.OriginRecordInfoSection) infoSection).getOriginKey()));
                origin.setInfoSection(to((com.unidata.mdm.backend.common.types.OriginRecordInfoSection) infoSection));
            }
            return (T) origin;
        } else if (klass == EtalonRecord.class) {
            EtalonRecord etalon = JaxbUtils.getDataObjectFactory().createEtalonRecord();
            etalon.getSimpleAttributes().addAll(simple);
            etalon.getArrayAttributes().addAll(array);
            etalon.getCodeAttributes().addAll(code);
            etalon.getComplexAttributes().addAll(complex);

            if (infoSection != null) {
                etalon.setEtalonKey(to(((com.unidata.mdm.backend.common.types.EtalonRecordInfoSection) infoSection).getEtalonKey()));
                etalon.setInfoSection(to((com.unidata.mdm.backend.common.types.EtalonRecordInfoSection) infoSection));
            }
            return (T) etalon;
        } else if (klass == OriginClassifierRecord.class) {
            OriginClassifierRecord origin = JaxbUtils.getDataObjectFactory().createOriginClassifierRecord();
            origin.getSimpleAttributes().addAll(simple);
            origin.getComplexAttributes().addAll(complex);
            if (infoSection != null) {
                origin.setClassifierName(((OriginClassifierInfoSection) infoSection).getClassifierName());
                origin.setClassifierNodeId(((OriginClassifierInfoSection) infoSection).getNodeId());
                origin.setStatus(RecordStatus.valueOf(infoSection.getStatus().name()));
            }
            return (T) origin;
        } else if (klass == EtalonClassifierRecord.class) {
            EtalonClassifierRecord etalon = JaxbUtils.getDataObjectFactory().createEtalonClassifierRecord();
            etalon.getSimpleAttributes().addAll(simple);
            etalon.getComplexAttributes().addAll(complex);
            if (infoSection != null) {
                etalon.setClassifierName(((EtalonClassifierInfoSection) infoSection).getClassifierName());
                etalon.setClassifierNodeId(((EtalonClassifierInfoSection) infoSection).getNodeId());
                etalon.setStatus(RecordStatus.valueOf(infoSection.getStatus().name()));
            }
            return (T) etalon;
        }

        return null;
    }
    /**
     * Convert nested record.
     * @param record the record to convert
     * @return view
     */
    public static SerializableDataRecord from(NestedRecord record) {

        if (Objects.isNull(record)) {
            return null;
        }

        int simpleAttributesSize = record.getSimpleAttributes().size();
        int complexAttributesSize = record.getComplexAttributes().size();
        int arrayAttributesSize = record.getArrayAttributes().size();
        int codeAttributesSize = record.getCodeAttributes().size();

        SerializableDataRecord sdr = new SerializableDataRecord(
                simpleAttributesSize +
                codeAttributesSize +
                arrayAttributesSize +
                complexAttributesSize + 1);

        record.getSimpleAttributes().forEach(attr -> sdr.addAttribute(from(attr)));
        record.getCodeAttributes().forEach(a -> sdr.addAttribute(fromCodeAttribute(a)));
        record.getArrayAttributes().forEach(a -> sdr.addAttribute(fromArrayAttribute(a)));
        record.getComplexAttributes().forEach(a -> sdr.addAttribute(fromComplexAttribute(a)));

        return sdr;
    }

    /**
     * Converts relation.
     * @param relation the relation to convert
     * @return view
     */
    public static SerializableDataRecord from(RelationBase relation) {

        if (Objects.isNull(relation)) {
            return null;
        }

        final SerializableDataRecord sdr;
        if (RelationTo.class.isInstance(relation)) {

            RelationTo relto = (RelationTo) relation;
            int simpleAttributesSize = relto.getSimpleAttributes().size();
            int complexAttributesSize = relto.getComplexAttributes().size();

            sdr = new SerializableDataRecord(simpleAttributesSize + complexAttributesSize + 1);
            relto.getSimpleAttributes().forEach(attr -> sdr.addAttribute(from(attr)));
            relto.getComplexAttributes().forEach(a -> sdr.addAttribute(fromComplexAttribute(a)));
        } else if (IntegralRecord.class.isInstance(relation)) {
            IntegralRecord integral = (IntegralRecord) relation;
            NestedRecord content = integral.getEtalonRecord() != null ? integral.getEtalonRecord() : integral.getOriginRecord();

            sdr = from(content);
        } else {
            sdr = null;
        }

        return sdr;
    }

    /**
     * Converts code attribute.
     * @param jaxbAttr JAXB attribute
     * @return attribute
     */
    private static Attribute fromCodeAttribute(CodeAttribute jaxbAttr) {

        if (Objects.isNull(jaxbAttr) || Objects.isNull(jaxbAttr.getType())) {
            return null;
        }

        Attribute result = null;
        switch (jaxbAttr.getType()) {
        case INTEGER:
            result = new IntegerCodeAttributeImpl(jaxbAttr.getName(), jaxbAttr.getIntValue());
            break;
        case STRING:
            result = new StringCodeAttributeImpl(jaxbAttr.getName(), jaxbAttr.getStringValue());
            break;
        default:
            break;
        }

        return result;
    }
    /**
     * Converts complex attributes.
     * @param jaxbAttr JAXB attribute
     * @return attribute
     */
    private static Attribute fromComplexAttribute(ComplexAttribute jaxbAttr) {

        if (Objects.isNull(jaxbAttr)) {
            return null;
        }

        ComplexAttributeImpl complex = new ComplexAttributeImpl(jaxbAttr.getName());
        jaxbAttr.getNestedRecord().forEach(nr -> complex.add(from(nr)));

        return complex;
    }

    /**
     * Converts complex attribute.
     * @param jaxbAttr JAXB attribute
     * @return attribute
     */
    private static Attribute fromArrayAttribute(ArrayAttribute jaxbAttr) {

        if (Objects.isNull(jaxbAttr) || Objects.isNull(jaxbAttr.getType())) {
            return null;
        }

        Attribute result = null;
        switch (jaxbAttr.getType()) {
        case DATE:
            result = new DateArrayAttributeImpl(jaxbAttr.getName(), fromDateArrayValue(jaxbAttr.getDateValue()));
            break;
        case INTEGER:
            result = new IntegerArrayAttributeImpl(jaxbAttr.getName(), jaxbAttr.getIntValue().isEmpty()
                    ? null
                    : jaxbAttr.getIntValue().stream().map(IntegerArrayValue::new).collect(Collectors.toList()));
            break;
        case NUMBER:
            result = new NumberArrayAttributeImpl(jaxbAttr.getName(), jaxbAttr.getNumberValue().isEmpty()
                    ? null
                    : jaxbAttr.getNumberValue().stream().map(NumberArrayValue::new).collect(Collectors.toList()));
            break;
        case STRING:
            result = new StringArrayAttributeImpl(jaxbAttr.getName(), jaxbAttr.getStringValue().isEmpty()
                    ? null
                    : jaxbAttr.getStringValue().stream().map(StringArrayValue::new).collect(Collectors.toList()));
            break;
        case TIME:
            result = new TimeArrayAttributeImpl(jaxbAttr.getName(), fromTimeArrayValue(jaxbAttr.getTimeValue()));
            break;
        case TIMESTAMP:
            result = new TimestampArrayAttributeImpl(jaxbAttr.getName(), fromTimestampArrayValue(jaxbAttr.getTimestampValue()));
            break;
        default:
            break;
        }

        return result;
    }

    /**
     * Converts complex attribute.
     * @param jaxbAttr JAXB attribute
     * @return attribute
     */
    public static Attribute from(SimpleAttribute jaxbAttr) {

        if (Objects.isNull(jaxbAttr)) {
            return null;
        }

        Attribute result = null;
        switch (jaxbAttr.getType()) {
        case BLOB:
            result = new BlobSimpleAttributeImpl(jaxbAttr.getName(), fromBlobValue(jaxbAttr.getValue()));
            break;
        case BOOLEAN:
            result = new BooleanSimpleAttributeImpl(jaxbAttr.getName(), jaxbAttr.getValue());
            break;
        case CLOB:
            result = new ClobSimpleAttributeImpl(jaxbAttr.getName(), fromClobValue(jaxbAttr.getValue()));
            break;
        case DATE:
            result = new DateSimpleAttributeImpl(jaxbAttr.getName(), fromDateValue(jaxbAttr.getValue()));
            break;
        case INTEGER:
            result = new IntegerSimpleAttributeImpl(jaxbAttr.getName(), jaxbAttr.getValue());
            break;
        case NUMBER:
            result = new NumberSimpleAttributeImpl(jaxbAttr.getName(), jaxbAttr.getValue());
            break;
        case STRING:
            result = new StringSimpleAttributeImpl(jaxbAttr.getName(), jaxbAttr.getValue());
            break;
        case TIME:
            result = new TimeSimpleAttributeImpl(jaxbAttr.getName(), fromTimeValue(jaxbAttr.getValue()));
            break;
        case TIMESTAMP:
            result = new TimestampSimpleAttributeImpl(jaxbAttr.getName(), fromTimestampValue(jaxbAttr.getValue()));
            break;
        case MEASURED:
             MeasuredValue value = jaxbAttr.getValue();
             result = new MeasuredSimpleAttributeImpl(jaxbAttr.getName(), value == null ? null : value.getValue())
                    .withValueId(value == null ? null : value.getMeasurementValueId())
                    .withInitialUnitId(value == null ? null : value.getMeasurementUnitId());
             break;
        default:
            break;
        }

        return result;
    }

    /**
     * Converts BLOB value.
     * @param jaxbBlobValue JAXB BLOB value
     * @return internal
     */
    private static BinaryLargeValue fromBlobValue(BlobValue jaxbBlobValue) {

        if (jaxbBlobValue == null) {
            return null;
        }

        return new BinaryLargeValueImpl()
            .withData(jaxbBlobValue.getData())
            .withFileName(jaxbBlobValue.getFileName())
            .withId(jaxbBlobValue.getId())
            .withMimeType(jaxbBlobValue.getMimeType())
            .withSize(jaxbBlobValue.getSize());
    }

    /**
     * Converts CLOB value.
     * @param jaxbClobValue JAXB CLOB value
     * @return internal
     */
    private static CharacterLargeValue fromClobValue(ClobValue jaxbClobValue) {

        if (jaxbClobValue == null) {
            return null;
        }

        return new CharacterLargeValueImpl()
            .withData(jaxbClobValue.getData() == null ? null : jaxbClobValue.getData().getBytes())
            .withFileName(jaxbClobValue.getFileName())
            .withId(jaxbClobValue.getId())
            .withMimeType(jaxbClobValue.getMimeType())
            .withSize(jaxbClobValue.getSize());
    }

    /**
     * Converts date value.
     * @param jaxbCalendar XML gregorian calendar instance
     * @return {@link LocalDate} or null
     */
    private static LocalDate fromDateValue(XMLGregorianCalendar jaxbCalendar) {

        if (Objects.isNull(jaxbCalendar)) {
            return null;
        }

        return LocalDate.of(jaxbCalendar.getYear(), jaxbCalendar.getMonth(), jaxbCalendar.getDay());
    }

    /**
     * Converts time value.
     * @param jaxbCalendar XML gregorian calendar instance
     * @return {@link LocalTime} or null
     */
    private static LocalTime fromTimeValue(XMLGregorianCalendar jaxbCalendar) {

        if (Objects.isNull(jaxbCalendar)) {
            return null;
        }

        return LocalTime.of(jaxbCalendar.getHour(), jaxbCalendar.getMinute(), jaxbCalendar.getSecond(),
                    (int) TimeUnit.NANOSECONDS.convert(jaxbCalendar.getMillisecond(),
                          TimeUnit.MILLISECONDS));
    }

    /**
     * Converts time stamp value.
     * @param jaxbCalendar XML gregorian calendar nstance
     * @return {@link LocalDateTime} or null
     */
    private static LocalDateTime fromTimestampValue(XMLGregorianCalendar jaxbCalendar) {

        if (Objects.isNull(jaxbCalendar)) {
            return null;
        }

        return LocalDateTime.of(
                jaxbCalendar.getYear(), jaxbCalendar.getMonth(), jaxbCalendar.getDay(),
                jaxbCalendar.getHour(), jaxbCalendar.getMinute(), jaxbCalendar.getSecond(),
                    (int) TimeUnit.NANOSECONDS.convert(jaxbCalendar.getMillisecond(),
                          TimeUnit.MILLISECONDS));
    }

	/**
	 * Convert alias code attr ps.
	 *
	 * @param source the source
	 * @return the list
	 */
	public static List<CodeAttributeAlias> convertAliasCodeAttrPs(
			List<AliasCodeAttributePointerDef> source) {

		if (CollectionUtils.isEmpty(source)) {
			return Collections.emptyList();
		}

		List<CodeAttributeAlias> target = new ArrayList<>(source.size());
		for (AliasCodeAttributePointerDef s : source) {

		    CodeAttributeAlias alias = convertAliasCodeAttrP(s);
		    if (Objects.isNull(alias)) {
		        continue;
		    }

			target.add(alias);
		}

		return target;
	}

	/**
	 * Convert alias code attr P.
	 *
	 * @param source the source
	 * @return the com.unidata.mdm.api. alias code attribute pointer def
	 */
	public static CodeAttributeAlias convertAliasCodeAttrP(
			AliasCodeAttributePointerDef source) {

		if (source == null) {
			return null;
		}

		return new CodeAttributeAlias(source.getAliasCodeAttributeName(), source.getRecordAttributeName());
	}

	/**
     * Converts date value.
     * @param jaxbCalendar XML gregorian calendar instance
     * @return {@link LocalDate} or null
     */
    private static List<ArrayValue<LocalDate>> fromDateArrayValue(List<XMLGregorianCalendar> jaxbCalendar) {

        if (Objects.isNull(jaxbCalendar) || jaxbCalendar.isEmpty()) {
            return null;
        }

        List<ArrayValue<LocalDate>> result = new ArrayList<>(jaxbCalendar.size());
        jaxbCalendar.forEach(el -> result.add(new DateArrayValue(LocalDate.of(el.getYear(), el.getMonth(), el.getDay()))));
        return result;
    }

    /**
     * Converts time value.
     * @param jaxbCalendar XML gregorian calendar instance
     * @return {@link LocalTime} or null
     */
    private static List<ArrayValue<LocalTime>> fromTimeArrayValue(List<XMLGregorianCalendar> jaxbCalendar) {

        if (Objects.isNull(jaxbCalendar) || jaxbCalendar.isEmpty()) {
            return null;
        }

        List<ArrayValue<LocalTime>> result = new ArrayList<>();
        jaxbCalendar.forEach(el -> result.add(new TimeArrayValue(LocalTime.of(el.getHour(), el.getMinute(), el.getSecond(),
                    (int) TimeUnit.NANOSECONDS.convert(el.getMillisecond(),
                          TimeUnit.MILLISECONDS)))));
        return result;
    }

    /**
     * Converts time stamp value.
     * @param jaxbCalendar XML gregorian calendar nstance
     * @return {@link LocalDateTime} or null
     */
    private static List<ArrayValue<LocalDateTime>> fromTimestampArrayValue(List<XMLGregorianCalendar> jaxbCalendar) {

        if (Objects.isNull(jaxbCalendar) || jaxbCalendar.isEmpty()) {
            return null;
        }

        List<ArrayValue<LocalDateTime>> result = new ArrayList<>();
        jaxbCalendar.forEach(el -> result.add(new TimestampArrayValue(LocalDateTime.of(
                el.getYear(), el.getMonth(), el.getDay(),
                el.getHour(), el.getMinute(), el.getSecond(),
                    (int) TimeUnit.NANOSECONDS.convert(el.getMillisecond(),
                          TimeUnit.MILLISECONDS)))));
        return result;
    }
}
package com.unidata.mdm.backend.service.dump.jaxb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.util.CollectionUtils;

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
import com.unidata.mdm.backend.common.types.CodeLinkValue;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonClassifierInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;
import com.unidata.mdm.backend.common.types.InfoSection;
import com.unidata.mdm.backend.common.types.OriginClassifierInfoSection;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.OriginRelationInfoSection;
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
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.data.ApprovalState;
import com.unidata.mdm.data.ArrayAttribute;
import com.unidata.mdm.data.ArrayDataType;
import com.unidata.mdm.data.BlobValue;
import com.unidata.mdm.data.ClobValue;
import com.unidata.mdm.data.CodeAttribute;
import com.unidata.mdm.data.ComplexAttribute;
import com.unidata.mdm.data.DataQualityStatusType;
import com.unidata.mdm.data.EtalonClassifierRecord;
import com.unidata.mdm.data.EtalonRecord;
import com.unidata.mdm.data.EtalonRecordInfoSection;
import com.unidata.mdm.data.ExternalSourceId;
import com.unidata.mdm.data.IntegralRecord;
import com.unidata.mdm.data.MeasuredValue;
import com.unidata.mdm.data.NestedRecord;
import com.unidata.mdm.data.OriginClassifierRecord;
import com.unidata.mdm.data.OriginRecord;
import com.unidata.mdm.data.OriginRecordInfoSection;
import com.unidata.mdm.data.RecordStatus;
import com.unidata.mdm.data.RelationBase;
import com.unidata.mdm.data.RelationTo;
import com.unidata.mdm.data.SimpleAttribute;

/**
 * @author Mikhail Mikhailov
 * Temporary JAXB -> internal dump stuff.
 * Subject for removal.
 */
public class JaxbDataRecordUtils {

    /**
     * Constructor.
     */
    private JaxbDataRecordUtils() {
        super();
    }

    public static EtalonKey from(com.unidata.mdm.data.EtalonKey soapKey) {
        if (Objects.isNull(soapKey)) {
            return null;
        }

        return EtalonKey.builder().id(soapKey.getId()).build();
    }

    public static OriginKey from(com.unidata.mdm.data.OriginKey soapKey) {
        if (Objects.isNull(soapKey)) {
            return null;
        }

        return OriginKey.builder()
                .id(soapKey.getId())
                .entityName(soapKey.getEntityName())
                .externalId(soapKey.getExternalId())
                .sourceSystem(soapKey.getSourceSystem())
                .build();
    }

    public static ReferenceAliasKey from(com.unidata.mdm.api.ReferenceAliasKey soapKey) {
        if (Objects.isNull(soapKey)) {
            return null;
        }

        return ReferenceAliasKey.builder()
                .entityAttributeName(soapKey.getEntityAttributeName())
                .value(soapKey.getValue())
                .build();
    }

    public static com.unidata.mdm.data.EtalonKey to(EtalonKey systemKey) {
        if (Objects.isNull(systemKey)) {
            return null;
        }

        return JaxbUtils.getDataObjectFactory().createEtalonKey().withId(systemKey.getId());
    }

    public static com.unidata.mdm.data.OriginKey to(OriginKey systemKey) {
        if (Objects.isNull(systemKey)) {
            return null;
        }

        return JaxbUtils.getDataObjectFactory().createOriginKey()
                .withId(systemKey.getId())
                .withEntityName(systemKey.getEntityName())
                .withExternalId(systemKey.getExternalId())
                .withSourceSystem(systemKey.getSourceSystem());
    }

    public static com.unidata.mdm.data.DataQualityError to(DataQualityError error) {

        if (Objects.isNull(error)) {
            return null;
        }

        return new com.unidata.mdm.data.DataQualityError()
                .withCategory(error.getCategory())
                .withCreateDate(JaxbUtils.dateToXMGregorianCalendar(error.getCreateDate()))
                .withErrorId(error.getId())
                .withMessage(error.getMessage())
                .withRuleName(error.getRuleName())
                .withSeverity(error.getSeverity() == null ? null : error.getSeverity().name())
                .withStatus(error.getStatus() == null ? null : DataQualityStatusType.valueOf(error.getStatus().name()))
                .withUpdateDate(JaxbUtils.dateToXMGregorianCalendar(error.getUpdateDate()));
    }

    public static List<com.unidata.mdm.data.DataQualityError> to(List<DataQualityError> error) {

        if (CollectionUtils.isEmpty(error)) {
            return Collections.emptyList();
        }

        List<com.unidata.mdm.data.DataQualityError> result = new ArrayList<>();
        for (DataQualityError dqe : error) {
            result.add(to(dqe));
        }

        return result;
    }

    public static SimpleAttribute to(com.unidata.mdm.backend.common.types.SimpleAttribute<?> attr, Map<String, List<ExternalSourceId>> externalSourceIds) {

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
                        .withSize(blv.getSize());
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
                        .withSize(clv.getSize());
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
            sa.setMeasuredValue(JaxbUtils.getDataObjectFactory(),
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

        if (attr instanceof CodeLinkValue
                && ((CodeLinkValue) attr).hasLinkEtalonId()
                && externalSourceIds.containsKey(((CodeLinkValue) attr).getLinkEtalonId())) {
            sa.withExternalSourceIds(
                    externalSourceIds.get(((CodeLinkValue) attr).getLinkEtalonId())
            );
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

        // UN-6625, UN-7242
        // Since nulls are not allowed as array values,
        // a null singleton is used as an empty indicator here
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

            aa.withDateValue(dateVal == null ? Collections.singletonList(null) : dateVal);
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

            aa.withTimeValue(timeVal == null ? Collections.singletonList(null) : timeVal);
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

            aa.withTimestampValue(tsVal == null ? Collections.singletonList(null) : tsVal);
            aa.setType(ArrayDataType.TIMESTAMP);
            break;
        case STRING:
            com.unidata.mdm.backend.common.types.ArrayAttribute<String> stringValues
                = (com.unidata.mdm.backend.common.types.ArrayAttribute<String>) attr;

            aa.withStringValue(stringValues.isEmpty() ? new String[]{null} : stringValues.toArray(String[].class));
            aa.setType(ArrayDataType.STRING);
            break;
        case INTEGER:
            com.unidata.mdm.backend.common.types.ArrayAttribute<Long> intValues
                = (com.unidata.mdm.backend.common.types.ArrayAttribute<Long>) attr;

            aa.withIntValue(intValues.isEmpty() ? new Long[]{null} : intValues.toArray(Long[].class));
            aa.setType(ArrayDataType.INTEGER);
            break;
        case NUMBER:
            com.unidata.mdm.backend.common.types.ArrayAttribute<Double> numberValues
                = (com.unidata.mdm.backend.common.types.ArrayAttribute<Double>) attr;

            aa.withNumberValue(numberValues.isEmpty() ? new Double[]{null} : numberValues.toArray(Double[].class));
            aa.setType(ArrayDataType.NUMBER);
            break;
        default:
            break;
        }

        return aa;
    }

    public static ComplexAttribute to(com.unidata.mdm.backend.common.types.ComplexAttribute attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        ComplexAttribute val = JaxbUtils.getDataObjectFactory().createComplexAttribute();
        val.setName(attr.getName());

        for (DataRecord record : attr.getRecords()) {
            val.getNestedRecord().add(to(record));
        }

        return val;
    }

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
                simple.add(to((com.unidata.mdm.backend.common.types.SimpleAttribute<?>) attr, Collections.emptyMap()));
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

    public static <T> T to(DataRecord data, InfoSection infoSection, Class<T> klass) {
        return to(data, infoSection, klass, Collections.emptyMap());
    }

    @SuppressWarnings("unchecked")
    public static <T> T to(DataRecord data, InfoSection infoSection, Class<T> klass, Map<String, List<ExternalSourceId>> externalSourceIds) {

        if (Objects.isNull(data)) {
            return null;
        }

        List<SimpleAttribute> simple = new ArrayList<>();
        List<CodeAttribute> code = new ArrayList<>();
        List<ArrayAttribute> array = new ArrayList<>();
        List<ComplexAttribute> complex = new ArrayList<>();

        for (Attribute attr : data.getAllAttributes()) {
            if (attr.getAttributeType() == AttributeType.SIMPLE) {
                simple.add(to((com.unidata.mdm.backend.common.types.SimpleAttribute<?>) attr, externalSourceIds));
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

        int predictedSize = simpleAttributesSize +
                codeAttributesSize +
                arrayAttributesSize +
                complexAttributesSize + 1;

        SerializableDataRecord sdr = new SerializableDataRecord(predictedSize);

        record.getSimpleAttributes().forEach(attr -> sdr.addAttribute(fromSimpleAttribute(attr)));
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
            relto.getSimpleAttributes().forEach(attr -> sdr.addAttribute(fromSimpleAttribute(attr)));
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
     * Converts complex attributes.
     * @param jaxbAttr JAXB attribute
     * @return attribute
     */
    private static Attribute fromComplexAttribute(ComplexAttribute jaxbAttr) {

        if (Objects.isNull(jaxbAttr)) {
            return null;
        }

        ComplexAttributeImpl complex = new ComplexAttributeImpl(jaxbAttr.getName());
        jaxbAttr.getNestedRecord().forEach(nr -> complex.getRecords().add(from(nr)));

        return complex;
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
     * Converts complex attribute.
     * @param jaxbAttr JAXB attribute
     * @return attribute
     */
    private static Attribute fromArrayAttribute(ArrayAttribute jaxbAttr) {

        if (Objects.isNull(jaxbAttr) || Objects.isNull(jaxbAttr.getType())) {
            return null;
        }

        // UN-6625, UN-7242
        // Since nulls are not allowed as array values,
        // a null singleton is used as an empty indicator here
        Attribute result = null;
        switch (jaxbAttr.getType()) {
        case DATE:
            result = new DateArrayAttributeImpl(jaxbAttr.getName(), fromDateArrayValue(jaxbAttr.getDateValue()));
            break;
        case INTEGER:
            result = new IntegerArrayAttributeImpl(jaxbAttr.getName(), jaxbAttr.getIntValue().isEmpty()
                    ? Collections.emptyList()
                    : jaxbAttr.getIntValue().stream()
                        .filter(Objects::nonNull)
                        .map(IntegerArrayValue::new)
                        .collect(Collectors.toList()));
            break;
        case NUMBER:
            result = new NumberArrayAttributeImpl(jaxbAttr.getName(), jaxbAttr.getNumberValue().isEmpty()
                    ? Collections.emptyList()
                    : jaxbAttr.getNumberValue().stream()
                        .filter(Objects::nonNull)
                        .map(NumberArrayValue::new)
                        .collect(Collectors.toList()));
            break;
        case STRING:
            result = new StringArrayAttributeImpl(jaxbAttr.getName(), jaxbAttr.getStringValue().isEmpty()
                    ? Collections.emptyList()
                    : jaxbAttr.getStringValue().stream()
                        .filter(Objects::nonNull)
                        .map(StringArrayValue::new)
                        .collect(Collectors.toList()));
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
    private static Attribute fromSimpleAttribute(SimpleAttribute jaxbAttr) {

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
     * Converts date value.
     * @param jaxbCalendar XML gregorian calendar instance
     * @return {@link LocalDate} or null
     */
    private static List<ArrayValue<LocalDate>> fromDateArrayValue(List<XMLGregorianCalendar> jaxbCalendar) {

        if (Objects.isNull(jaxbCalendar) || jaxbCalendar.isEmpty()) {
            return Collections.emptyList();
        }

        return jaxbCalendar.stream()
            .filter(Objects::nonNull)
            .map(el -> new DateArrayValue(LocalDate.of(el.getYear(), el.getMonth(), el.getDay())))
            .collect(Collectors.toList());
    }

    /**
     * Converts time value.
     * @param jaxbCalendar XML gregorian calendar instance
     * @return {@link LocalTime} or null
     */
    private static List<ArrayValue<LocalTime>> fromTimeArrayValue(List<XMLGregorianCalendar> jaxbCalendar) {

        if (Objects.isNull(jaxbCalendar) || jaxbCalendar.isEmpty()) {
            return Collections.emptyList();
        }

        return jaxbCalendar.stream()
            .filter(Objects::nonNull)
            .map(el -> new TimeArrayValue(LocalTime.of(el.getHour(), el.getMinute(), el.getSecond(),
                (int) TimeUnit.NANOSECONDS.convert(el.getMillisecond(), TimeUnit.MILLISECONDS))))
            .collect(Collectors.toList());
    }

    /**
     * Converts time stamp value.
     * @param jaxbCalendar XML gregorian calendar nstance
     * @return {@link LocalDateTime} or null
     */
    private static List<ArrayValue<LocalDateTime>> fromTimestampArrayValue(List<XMLGregorianCalendar> jaxbCalendar) {

        if (Objects.isNull(jaxbCalendar) || jaxbCalendar.isEmpty()) {
            return Collections.emptyList();
        }

        return jaxbCalendar.stream()
            .filter(Objects::nonNull)
            .map(el -> new TimestampArrayValue(LocalDateTime.of(el.getYear(), el.getMonth(), el.getDay(),
                el.getHour(), el.getMinute(), el.getSecond(),
                (int) TimeUnit.NANOSECONDS.convert(el.getMillisecond(), TimeUnit.MILLISECONDS))))
            .collect(Collectors.toList());
    }
}

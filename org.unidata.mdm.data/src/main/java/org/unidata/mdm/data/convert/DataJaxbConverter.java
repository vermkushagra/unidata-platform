package org.unidata.mdm.data.convert;

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
import org.unidata.mdm.core.type.data.ArrayValue;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.Attribute.AttributeType;
import org.unidata.mdm.core.type.data.BinaryLargeValue;
import org.unidata.mdm.core.type.data.CharacterLargeValue;
import org.unidata.mdm.core.type.data.CodeLinkValue;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.InfoSection;
import org.unidata.mdm.core.type.data.impl.BinaryLargeValueImpl;
import org.unidata.mdm.core.type.data.impl.BlobSimpleAttributeImpl;
import org.unidata.mdm.core.type.data.impl.BooleanSimpleAttributeImpl;
import org.unidata.mdm.core.type.data.impl.CharacterLargeValueImpl;
import org.unidata.mdm.core.type.data.impl.ClobSimpleAttributeImpl;
import org.unidata.mdm.core.type.data.impl.ComplexAttributeImpl;
import org.unidata.mdm.core.type.data.impl.DateArrayAttributeImpl;
import org.unidata.mdm.core.type.data.impl.DateArrayValue;
import org.unidata.mdm.core.type.data.impl.DateSimpleAttributeImpl;
import org.unidata.mdm.core.type.data.impl.IntegerArrayAttributeImpl;
import org.unidata.mdm.core.type.data.impl.IntegerArrayValue;
import org.unidata.mdm.core.type.data.impl.IntegerCodeAttributeImpl;
import org.unidata.mdm.core.type.data.impl.IntegerSimpleAttributeImpl;
import org.unidata.mdm.core.type.data.impl.MeasuredSimpleAttributeImpl;
import org.unidata.mdm.core.type.data.impl.NumberArrayAttributeImpl;
import org.unidata.mdm.core.type.data.impl.NumberArrayValue;
import org.unidata.mdm.core.type.data.impl.NumberSimpleAttributeImpl;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.core.type.data.impl.StringArrayAttributeImpl;
import org.unidata.mdm.core.type.data.impl.StringArrayValue;
import org.unidata.mdm.core.type.data.impl.StringCodeAttributeImpl;
import org.unidata.mdm.core.type.data.impl.StringSimpleAttributeImpl;
import org.unidata.mdm.core.type.data.impl.TimeArrayAttributeImpl;
import org.unidata.mdm.core.type.data.impl.TimeArrayValue;
import org.unidata.mdm.core.type.data.impl.TimeSimpleAttributeImpl;
import org.unidata.mdm.core.type.data.impl.TimestampArrayAttributeImpl;
import org.unidata.mdm.core.type.data.impl.TimestampArrayValue;
import org.unidata.mdm.core.type.data.impl.TimestampSimpleAttributeImpl;
import org.unidata.mdm.core.type.keys.ReferenceAliasKey;
import org.unidata.mdm.data.ApprovalState;
import org.unidata.mdm.data.ArrayAttribute;
import org.unidata.mdm.data.ArrayDataType;
import org.unidata.mdm.data.BlobValue;
import org.unidata.mdm.data.ClobValue;
import org.unidata.mdm.data.CodeAttribute;
import org.unidata.mdm.data.ComplexAttribute;
import org.unidata.mdm.data.EtalonRecord;
import org.unidata.mdm.data.EtalonRecordInfoSection;
import org.unidata.mdm.data.ExternalSourceId;
import org.unidata.mdm.data.IntegralRecord;
import org.unidata.mdm.data.MeasuredValue;
import org.unidata.mdm.data.NestedRecord;
import org.unidata.mdm.data.OriginRecord;
import org.unidata.mdm.data.OriginRecordInfoSection;
import org.unidata.mdm.data.RecordStatus;
import org.unidata.mdm.data.RelationBase;
import org.unidata.mdm.data.RelationTo;
import org.unidata.mdm.data.SimpleAttribute;
import org.unidata.mdm.data.type.data.AbstractRelationInfoSection;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.EtalonRelationInfoSection;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.OriginRelationInfoSection;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.data.util.DataJaxbUtils;

/**
 * JAXB to {@linkplain DataRecord} converter.
 * @author Mikhail Mikhailov on Oct 21, 2019
 */
public class DataJaxbConverter {
    /**
     * Constructor.
     */
    protected DataJaxbConverter() {
        super();
    }

    public static RecordEtalonKey from(org.unidata.mdm.data.EtalonKey soapKey) {
        if (Objects.isNull(soapKey)) {
            return null;
        }

        return RecordEtalonKey.builder().id(soapKey.getId()).build();
    }

    public static RecordOriginKey from(org.unidata.mdm.data.OriginKey soapKey) {
        if (Objects.isNull(soapKey)) {
            return null;
        }

        return RecordOriginKey.builder()
                .id(soapKey.getId())
                .entityName(soapKey.getEntityName())
                .externalId(soapKey.getExternalId())
                .sourceSystem(soapKey.getSourceSystem())
                .build();
    }

    public static ReferenceAliasKey from(org.unidata.mdm.api.ReferenceAliasKey soapKey) {
        if (Objects.isNull(soapKey)) {
            return null;
        }

        return ReferenceAliasKey.builder()
                .entityAttributeName(soapKey.getEntityAttributeName())
                .value(soapKey.getValue())
                .build();
    }

    public static org.unidata.mdm.data.EtalonKey to(RecordEtalonKey systemKey) {
        if (Objects.isNull(systemKey)) {
            return null;
        }

        return DataJaxbUtils.getDataObjectFactory().createEtalonKey().withId(systemKey.getId());
    }

    public static org.unidata.mdm.data.OriginKey to(RecordOriginKey systemKey) {
        if (Objects.isNull(systemKey)) {
            return null;
        }

        return DataJaxbUtils.getDataObjectFactory().createOriginKey()
                .withId(systemKey.getId())
                .withEntityName(systemKey.getEntityName())
                .withExternalId(systemKey.getExternalId())
                .withSourceSystem(systemKey.getSourceSystem());
    }

    public static SimpleAttribute to(org.unidata.mdm.core.type.data.SimpleAttribute<?> attr, Map<String, List<ExternalSourceId>> externalSourceIds) {

        if (Objects.isNull(attr)) {
            return null;
        }

        SimpleAttribute sa = DataJaxbUtils.getDataObjectFactory().createSimpleAttribute()
                .withName(attr.getName());

        switch (attr.getDataType()) {
        case BLOB:
            BlobValue blob = null;
            if (attr.getValue() != null) {
                BinaryLargeValue blv = attr.castValue();
                blob = DataJaxbUtils.getDataObjectFactory().createBlobValue()
                        .withFileName(blv.getFileName())
                        .withId(blv.getId())
                        .withMimeType(blv.getMimeType())
                        .withSize(blv.getSize());
            }
            sa.setBlobValue(DataJaxbUtils.getDataObjectFactory(), blob);
            break;
        case BOOLEAN:
            sa.setBoolValue(DataJaxbUtils.getDataObjectFactory(), attr.castValue());
            break;
        case CLOB:
            ClobValue clob = null;
            if (attr.getValue() != null) {
                CharacterLargeValue clv = attr.castValue();
                clob = DataJaxbUtils.getDataObjectFactory().createClobValue()
                        .withFileName(clv.getFileName())
                        .withId(clv.getId())
                        .withMimeType(clv.getMimeType())
                        .withSize(clv.getSize());
            }
            sa.setClobValue(DataJaxbUtils.getDataObjectFactory(), clob);
            break;
        case DATE:
            XMLGregorianCalendar dateVal = null;
            if (attr.getValue() != null) {
                LocalDate attrVal = attr.castValue();
                dateVal = DataJaxbUtils.getDatatypeFactory().newXMLGregorianCalendarDate(
                        attrVal.getYear(), attrVal.getMonthValue(), attrVal.getDayOfMonth(),
                            DatatypeConstants.FIELD_UNDEFINED);
            }
            sa.setDateValue(DataJaxbUtils.getDataObjectFactory(), dateVal);
            break;
        case LINK:
        case ENUM:
        case STRING:
            sa.setStringValue(DataJaxbUtils.getDataObjectFactory(), attr.castValue());
            break;
        case INTEGER:
            sa.setIntValue(DataJaxbUtils.getDataObjectFactory(), attr.castValue());
            break;
        case NUMBER:
            sa.setNumberValue(DataJaxbUtils.getDataObjectFactory(), attr.castValue());
            break;
        case MEASURED:
            MeasuredSimpleAttributeImpl number = (MeasuredSimpleAttributeImpl) attr;
            Double value = number.getInitialValue();
            sa.setMeasuredValue(DataJaxbUtils.getDataObjectFactory(),
                    DataJaxbUtils.getDataObjectFactory().createMeasuredValue()
                             .withValue(value)
                             .withMeasurementValueId(number.getValueId())
                             .withMeasurementUnitId(number.getInitialUnitId()));
            break;
        case TIME:
            XMLGregorianCalendar timeVal = null;
            if (attr.getValue() != null) {
                LocalTime attrVal = attr.castValue();
                timeVal = DataJaxbUtils.getDatatypeFactory().newXMLGregorianCalendarTime(
                    attrVal.getHour(), attrVal.getMinute(), attrVal.getSecond(),
                    (int) TimeUnit.MILLISECONDS.convert(attrVal.getNano(), TimeUnit.NANOSECONDS),
                        DatatypeConstants.FIELD_UNDEFINED);
            }
            sa.setTimeValue(DataJaxbUtils.getDataObjectFactory(), timeVal);
            break;
        case TIMESTAMP:
            XMLGregorianCalendar tsVal = null;
            if (attr.getValue() != null) {
                LocalDateTime attrVal = attr.castValue();
                tsVal = DataJaxbUtils.getDatatypeFactory().newXMLGregorianCalendar(
                        attrVal.getYear(), attrVal.getMonthValue(), attrVal.getDayOfMonth(),
                        attrVal.getHour(), attrVal.getMinute(), attrVal.getSecond(),
                        (int) TimeUnit.MILLISECONDS.convert(attrVal.getNano(), TimeUnit.NANOSECONDS),
                            DatatypeConstants.FIELD_UNDEFINED);
            }
            sa.setTimestampValue(DataJaxbUtils.getDataObjectFactory(), tsVal);
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

    public static CodeAttribute to(org.unidata.mdm.core.type.data.CodeAttribute<?> attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        CodeAttribute ca = DataJaxbUtils.getDataObjectFactory().createCodeAttribute()
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
    public static ArrayAttribute to(org.unidata.mdm.core.type.data.ArrayAttribute<?> attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        ArrayAttribute aa = DataJaxbUtils.getDataObjectFactory().createArrayAttribute()
                .withName(attr.getName());

        // UN-6625, UN-7242
        // Since nulls are not allowed as array values,
        // a null singleton is used as an empty indicator here
        switch (attr.getDataType()) {
        case DATE:
            List<XMLGregorianCalendar> dateVal = null;
            if (!attr.isEmpty()) {
                org.unidata.mdm.core.type.data.ArrayAttribute<LocalDate> dateValues
                    = (org.unidata.mdm.core.type.data.ArrayAttribute<LocalDate>) attr;

                dateVal = new ArrayList<>(dateValues.getValue().size());
                for (ArrayValue<LocalDate> attrVal : dateValues) {
                    XMLGregorianCalendar converted
                        = DataJaxbUtils.getDatatypeFactory().newXMLGregorianCalendarDate(
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
                org.unidata.mdm.core.type.data.ArrayAttribute<LocalTime> timeValues
                    = (org.unidata.mdm.core.type.data.ArrayAttribute<LocalTime>) attr;

                timeVal = new ArrayList<>(timeValues.getValue().size());
                for (ArrayValue<LocalTime> attrVal : timeValues) {
                    XMLGregorianCalendar converted = DataJaxbUtils.getDatatypeFactory().newXMLGregorianCalendarTime(
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
                org.unidata.mdm.core.type.data.ArrayAttribute<LocalDateTime> tsValues
                    = (org.unidata.mdm.core.type.data.ArrayAttribute<LocalDateTime>) attr;

                tsVal= new ArrayList<>(tsValues.getValue().size());
                for (ArrayValue<LocalDateTime> attrVal : tsValues) {
                    XMLGregorianCalendar converted = DataJaxbUtils.getDatatypeFactory().newXMLGregorianCalendar(
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
            org.unidata.mdm.core.type.data.ArrayAttribute<String> stringValues
                = (org.unidata.mdm.core.type.data.ArrayAttribute<String>) attr;

            aa.withStringValue(stringValues.isEmpty() ? new String[]{null} : stringValues.toArray(String[].class));
            aa.setType(ArrayDataType.STRING);
            break;
        case INTEGER:
            org.unidata.mdm.core.type.data.ArrayAttribute<Long> intValues
                = (org.unidata.mdm.core.type.data.ArrayAttribute<Long>) attr;

            aa.withIntValue(intValues.isEmpty() ? new Long[]{null} : intValues.toArray(Long[].class));
            aa.setType(ArrayDataType.INTEGER);
            break;
        case NUMBER:
            org.unidata.mdm.core.type.data.ArrayAttribute<Double> numberValues
                = (org.unidata.mdm.core.type.data.ArrayAttribute<Double>) attr;

            aa.withNumberValue(numberValues.isEmpty() ? new Double[]{null} : numberValues.toArray(Double[].class));
            aa.setType(ArrayDataType.NUMBER);
            break;
        default:
            break;
        }

        return aa;
    }

    public static ComplexAttribute to(org.unidata.mdm.core.type.data.ComplexAttribute attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        ComplexAttribute val = DataJaxbUtils.getDataObjectFactory().createComplexAttribute();
        val.setName(attr.getName());

        for (DataRecord record : attr) {
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
                simple.add(to((org.unidata.mdm.core.type.data.SimpleAttribute<?>) attr, Collections.emptyMap()));
            } else if (attr.getAttributeType() == AttributeType.CODE) {
                code.add(to((org.unidata.mdm.core.type.data.CodeAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.ARRAY) {
                array.add(to((org.unidata.mdm.core.type.data.ArrayAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.COMPLEX) {
                complex.add(to((org.unidata.mdm.core.type.data.ComplexAttribute) attr));
            }
        }

        NestedRecord val = DataJaxbUtils.getDataObjectFactory().createNestedRecord();
        val.withSimpleAttributes(simple)
           .withCodeAttributes(code)
           .withArrayAttributes(array)
           .withComplexAttributes(complex);

        return val;
    }

    public static OriginRecordInfoSection to(org.unidata.mdm.data.type.data.OriginRecordInfoSection is) {
        if (is == null) {
            return null;
        }

        return DataJaxbUtils.getDataObjectFactory().createOriginRecordInfoSection()
                .withApproval(ApprovalState.valueOf(is.getApproval().name()))
                .withCreateDate(DataJaxbUtils.dateToXMGregorianCalendar(is.getCreateDate()))
                .withCreatedBy(is.getCreatedBy())
                .withEntityName(is.getOriginKey().getEntityName())
                .withRangeFrom(DataJaxbUtils.dateToXMGregorianCalendar(is.getValidFrom()))
                .withRangeTo(DataJaxbUtils.dateToXMGregorianCalendar(is.getValidTo()))
                .withRevision(is.getRevision())
                .withStatus(RecordStatus.valueOf(is.getStatus().name()))
                .withUpdateDate(DataJaxbUtils.dateToXMGregorianCalendar(is.getUpdateDate()))
                .withUpdatedBy(is.getUpdatedBy());
    }

    public static EtalonRecordInfoSection to(org.unidata.mdm.data.type.data.EtalonRecordInfoSection is) {
        if (is == null) {
            return null;
        }

        return DataJaxbUtils.getDataObjectFactory().createEtalonRecordInfoSection()
                .withApproval(ApprovalState.valueOf(is.getApproval().name()))
                .withCreateDate(DataJaxbUtils.dateToXMGregorianCalendar(is.getCreateDate()))
                .withCreatedBy(is.getCreatedBy())
                .withEntityName(is.getEntityName())
                .withRangeFrom(DataJaxbUtils.dateToXMGregorianCalendar(is.getValidFrom()))
                .withRangeTo(DataJaxbUtils.dateToXMGregorianCalendar(is.getValidTo()))
                .withStatus(RecordStatus.valueOf(is.getStatus().name()))
                .withUpdateDate(DataJaxbUtils.dateToXMGregorianCalendar(is.getUpdateDate()))
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
                simple.add(to((org.unidata.mdm.core.type.data.SimpleAttribute<?>) attr, externalSourceIds));
            } else if (attr.getAttributeType() == AttributeType.CODE) {
                code.add(to((org.unidata.mdm.core.type.data.CodeAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.ARRAY) {
                array.add(to((org.unidata.mdm.core.type.data.ArrayAttribute<?>) attr));
            } else if (attr.getAttributeType() == AttributeType.COMPLEX) {
                complex.add(to((org.unidata.mdm.core.type.data.ComplexAttribute) attr));
            }
        }

        if (klass == RelationTo.class) {
            RelationTo relation = DataJaxbUtils.getDataObjectFactory().createRelationTo();
            relation.getSimpleAttributes().addAll(simple);
            relation.getComplexAttributes().addAll(complex);
            return (T) relation;
        } else if (klass == IntegralRecord.class) {
            IntegralRecord relation = DataJaxbUtils.getDataObjectFactory().createIntegralRecord();
            relation.withRelName(((AbstractRelationInfoSection) infoSection).getRelationName());
            relation.withEtalonRecord(EtalonRelation.class.isInstance(data)
                    ? DataJaxbUtils.getDataObjectFactory().createEtalonRecord()
                        .withSimpleAttributes(simple)
                        .withCodeAttributes(code)
                        .withArrayAttributes(array)
                        .withComplexAttributes(complex)
                        .withEtalonKey(to(((EtalonRelationInfoSection) infoSection).getToEtalonKey()))
                    : null);

            relation.withOriginRecord(OriginRelation.class.isInstance(data)
                    ? DataJaxbUtils.getDataObjectFactory().createOriginRecord()
                        .withSimpleAttributes(simple)
                        .withCodeAttributes(code)
                        .withArrayAttributes(array)
                        .withComplexAttributes(complex)
                        .withOriginKey(to(((OriginRelationInfoSection) infoSection).getRelationOriginKey().getTo()))
                    : null);

            return (T) relation;
        } else if (klass == OriginRecord.class) {
            OriginRecord origin = DataJaxbUtils.getDataObjectFactory().createOriginRecord();
            origin.getSimpleAttributes().addAll(simple);
            origin.getArrayAttributes().addAll(array);
            origin.getCodeAttributes().addAll(code);
            origin.getComplexAttributes().addAll(complex);

            if (infoSection != null) {
                origin.setOriginKey(to(((org.unidata.mdm.data.type.data.OriginRecordInfoSection) infoSection).getOriginKey()));
                origin.setInfoSection(to((org.unidata.mdm.data.type.data.OriginRecordInfoSection) infoSection));
            }

            return (T) origin;
        } else if (klass == EtalonRecord.class) {
            EtalonRecord etalon = DataJaxbUtils.getDataObjectFactory().createEtalonRecord();
            etalon.getSimpleAttributes().addAll(simple);
            etalon.getArrayAttributes().addAll(array);
            etalon.getCodeAttributes().addAll(code);
            etalon.getComplexAttributes().addAll(complex);

            if (infoSection != null) {
                etalon.setEtalonKey(to(((org.unidata.mdm.data.type.data.EtalonRecordInfoSection) infoSection).getEtalonKey()));
                etalon.setInfoSection(to((org.unidata.mdm.data.type.data.EtalonRecordInfoSection) infoSection));
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
    public static Attribute fromComplexAttribute(ComplexAttribute jaxbAttr) {

        if (Objects.isNull(jaxbAttr)) {
            return null;
        }

        ComplexAttributeImpl complex = new ComplexAttributeImpl(jaxbAttr.getName());
        jaxbAttr.getNestedRecord().forEach(nr -> complex.add(from(nr)));

        return complex;
    }

    /**
     * Converts code attribute.
     * @param jaxbAttr JAXB attribute
     * @return attribute
     */
    public static Attribute fromCodeAttribute(CodeAttribute jaxbAttr) {

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
    public static Attribute fromArrayAttribute(ArrayAttribute jaxbAttr) {

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
    public static Attribute fromSimpleAttribute(SimpleAttribute jaxbAttr) {

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
    public static BinaryLargeValue fromBlobValue(BlobValue jaxbBlobValue) {

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
    public static CharacterLargeValue fromClobValue(ClobValue jaxbClobValue) {

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
    public static LocalDate fromDateValue(XMLGregorianCalendar jaxbCalendar) {

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
    public static LocalTime fromTimeValue(XMLGregorianCalendar jaxbCalendar) {

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
    public static LocalDateTime fromTimestampValue(XMLGregorianCalendar jaxbCalendar) {

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
    public static List<ArrayValue<LocalDate>> fromDateArrayValue(List<XMLGregorianCalendar> jaxbCalendar) {

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
    public static List<ArrayValue<LocalTime>> fromTimeArrayValue(List<XMLGregorianCalendar> jaxbCalendar) {

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
    public static List<ArrayValue<LocalDateTime>> fromTimestampArrayValue(List<XMLGregorianCalendar> jaxbCalendar) {

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

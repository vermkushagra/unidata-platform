package com.unidata.mdm.backend.common.record;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.AttributeIterator;
import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeLinkValue;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BlobSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.ClobSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.ComplexAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DateArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DateArrayValue;
import com.unidata.mdm.backend.common.types.impl.DateSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.EnumSimpleAttributeImpl;
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

/**
 * @author Mikhail Mikhailov
 * Data view - simple and complex attributes.
 */
public class SerializableDataRecord implements DataRecord {

    /**
     * Internal storage, allowing O(1) key access.
     * TODO: Hash map from JDK is taken temporarilly. Looking for a memory effective replacement.
     */
    private Map<String, Attribute> map;
    /**
     * Constructor.
     * @param predictedSize number of attributes.
     */
    public SerializableDataRecord(int predictedSize) {
        super();
        map = new HashMap<>(predictedSize);
    }

    /**
     * Constructor.
     */
    public SerializableDataRecord() {
        this(16);
    }

    /**
     * Constructor.
     */
    public static SerializableDataRecord of(DataRecord other) {

        if (Objects.isNull(other)) {
            return null;
        }

        Collection<Attribute> attrs = other.getAllAttributes();
        SerializableDataRecord record = new SerializableDataRecord(attrs.size());
        for (Attribute attr : attrs) {
            switch (attr.getAttributeType()) {
            case SIMPLE:
                record.addAttribute(of((SimpleAttribute<?>) attr));
                break;
            case ARRAY:
                record.addAttribute(of((ArrayAttribute<?>) attr));
                break;
            case CODE:
                record.addAttribute(of((CodeAttribute<?>) attr));
                break;
            case COMPLEX:
                record.addAttribute(of((ComplexAttribute) attr));
                break;
            }
        }

        return record;
    }

    /**
     * Copies simple attribute.
     * @param attr the attribute to copy
     * @return new attribute
     */
    public static ComplexAttribute of(ComplexAttribute attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        ComplexAttributeImpl result = new ComplexAttributeImpl(attr.getName());
        for (DataRecord dr : attr.getRecords()) {
            result.getRecords().add(of(dr));
        }

        return result;
    }

    /**
     * Copies simple attribute.
     * @param attr the attribute to copy
     * @return new attribute
     */
    public static SimpleAttribute<?> of(SimpleAttribute<?> attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        SimpleAttribute<?> result;
        switch (attr.getDataType()) {
        case BLOB:
            result = new BlobSimpleAttributeImpl(attr.getName(), attr.castValue());
            break;
        case BOOLEAN:
            result = new BooleanSimpleAttributeImpl(attr.getName(), attr.castValue());
            break;
        case CLOB:
            result = new ClobSimpleAttributeImpl(attr.getName(), attr.castValue());
            break;
        case DATE:
            result = new DateSimpleAttributeImpl(attr.getName(), attr.castValue());
            break;
        case INTEGER:
            result = new IntegerSimpleAttributeImpl(attr.getName(), attr.castValue());
            break;
        case NUMBER:
            result = new NumberSimpleAttributeImpl(attr.getName(), attr.castValue());
            break;
        case MEASURED:
            MeasuredSimpleAttributeImpl measuredSimpleAttribute = (MeasuredSimpleAttributeImpl) attr;
            result = new MeasuredSimpleAttributeImpl(attr.getName(), attr.castValue())
                    .withInitialUnitId(measuredSimpleAttribute.getInitialUnitId())
                    .withValueId(measuredSimpleAttribute.getValueId());
            break;
        case STRING:
            result = new StringSimpleAttributeImpl(attr.getName(), attr.castValue());
            ((StringSimpleAttributeImpl) result).setLinkEtalonId(((CodeLinkValue) attr).getLinkEtalonId());
            break;
        case TIME:
            result = new TimeSimpleAttributeImpl(attr.getName(), attr.castValue());
            break;
        case TIMESTAMP:
            result = new TimestampSimpleAttributeImpl(attr.getName(), attr.castValue());
            break;
        case ENUM:
            result = new EnumSimpleAttributeImpl(attr.getName(), attr.castValue());
            break;
        default:
            return null;
        }

        ((AbstractSimpleAttribute<?>) result).setDisplayValue(attr.getDisplayValue());
        return result;
    }

    /**
     * Copies array attribute.
     * @param attr the attribute to copy
     * @return cloned attribute
     */
    public static ArrayAttribute<?> of (ArrayAttribute<?> attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        ArrayAttribute<?> result;
        switch (attr.getDataType()) {
        case DATE:
            result = new DateArrayAttributeImpl(attr.getName());
            ((DateArrayAttributeImpl) result).setValue(attr.isEmpty()
                    ? null
                    : attr.getValue().stream().map(v -> new DateArrayValue((LocalDate) v.getValue())).collect(Collectors.toList()));
            break;
        case INTEGER:
            result = new IntegerArrayAttributeImpl(attr.getName());
            ((IntegerArrayAttributeImpl) result).setValue(attr.isEmpty()
                    ? null
                    : attr.getValue().stream().map(v -> new IntegerArrayValue((Long) v.getValue())).collect(Collectors.toList()));
            break;
        case NUMBER:
            result = new NumberArrayAttributeImpl(attr.getName());
            ((NumberArrayAttributeImpl) result).setValue(attr.isEmpty()
                    ? null
                    : attr.getValue().stream().map(v -> new NumberArrayValue((Double) v.getValue())).collect(Collectors.toList()));
            break;
        case STRING:
            result = new StringArrayAttributeImpl(attr.getName());
            ((StringArrayAttributeImpl) result).setValue(attr.isEmpty()
                    ? null
                    : attr.getValue().stream().map(v -> new StringArrayValue((String) v.getValue())).collect(Collectors.toList()));
            break;
        case TIME:
            result = new TimeArrayAttributeImpl(attr.getName());
            ((TimeArrayAttributeImpl) result).setValue(attr.isEmpty()
                    ? null
                    : attr.getValue().stream().map(v -> new TimeArrayValue((LocalTime) v.getValue())).collect(Collectors.toList()));
            break;
        case TIMESTAMP:
            result = new TimestampArrayAttributeImpl(attr.getName());
            ((TimestampArrayAttributeImpl) result).setValue(attr.isEmpty()
                    ? null
                    : attr.getValue().stream().map(v -> new TimestampArrayValue((LocalDateTime) v.getValue())).collect(Collectors.toList()));
            break;
        default:
            return null;
        }

        return result;
    }

    /**
     * Copies code attribute.
     * @param attr the attribute
     * @return copy
     */
    public static CodeAttribute<?> of (CodeAttribute<?> attr) {

        if (Objects.isNull(attr)) {
            return null;
        }

        CodeAttribute<?> result;
        switch (attr.getDataType()) {
        case INTEGER:
            result = new IntegerCodeAttributeImpl(attr.getName(), attr.castValue());
            ((IntegerCodeAttributeImpl) result).setSupplementary(attr.hasSupplementary()
                    ? attr.getSupplementary().stream().map(v -> (Long) v).collect(Collectors.toList())
                    : null);
            break;
        case STRING:
            result = new StringCodeAttributeImpl(attr.getName(), attr.castValue());
            ((StringCodeAttributeImpl) result).setSupplementary(attr.hasSupplementary()
                    ? attr.getSupplementary().stream().map(v -> (String) v).collect(Collectors.toList())
                    : null);
            break;
        default:
            return null;
        }

        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getAttributeNames() {
        return map.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Attribute> getAllAttributes() {
        return map.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Attribute> getAllAttributesRecursive() {

        List<Attribute> collected = new ArrayList<>(map.size());
        for (Entry<String, Attribute> en : map.entrySet()) {
            if (en.getValue().getAttributeType() == AttributeType.COMPLEX) {
                collected.add(en.getValue());

                ComplexAttribute inner = (ComplexAttribute) en.getValue();
                if (inner.getRecords().isEmpty()) {
                    continue;
                }

                for (DataRecord r : inner.getRecords()) {
                    collected.addAll(r.getAllAttributesRecursive());
                }
            } else {
                collected.add(en.getValue());
            }
        }

        return collected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attribute getAttribute(String name) {
        return map.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Attribute> getAttributeRecursive(String path) {

        String[] tokens = StringUtils.split(path, '.');
        if (Objects.nonNull(tokens) && tokens.length > 0) {

            if (tokens.length == 1) {
                Attribute get = getAttribute(path);
                return get != null ? Collections.singletonList(get) : Collections.emptyList();
            }

            Attribute complex = getAttribute(tokens[0]);
            if (Objects.nonNull(complex) && complex.getAttributeType() == AttributeType.COMPLEX) {

                ComplexAttribute complexAttr = (ComplexAttribute) complex;
                List<Attribute> result = new ArrayList<>(complexAttr.getRecords().size());
                for (DataRecord nested : complexAttr.getRecords()) {
                    result.addAll(nested.getAttributeRecursive(StringUtils.join(tokens, '.', 1, tokens.length)));
                }

                return result;
            }
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeIterator attributeIterator() {
        return new AttributeIteratorImpl(map.entrySet().iterator());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<SimpleAttribute<?>> getSimpleAttributes() {
        return map.values().stream()
                .filter(a -> a.getAttributeType() == AttributeType.SIMPLE)
                .map(a -> (SimpleAttribute<?>) a)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SimpleAttribute<?>> getSimpleAttributesOrdered() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleAttribute<?> getSimpleAttribute(String name) {
        Attribute attr = getAttribute(name);
        return attr != null && attr.getAttributeType() == AttributeType.SIMPLE ? (SimpleAttribute<?>) attr : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<SimpleAttribute<?>> getSimpleAttributeRecursive(String name) {

        Collection<Attribute> attrs = getAttributeRecursive(name);
        if (!attrs.isEmpty()) {
            return attrs.stream()
                    .filter(attr -> attr.getAttributeType() == AttributeType.SIMPLE)
                    .map(attr -> (SimpleAttribute<?>) attr)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CodeAttribute<?>> getCodeAttributes() {
        return map.values().stream()
                .filter(a -> a.getAttributeType() == AttributeType.CODE)
                .map(a -> (CodeAttribute<?>) a)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CodeAttribute<?>> getCodeAttributesOrdered() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeAttribute<?> getCodeAttribute(String name) {
        Attribute attr = getAttribute(name);
        return attr != null && attr.getAttributeType() == AttributeType.CODE ? (CodeAttribute<?>) attr : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ArrayAttribute<?>> getArrayAttributes() {

        return map.values().stream()
                .filter(a -> a.getAttributeType() == AttributeType.ARRAY)
                .map(a -> (ArrayAttribute<?>) a)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ArrayAttribute<?>> getArrayAttributesOrdered() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayAttribute<?> getArrayAttribute(String name) {
        Attribute attr = getAttribute(name);
        return attr != null && attr.getAttributeType() == AttributeType.ARRAY ? (ArrayAttribute<?>) attr : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ArrayAttribute<?>> getArrayAttributeRecursive(String name) {

        Collection<Attribute> attrs = getAttributeRecursive(name);
        if (!attrs.isEmpty()) {
            return attrs.stream()
                    .filter(attr -> attr.getAttributeType() == AttributeType.ARRAY)
                    .map(attr -> (ArrayAttribute<?>) attr)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ComplexAttribute getComplexAttribute(String name) {
        Attribute attr = getAttribute(name);
        return attr != null && attr.getAttributeType() == AttributeType.COMPLEX ? (ComplexAttribute) attr : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ComplexAttribute> getComplexAttributeRecursive(String name) {

        Collection<Attribute> attrs = getAttributeRecursive(name);
        if (!attrs.isEmpty()) {
            return attrs.stream()
                    .filter(attr -> attr.getAttributeType() == AttributeType.COMPLEX)
                    .map(attr -> (ComplexAttribute) attr)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ComplexAttribute> getComplexAttributes() {
        return map.values().stream()
                .filter(a -> a.getAttributeType() == AttributeType.COMPLEX)
                .map(a -> (ComplexAttribute) a)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ComplexAttribute> getComplexAttributesOrdered() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAll(Collection<? extends Attribute> attributes) {
        for (Attribute attribute : attributes) {
            addAttribute(attribute);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAttribute(Attribute attribute) {
        if (Objects.isNull(attribute)) {
            return;
        }

        map.put(attribute.getName(), attribute);
        attribute.setRecord(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAttributeRecursive(String path, Attribute attribute) {

        String[] tokens = StringUtils.split(path, '.');
        if (Objects.nonNull(tokens) && tokens.length > 0) {

            if (tokens.length == 1) {
                addAttribute(attribute);
            } else {

                if (Objects.isNull(attribute)) {
                    return;
                }

                if (StringUtils.equals(tokens[tokens.length - 1], attribute.getName())) {
                    tokens = Arrays.copyOf(tokens, tokens.length - 1);
                }

                int level = 0;
                while (level < tokens.length) {
                    //getAttribute(name)
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addAllRecursive(Map<String, Attribute> attributes) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String putAttribute(String name, String value) {

        String oldValue = null;
        Attribute existing = map.get(name);
        if (Objects.isNull(existing)) {
            addAttribute(new StringSimpleAttributeImpl(name, value));
        } else {
            if (existing.getAttributeType() != AttributeType.SIMPLE) {
                final String message = "Put string simple attribute '{}': Attribute exists and is not simple.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_STRING_NOT_SIMPLE,
                        name);
            } else if (((SimpleAttribute<?>) existing).getDataType() != DataType.STRING) {
                final String message = "Put string simple attribute '{}': Attribute exists and is not string.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_STRING_NOT_STRING,
                        name);
            }

            StringSimpleAttributeImpl cast = (StringSimpleAttributeImpl) existing;
            oldValue = cast.getValue();
            cast.setValue(value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long putAttribute(String name, Long value) {

        Long oldValue = null;
        Attribute existing = map.get(name);
        if (Objects.isNull(existing)) {
            addAttribute(new IntegerSimpleAttributeImpl(name, value));
        } else {
            if (existing.getAttributeType() != AttributeType.SIMPLE) {
                final String message = "Put integer simple attribute '{}': Attribute exists and is not simple.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_INT_NOT_SIMPLE,
                        name);
            } else if (((SimpleAttribute<?>) existing).getDataType() != DataType.INTEGER) {
                final String message = "Put integer simple attribute '{}': Attribute exists and is not integer.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_INT_NOT_INT,
                        name);
            }

            IntegerSimpleAttributeImpl cast = (IntegerSimpleAttributeImpl) existing;
            oldValue = cast.getValue();
            cast.setValue(value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double putAttribute(String name, Double value) {

        Double oldValue = null;
        Attribute existing = map.get(name);
        if (Objects.isNull(existing)) {
            addAttribute(new NumberSimpleAttributeImpl(name, value));
        } else {
            if (existing.getAttributeType() != AttributeType.SIMPLE) {
                final String message = "Put numeric simple attribute '{}': Attribute exists and is not simple.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_NUM_NOT_SIMPLE,
                        name);
            } else if (((SimpleAttribute<?>) existing).getDataType() != DataType.NUMBER) {
                final String message = "Put numeric simple attribute '{}': Attribute exists and is not numeric.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_NUM_NOT_NUM,
                        name);
            }

            NumberSimpleAttributeImpl cast = (NumberSimpleAttributeImpl) existing;
            oldValue = cast.getValue();
            cast.setValue(value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean putAttribute(String name, Boolean value) {

        Boolean oldValue = null;
        Attribute existing = map.get(name);
        if (Objects.isNull(existing)) {
            addAttribute(new BooleanSimpleAttributeImpl(name, value));
        } else {
            if (existing.getAttributeType() != AttributeType.SIMPLE) {
                final String message = "Put boolean simple attribute '{}': Attribute exists and is not simple.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_BOOL_NOT_SIMPLE,
                        name);
            } else if (((SimpleAttribute<?>) existing).getDataType() != DataType.BOOLEAN) {
                final String message = "Put boolean simple attribute '{}': Attribute exists and is not boolean.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_BOOL_NOT_BOOL,
                        name);
            }

            BooleanSimpleAttributeImpl cast = (BooleanSimpleAttributeImpl) existing;
            oldValue = cast.getValue();
            cast.setValue(value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate putAttribute(String name, LocalDate value) {

        LocalDate oldValue = null;
        Attribute existing = map.get(name);
        if (Objects.isNull(existing)) {
            addAttribute(new DateSimpleAttributeImpl(name, value));
        } else {
            if (existing.getAttributeType() != AttributeType.SIMPLE) {
                final String message = "Put date simple attribute '{}': Attribute exists and is not simple.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_DATE_NOT_SIMPLE,
                        name);
            } else if (((SimpleAttribute<?>) existing).getDataType() != DataType.DATE) {
                final String message = "Put date simple attribute '{}': Attribute exists and is not date.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_DATE_NOT_DATE,
                        name);
            }

            DateSimpleAttributeImpl cast = (DateSimpleAttributeImpl) existing;
            oldValue = cast.getValue();
            cast.setValue(value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalTime putAttribute(String name, LocalTime value) {

        LocalTime oldValue = null;
        Attribute existing = map.get(name);
        if (Objects.isNull(existing)) {
            addAttribute(new TimeSimpleAttributeImpl(name, value));
        } else {
            if (existing.getAttributeType() != AttributeType.SIMPLE) {
                final String message = "Put time simple attribute '{}': Attribute exists and is not simple.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_TIME_NOT_SIMPLE,
                        name);
            } else if (((SimpleAttribute<?>) existing).getDataType() != DataType.TIME) {
                final String message = "Put time simple attribute '{}': Attribute exists and is not time.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_TIME_NOT_TIME,
                        name);
            }

            TimeSimpleAttributeImpl cast = (TimeSimpleAttributeImpl) existing;
            oldValue = cast.getValue();
            cast.setValue(value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime putAttribute(String name, LocalDateTime value) {

        LocalDateTime oldValue = null;
        Attribute existing = map.get(name);
        if (Objects.isNull(existing)) {
            addAttribute(new TimestampSimpleAttributeImpl(name, value));
        } else {
            if (existing.getAttributeType() != AttributeType.SIMPLE) {
                final String message = "Put timestamp simple attribute '{}': Attribute exists and is not simple.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_TIMESTAMP_NOT_SIMPLE,
                        name);
            } else if (((SimpleAttribute<?>) existing).getDataType() != DataType.TIMESTAMP) {
                final String message = "Put timestamp simple attribute '{}': Attribute exists and is not timestamp.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_TIMESTAMP_NOT_TIMESTAMP,
                        name);
            }

            TimestampSimpleAttributeImpl cast = (TimestampSimpleAttributeImpl) existing;
            oldValue = cast.getValue();
            cast.setValue(value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryLargeValue putAttribute(String name, BinaryLargeValue value) {

        BinaryLargeValue oldValue = null;
        Attribute existing = map.get(name);
        if (Objects.isNull(existing)) {
            addAttribute(new BlobSimpleAttributeImpl(name, value));
        } else {
            if (existing.getAttributeType() != AttributeType.SIMPLE) {
                final String message = "Put BLOB simple attribute '{}': Attribute exists and is not simple.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_BLOB_NOT_SIMPLE,
                        name);
            } else if (((SimpleAttribute<?>) existing).getDataType() != DataType.BLOB) {
                final String message = "Put BLOB simple attribute '{}': Attribute exists and is not BLOB.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_BLOB_NOT_BLOB,
                        name);
            }

            BlobSimpleAttributeImpl cast = (BlobSimpleAttributeImpl) existing;
            oldValue = cast.getValue();
            cast.setValue(value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharacterLargeValue putAttribute(String name, CharacterLargeValue value) {

        CharacterLargeValue oldValue = null;
        Attribute existing = map.get(name);
        if (Objects.isNull(existing)) {
            addAttribute(new ClobSimpleAttributeImpl(name, value));
        } else {
            if (existing.getAttributeType() != AttributeType.SIMPLE) {
                final String message = "Put CLOB simple attribute '{}': Attribute exists and is not simple.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_CLOB_NOT_SIMPLE,
                        name);
            } else if (((SimpleAttribute<?>) existing).getDataType() != DataType.CLOB) {
                final String message = "Put CLOB simple attribute '{}': Attribute exists and is not CLOB.";
                throw new DataProcessingException(message,
                        ExceptionId.EX_DATA_ATTRIBUTE_PUT_CLOB_NOT_CLOB,
                        name);
            }

            ClobSimpleAttributeImpl cast = (ClobSimpleAttributeImpl) existing;
            oldValue = cast.getValue();
            cast.setValue(value);
        }

        return oldValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAttribute(String name) {
        return map.containsKey(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attribute removeAttribute(String name) {

        Attribute existing = map.remove(name);
        if (Objects.nonNull(existing)) {
            existing.setRecord(null);
        }

        return existing;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Attribute> removeAttributeRecursive(String path) {

        String[] tokens = StringUtils.split(path, '.');
        if (Objects.nonNull(tokens) && tokens.length > 0) {

            if (tokens.length == 1) {
                Attribute removed = removeAttribute(path);
                return removed != null ? Collections.singletonList(removed) : Collections.emptyList();
            }


            if (tokens.length > 0) {

                Attribute complex = getAttribute(tokens[0]);
                if (Objects.nonNull(complex) && complex.getAttributeType() == AttributeType.COMPLEX) {

                    ComplexAttribute complexAttr = ((ComplexAttribute) complex);
                    List<Attribute> result = new ArrayList<>(complexAttr.getRecords().size());
                    for (DataRecord nested : complexAttr.getRecords()) {
                        result.addAll(nested.removeAttributeRecursive(StringUtils.join(tokens, '.', 1, tokens.length)));
                    }

                    return result;
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return map.size();
    }
}

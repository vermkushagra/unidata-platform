package org.unidata.mdm.data.convert;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.BinaryLargeValue;
import org.unidata.mdm.core.type.data.CharacterLargeValue;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.impl.DateArrayValue;
import org.unidata.mdm.core.type.data.impl.IntegerArrayValue;
import org.unidata.mdm.core.type.data.impl.NumberArrayValue;
import org.unidata.mdm.core.type.data.impl.StringArrayValue;
import org.unidata.mdm.core.type.data.impl.TimeArrayValue;
import org.unidata.mdm.core.type.data.impl.TimestampArrayValue;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.search.type.indexing.IndexingField;
import org.unidata.mdm.search.type.indexing.impl.IndexingRecordImpl;

/**
 * @author Mikhail Mikhailov on Oct 12, 2019
 * Abstract, common to all data kinds, 'DataRecord' part of the indexing support.
 */
public abstract class AbstractIndexingConverter {
    /**
     * Constructor.
     */
    protected AbstractIndexingConverter() {
        super();
    }

    /**
     * Builds JSON representation of an object for insert or update.
     *
     * @param builder the builder
     * @param record the record
     * @param recordPath current path
     * @throws IOException
     */
    protected static List<IndexingField> buildRecord(DataRecord record) {

        List<IndexingField> collected = new ArrayList<>(record.getSize());
        for (Attribute attr : record.getAllAttributes()) {
            if (attr == null) {
                continue;
            }

            IndexingField retval = buildAttribute(attr);
            if (Objects.nonNull(retval)) {
                collected.add(retval);
            }
        }

        return collected;
    }

    /**
     * Builds JSON representation of an attribute for insert or update.
     *
     * @param builder the builder
     * @param attr the attr
     * @param attrPath current path
     * @throws IOException
     */
    protected static IndexingField buildAttribute(Attribute attr) {

        IndexingField result = null;
        final String name = attr.getName();
        switch (attr.getAttributeType()) {
            case SIMPLE:

                SimpleAttribute<?> simple = (SimpleAttribute<?>) attr;
                switch (simple.getDataType()) {
                    case STRING:
                    case LINK:
                    case ENUM:
                        result = IndexingField.of(EntityIndexType.RECORD, name, simple.<String>castValue());
                        break;
                    case NUMBER:
                    case MEASURED:
                        result = IndexingField.of(EntityIndexType.RECORD, name, simple.<Double>castValue());
                        break;
                    case BOOLEAN:
                        result = IndexingField.of(EntityIndexType.RECORD, name, simple.<Boolean>castValue());
                        break;
                    case DATE:
                        result = IndexingField.of(EntityIndexType.RECORD, name, simple.<LocalDate>castValue());
                        break;
                    case TIME:
                        result = IndexingField.of(EntityIndexType.RECORD, name, simple.<LocalTime>castValue());
                        break;
                    case TIMESTAMP:
                        result = IndexingField.of(EntityIndexType.RECORD, name, simple.<LocalDateTime>castValue());
                        break;
                    case INTEGER:
                        result = IndexingField.of(EntityIndexType.RECORD, name, simple.<Long>castValue());
                        break;
                    case BLOB:
                        result = IndexingField.of(EntityIndexType.RECORD, name, simple.<BinaryLargeValue>castValue().getFileName());
                        break;
                    case CLOB:
                        result = IndexingField.of(EntityIndexType.RECORD, name, simple.<CharacterLargeValue>castValue().getFileName());
                        break;
                }
                break;
            case CODE:

                CodeAttribute<?> code = (CodeAttribute<?>) attr;
                switch (code.getDataType()) {
                case INTEGER:
                    result = IndexingField.ofIntegers(EntityIndexType.RECORD, name,
                            Stream.concat(Stream.of(code.<Long>castValue()), code.<Long>castSupplementary().stream())
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                    break;
                case STRING:
                    result = IndexingField.ofStrings(EntityIndexType.RECORD, name,
                            Stream.concat(Stream.of(code.<String>castValue()), code.<String>castSupplementary().stream())
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()));
                    break;
                default:
                    break;
                }
                break;
            case ARRAY:

                ArrayAttribute<?> array = (ArrayAttribute<?>) attr;
                if (array.isEmpty()) {
                    break;
                }

                switch (array.getDataType()) {
                    case STRING:
                        result = IndexingField.ofStrings(EntityIndexType.RECORD, name,
                                array.getValue().stream()
                                    .map(value -> ((StringArrayValue) value).getValue())
                                    .collect(Collectors.toList()));
                        break;
                    case NUMBER:
                        result = IndexingField.ofNumbers(EntityIndexType.RECORD, name,
                                array.getValue().stream()
                                    .map(value -> ((NumberArrayValue) value).getValue())
                                    .collect(Collectors.toList()));
                        break;
                    case INTEGER:
                        result = IndexingField.ofIntegers(EntityIndexType.RECORD, name,
                                array.getValue().stream()
                                    .map(value -> ((IntegerArrayValue) value).getValue())
                                    .collect(Collectors.toList()));
                        break;
                    case DATE:
                        result = IndexingField.ofDates(EntityIndexType.RECORD, name,
                                array.getValue().stream()
                                    .map(value -> ((DateArrayValue) value).getValue())
                                    .collect(Collectors.toList()));
                        break;
                    case TIME:
                        result = IndexingField.ofTimes(EntityIndexType.RECORD, name,
                                array.getValue().stream()
                                    .map(value -> ((TimeArrayValue) value).getValue())
                                    .collect(Collectors.toList()));
                        break;
                    case TIMESTAMP:
                        result = IndexingField.ofTimestamps(EntityIndexType.RECORD, name,
                                array.getValue().stream()
                                    .map(value -> ((TimestampArrayValue) value).getValue())
                                    .collect(Collectors.toList()));
                        break;
                }
                break;
            case COMPLEX:

                ComplexAttribute complexAttribute = (ComplexAttribute) attr;
                if (complexAttribute.isEmpty()) {
                    break;
                }

                result = IndexingField.ofRecords(EntityIndexType.RECORD, name,
                        complexAttribute.stream()
                            .filter(Objects::nonNull)
                            .map(AbstractIndexingConverter::buildRecord)
                            .map(IndexingRecordImpl::new)
                            .collect(Collectors.toList()));
                break;
        }

        return result;
    }

}

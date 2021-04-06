/**
 *
 */
package com.unidata.mdm.backend.exchange.chain;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.ComplexAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.RecordKeysCache;
import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.def.ComplexAttributeExpansion;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.ExchangeFieldTransformer;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.SimpleAttributeDef;

/**
 * @author Mikhail Mikhailov
 * Basic import stuff, that may be useful for various import types.
 */
public abstract class BaseTransformImportChainMember {

    protected static final int DEFAULT_BULK_SIZE = 1000;

    /**
     * This run cache.
     */
    protected static final RecordKeysCache KEYS_CACHE = new RecordKeysCache();
    /**
     * Import records logger name.
     */
    protected static final String RECORDS_LOGGER_NAME = "import-records";
    /**
     * Records logger.
     */
    protected static final Logger TRANSFORM_CHAIN_MEMBER_LOGGER = LoggerFactory.getLogger(RECORDS_LOGGER_NAME);
    /**
     * Sorts entity import definitions by import order field.
     */
    public static final Comparator<ExchangeEntity> ENTITY_IMPORT_ORDER_COMPARATOR = new Comparator<ExchangeEntity>() {
                @Override
                public int compare(ExchangeEntity o1, ExchangeEntity o2) {
                    return o1.getImportOrder() - o2.getImportOrder();
                }
            };

    /**
     * Ctor.
     */
    public BaseTransformImportChainMember() {
        super();
    }

    /**
     * Applies an {@link ExchangeFieldTransformer} to a string value
     * @param value the value to apply transformation to
     * @param t the transformer
     * @return result or null
     */
    protected String applyTransformation(String value, List<ExchangeFieldTransformer> lt) {
        for (ExchangeFieldTransformer t : lt) {
            value = t.transform(value);
        }
        return value;
    }

    /**
     * Appends transformation results.
     * @param records the records to append
     * @param relations the relations to append
     * @param ctx the context
     */
    protected void appendTransformationResult(
            Map<String, List<UpsertRequestContext>> records,
            Map<String, List<UpsertRelationsRequestContext>> relations,
            ExchangeContext ctx) {

        // 4. Set collected records.
        if (records.size() > 0) {
            Map<String, List<UpsertRequestContext>> allRecords = ctx.getFromStorage(StorageId.IMPORT_ORIGIN_RECORDS);
            for (Entry<String, List<UpsertRequestContext>> e : records.entrySet()) {
                if (allRecords.containsKey(e.getKey())) {
                    allRecords.get(e.getKey()).addAll(e.getValue());
                } else {
                    allRecords.put(e.getKey(), e.getValue());
                }
            }
        }

        // 5. Set collected relations
        if (relations.size() > 0) {
            Map<String, List<UpsertRelationsRequestContext>> allRelations = ctx.getFromStorage(StorageId.IMPORT_ORIGIN_RELATIONS);
            for (Entry<String, List<UpsertRelationsRequestContext>> e : relations.entrySet()) {
                if (allRelations.containsKey(e.getKey())) {
                    allRelations.get(e.getKey()).addAll(e.getValue());
                } else {
                    allRelations.put(e.getKey(), e.getValue());
                }
            }
        }
    }

    /**
     * Sets an attribute.
     * @param record the record to modify
     * @param field exchange field
     * @param attrs attributes tree to traverse
     * @param path the path to process
     * @param value the value to set
     * @param level current expansion level
     */
    protected void setAttribute(DataRecord record, ExchangeField field, Map<String, AttributeInfoHolder> attrs, String path, Object value, int level) {

        boolean compound = ModelUtils.isCompoundPath(path);
        if (!compound) {
            AttributeInfoHolder holder = attrs.get(path);
            if (holder.getAttribute() instanceof SimpleAttributeDef) {
                processSimpleAttribute((SimpleAttributeDef) holder.getAttribute(), record, field, value);
            } else if (holder.getAttribute() instanceof ComplexAttributeDef) {
                processComplexAttribute((ComplexAttributeDef) holder.getAttribute(), record, field, value, level);
            } else if (holder.getAttribute() instanceof CodeAttributeDef) {
                processCodeAttribute((CodeAttributeDef) holder.getAttribute(), record, value);
            }
        } else {
            String attrPath = ModelUtils.stripAttributePath(level, path);
            AttributeInfoHolder holder = attrs.get(attrPath);
            if (holder.getAttribute() instanceof ComplexAttributeDef) {
                DataRecord nested
                    = processComplexAttribute((ComplexAttributeDef) holder.getAttribute(), record, field, value, level);
                setAttribute(nested, field, attrs, path, value, level + 1);
            } else if (holder.getAttribute() instanceof SimpleAttributeDef) {
                processSimpleAttribute((SimpleAttributeDef) holder.getAttribute(), record, field, value);
            }
        }
    }

    /**
     * Processes complex attribute.
     * @param attr the attribute to process
     * @param record record to modify
     * @param field exchange field
     * @param value value to set
     * @return a nested entity, new or existing
     */
    protected DataRecord processComplexAttribute(ComplexAttributeDef attr, DataRecord record, ExchangeField field, Object value, int level) {

     // 1. Find attribute
        ComplexAttribute valueAttr = record.getComplexAttribute(attr.getName());

        // 2. Create nested attribute, if needed
        if (valueAttr == null) {
            valueAttr = new ComplexAttributeImpl(attr.getName());
            record.addAttribute(valueAttr);
        }

        DataRecord result = null;
        BigInteger minCount = attr.getMinCount() == null ? BigInteger.ZERO : attr.getMinCount();
        BigInteger maxCount = attr.getMaxCount() == null ? BigInteger.ZERO : attr.getMaxCount();
        boolean cardinalityOne = 1 == minCount.intValue() && 1 == maxCount.intValue();

        // 3. Check rules. Cardinality has precedence
        if (cardinalityOne) {
            if (1 == valueAttr.getRecords().size()) {
                result = valueAttr.getRecords().get(0);
            } else {
                valueAttr.getRecords().add(result = new SerializableDataRecord());
            }
        } else {
            // Process expansion rules
            if (0 == valueAttr.getRecords().size()) {
                valueAttr.getRecords().add(result = new SerializableDataRecord());
            } else {
                boolean expand = true;
                if (field.getExpansions() != null && field.getExpansions().size() > 0) {
                    for (ComplexAttributeExpansion exp : field.getExpansions()) {
                        if (exp.getLevel() == level) {
                            expand = exp.isExpand();
                            break;
                        }
                    }
                }

                if (expand) {
                    valueAttr.getRecords().add(result = new SerializableDataRecord());
                } else {
                    result = valueAttr.getRecords().get(valueAttr.getRecords().size() - 1);
                }
            }
        }

        return result;
    }

    /**
     * Sets simple attribute value.
     * @param attr the attribute
     * @param record the record
     * @param field exchange field
     * @param value the value to set
     */
    protected void processSimpleAttribute(SimpleAttributeDef attr, DataRecord record, ExchangeField field, Object value) {
        SimpleAttribute<?> valueAttr = record.getSimpleAttribute(attr.getName());
        if (valueAttr == null) {
            DataType type = attr.getSimpleDataType() == null ? DataType.STRING : DataType.valueOf(attr.getSimpleDataType().name());
            valueAttr = AbstractSimpleAttribute.of(type, attr.getName());
            record.addAttribute(valueAttr);
        }

        processSimpleAttributeValue(attr, valueAttr, value);
    }

    /**
     * Sets simple attribute value.
     * @param attr the attribute
     * @param record the record
     * @param value the value to set
     */
    protected void processCodeAttribute(CodeAttributeDef attr, DataRecord record, Object value) {
        SimpleAttribute<?> valueAttr = record.getSimpleAttribute(attr.getName());
        if (valueAttr == null) {
            valueAttr = AbstractSimpleAttribute.of(DataType.STRING, attr.getName());
            record.addAttribute(valueAttr);
        }

        processSimpleAttributeValue(attr, valueAttr, value);
    }

    /**
     * Sets the value to the simple attribute.
     * @param attr meta model attribute
     * @param valueAttr data simple attribute
     * @param value the value to set
     */
    protected void processSimpleAttributeValue(AbstractAttributeDef attr, SimpleAttribute<?> valueAttr, Object value) {

        if (Objects.isNull(valueAttr)) {
            // TODO log/throw
            return;
        }

        // Overwrite or set the value
        switch (valueAttr.getDataType()) {
            case BOOLEAN:
                valueAttr.castValue(toBoolean(value));
                return;
            case INTEGER:
                valueAttr.castValue(toLong(value));
                return;
            case NUMBER:
                valueAttr.castValue(toDouble(value));
                return;
            case STRING:
                valueAttr.castValue(toString(value));
                return;
            case DATE:
                valueAttr.castValue(toDate(value));
                return;
            case MEASURED:
                valueAttr.castValue(toDouble(value));
                return;
            case TIME:
                valueAttr.castValue(toTime(value));
                return;
            case TIMESTAMP:
                valueAttr.castValue(toTimestamp(value));
                return;
            case BLOB:
            case CLOB:
                // TODO
                return;
            default:
                return;
        }
    }

    /**
     * Object to boolean, if required.
     * @param o object
     * @return
     */
    protected Boolean toBoolean(Object o) {
        return o == null
                ? Boolean.FALSE
                : Boolean.class.isAssignableFrom(o.getClass())
                    ? (Boolean) o
                    : Boolean.valueOf(o.toString());
    }

    /**
     * Object to long, if required.
     * @param o object
     * @return
     */
    protected Long toLong(Object o) {
        return o == null
                ? null
                : Number.class.isAssignableFrom(o.getClass())
                    ? ((Number) o).longValue()
                    : Long.valueOf(o.toString());
    }

    /**
     * Object to boolean, if required.
     * @param o object
     * @return
     */
    protected Double toDouble(Object o) {
        return o == null
                ? null
                : Number.class.isAssignableFrom(o.getClass())
                    ? ((Number) o).doubleValue()
                    : Double.valueOf(o.toString());
    }

    /**
     * Object to boolean, if required.
     * @param o object
     * @return
     */
    protected String toString(Object o) {
        return o == null
                ? null
                : String.class.isAssignableFrom(o.getClass())
                    ? (String) o
                    : o.toString();
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    protected LocalDate toDate(Object o) {
        if (o == null) {
            return null;
        } else if (Calendar.class.isAssignableFrom(o.getClass())) {
            return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (Date.class.isAssignableFrom(o.getClass())) {
            return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (String.class.isAssignableFrom(o.getClass())) {
            return LocalDate.parse(o.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        return null;
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    protected LocalTime toTime(Object o) {
        if (o == null) {
            return null;
        } else if (Calendar.class.isAssignableFrom(o.getClass())) {
            return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        } else if (Date.class.isAssignableFrom(o.getClass())) {
            return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        } else if (String.class.isAssignableFrom(o.getClass())) {
            return LocalTime.parse(o.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        return null;
    }

    /**
     * Object to boolean, if required.
     *
     * @param o
     *            object
     * @return the XML gregorian calendar
     */
    protected LocalDateTime toTimestamp(Object o) {
        if (o == null) {
            return null;
        } else if (Calendar.class.isAssignableFrom(o.getClass())) {
            return ((Calendar) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (Date.class.isAssignableFrom(o.getClass())) {
            return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (String.class.isAssignableFrom(o.getClass())) {
            return LocalDateTime.parse(o.toString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        return null;
    }

    /**
     * Log result of work.
     * @param results
     */
    protected void processResult(Collection<Future<Result>> results){
        Map<String, Result> overAllResult = new HashMap<>();
        for (Future<Result> resultFuture : results) {
            try {
                Result result = resultFuture.get();
                if (!overAllResult.containsKey(result.getEntityName())) {
                    overAllResult.put(result.getEntityName(), new Result().setEntityName(result.getEntityName()));
                }
                Result finalResult = overAllResult.get(result.getEntityName());
                finalResult.addFailed(result.getFailed());
                finalResult.addProcessed(result.getProcessed());
                finalResult.addRejected(result.getReject());
                finalResult.addTotal(result.getTotal());
            } catch (Exception e) {
                TRANSFORM_CHAIN_MEMBER_LOGGER.warn("Failed to get result for job {}.", e);
            }
        }

        TRANSFORM_CHAIN_MEMBER_LOGGER.info("Result: {}", overAllResult);
    }
}

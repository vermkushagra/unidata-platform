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

package com.unidata.mdm.backend.service.job.exchange.in;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute.CodeDataType;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractArrayAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractCodeAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.ComplexAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.exchange.def.ComplexAttributeExpansion;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.data.util.AttributeUtils;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.meta.AbstractAttributeDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.SimpleAttributeDef;

/**
 * Responsible for mapping result set to business logic object!.
 *
 * @param <T>
 *            mapped entity
 */
public abstract class AbstractRowMapper<T> implements RowMapper<T>, Serializable {
    /** Serial version UID.*/
    private static final long serialVersionUID = 1L;
    /** Logger. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(ImportDataJobConstants.IMPORT_JOB_LOGGER_NAME);
    /**
     * Gets attribute data type according to mapping hint.
     * @param clazz the class
     * @return data type or null
     */
    protected DataType ofSimpleType(Class<?> clazz) {

        if (Objects.isNull(clazz)) {
            return null;
        }

        if (Integer.class == clazz || Long.class == clazz) {
            return DataType.INTEGER;
        } else if (Double.class == clazz || Float.class == clazz) {
            return DataType.NUMBER;
        } else if (String.class == clazz) {
            return DataType.STRING;
        } else if (Boolean.class == clazz) {
            return DataType.BOOLEAN;
        } else if (Date.class.isAssignableFrom(clazz)) {
            return DataType.DATE;
        } else if (Time.class == clazz) {
            return DataType.TIME;
        } else if (Timestamp.class == clazz) {
            return DataType.TIMESTAMP;
        }

        return null;
    }
    /**
     * Gets field simple value.
     *
     * @param fields
     *            - result set.
     * @param alias
     *            - name of table column.
     * @param clazz
     *            - class of a field.
     * @return field value
     * @throws SQLException
     *             the SQL exception
     */
    protected Object getFieldValue(ResultSet fields, String alias, Class<?> clazz) throws SQLException {

        Object result = null;
        if(fields.getObject(alias) == null){
            return result;
        }

        if (clazz == Integer.class) {
            result = fields.getInt(alias);
        } else if (clazz == Long.class) {
            result = fields.getLong(alias);
        } else if (clazz == String.class) {
            result = fields.getString(alias);
        } else if (clazz == java.sql.Date.class) {
            result = fields.getDate(alias);
        } else if (clazz == Timestamp.class) {
            result = fields.getTimestamp(alias);
        } else if (clazz == Time.class) {
            result = fields.getTime(alias);
        } else if (clazz == Float.class) {
            result = fields.getFloat(alias);
        } else if (clazz == Double.class) {
            result = fields.getDouble(alias);
        } else if (clazz == Boolean.class) {
            result = fields.getBoolean(alias);
        }

        return fields.wasNull() ? null : result;
    }
    /**
     * Gets default field class for classifier node.
     * @param classifierAttr the attr.
     * @return class or null
     */
    protected Class<?> getFieldClass(ClsfNodeAttrDTO classifierAttr) {

        if (classifierAttr == null || classifierAttr.getDataType() == null) {
            return null;
        }

        switch (classifierAttr.getDataType()) {
        case BOOLEAN:
            return Boolean.class;
        case STRING:
        case ENUM:
        case LINK:
            return String.class;
        case DATE:
            return java.sql.Date.class;
        case TIME:
            return Time.class;
        case TIMESTAMP:
            return Timestamp.class;
        case INTEGER:
            return Long.class;
        case NUMBER:
            return Double.class;
        case MEASURED:
            return Double.class;
        default:
            break;
        }

        return null;
    }
    /**
     * Gets default field class.
     *
     * @param holder
     *            the holder
     * @return class or null
     */
    protected Class<?> getFieldClass(AttributeInfoHolder holder) {

        if (holder == null) {
            return null;
        }

        AbstractAttributeDef attrDef = holder.getAttribute();
        if (holder.isComplex()) {
            throw new IllegalArgumentException("Complex attribute for class field selected.");
        } else if (holder.isCode()) {
            CodeAttributeDef codeDef = (CodeAttributeDef) attrDef;
            switch (codeDef.getSimpleDataType()) {
                case STRING:
                    return String.class;
                case INTEGER:
                    return Long.class;
                default:
                    break;
            }
        } else if (holder.isArray()) {
            return String.class;
        } else {
            SimpleAttributeDef sa = (SimpleAttributeDef) attrDef;
            if (sa.getSimpleDataType() != null) {
                // Simple DT with a value type
                switch (sa.getSimpleDataType()) {
                    case BOOLEAN:
                        return Boolean.class;
                    case STRING:
                        return String.class;
                    case DATE:
                        return java.sql.Date.class;
                    case TIME:
                        return Time.class;
                    case TIMESTAMP:
                        return Timestamp.class;
                    case INTEGER:
                        return Long.class;
                    case NUMBER:
                        return Double.class;
                    case MEASURED:
                        return Double.class;
                    default:
                        break;
                }
            } else if (StringUtils.isNotBlank(sa.getEnumDataType()) || StringUtils.isNotBlank(sa.getLinkDataType())) {
                // Enum fields are always strings
                // No opportunity to check the value, just set it
                return String.class;
            } else if (sa.getLookupEntityType() != null) {
                switch (sa.getLookupEntityCodeAttributeType()) {
                    case STRING:
                        return String.class;
                    case INTEGER:
                        return Long.class;
                    default:
                        break;
                }
            }
        }

        return null;
    }

    /**
     * Imports from date for a possibly given range. Null means, the start date
     * is not defined, what is pretty normal.
     *
     * @param fields
     *            the fields
     * @param range
     *            the entity
     * @return date or null
     * @throws SQLException
     *             the SQL exception
     */
    protected Date importRangeFrom(ResultSet fields, @Nonnull VersionRange range) throws SQLException {

        if (isNull(range.getValidFrom()) || nonNull(range.getValidFrom().getValue())) {
            return null;
        }

        DbExchangeField fromField = (DbExchangeField) range.getValidFrom();
        Object result = getFieldValue(fields, ImportDataJobUtils.getFieldAlias(fromField), fromField.getTypeClazz());

        if (result == null || !Date.class.isAssignableFrom(result.getClass())) {
            return null;
        }

        Date from = (Date) result;
        if (range.isNormalizeFrom()) {

            ZonedDateTime adj;
            if (from instanceof java.sql.Date) {
                LocalDate ld = ((java.sql.Date) from).toLocalDate();
                adj = ZonedDateTime.of(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth(),
                    0, 0, 0, 0, ZoneId.systemDefault());
            } else if (from instanceof java.sql.Timestamp) {
                LocalDateTime ldt = ((java.sql.Timestamp) from).toLocalDateTime();
                adj = ZonedDateTime.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
                        0, 0, 0, 0, ZoneId.systemDefault());
            } else {
                // This is probably wrong
                ZonedDateTime i = from.toInstant().atZone(ZoneId.systemDefault());
                adj = ZonedDateTime.of(i.getYear(), i.getMonthValue(), i.getDayOfMonth(),
                        0, 0, 0, 0, ZoneId.systemDefault());
            }

            from = ConvertUtils.zonedDateTime2Date(adj);
        }

        return from;
    }

    /**
     * Imports to date for a possibly given range. Null means, the to date is
     * not defined, what is pretty normal.
     *
     * @param fields
     *            the fields
     * @param range
     *            the entity
     * @return date or null
     * @throws SQLException
     *             the SQL exception
     */
    protected Date importRangeTo(ResultSet fields, @Nonnull VersionRange range) throws SQLException {
        if (isNull(range.getValidTo()) || nonNull(range.getValidTo().getValue())) {
            return null;
        }

        DbExchangeField toField = (DbExchangeField) range.getValidTo();
        Object result = getFieldValue(fields, ImportDataJobUtils.getFieldAlias(toField), toField.getTypeClazz());

        if (result == null || !Date.class.isAssignableFrom(result.getClass())) {
            return null;
        }

        Date to = (Date) result;
        if (range.isNormalizeTo()) {

            ZonedDateTime adj;
            if (to instanceof java.sql.Date) {
                LocalDate ld = ((java.sql.Date) to).toLocalDate();
                adj = ZonedDateTime.of(ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth(),
                        23, 59, 59, (int) TimeUnit.MILLISECONDS.toNanos(999), ZoneId.systemDefault());
            } else if (to instanceof java.sql.Timestamp) {
                LocalDateTime ldt = ((java.sql.Timestamp) to).toLocalDateTime();
                adj = ZonedDateTime.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
                        23, 59, 59, (int) TimeUnit.MILLISECONDS.toNanos(999), ZoneId.systemDefault());
            } else {
                // This is probably wrong
                ZonedDateTime i = to.toInstant().atZone(ZoneId.systemDefault());
                adj = ZonedDateTime.of(i.getYear(), i.getMonthValue(), i.getDayOfMonth(),
                        23, 59, 59, (int) TimeUnit.MILLISECONDS.toNanos(999), ZoneId.systemDefault());
            }

            to = ConvertUtils.zonedDateTime2Date(adj);
        }

        return to;
    }

    /**
     * Import range status.
     *
     * @param fields
     *            the fields
     * @param range
     *            the range
     * @return the record status
     * @throws SQLException
     *             the SQL exception
     */
    protected RecordStatus importRangeStatus(ResultSet fields, VersionRange range) throws SQLException {
        if (isNull(range) || isNull(range.getIsActive())) {
            return RecordStatus.ACTIVE;
        }

        DbExchangeField isActive = (DbExchangeField) range.getIsActive();
        if (isActive.getColumn() == null && isActive.getValue() != null) {
            return BooleanUtils.toBoolean(isActive.getValue().toString()) ? RecordStatus.ACTIVE : RecordStatus.INACTIVE;
        }

        Object result = getFieldValue(fields, ImportDataJobUtils.getFieldAlias(isActive), Boolean.class);
        if (result == null || !Boolean.class.isAssignableFrom(result.getClass())) {
            return RecordStatus.ACTIVE;
        }

        return (Boolean) result ? RecordStatus.ACTIVE : RecordStatus.INACTIVE;
    }

    protected String importNaturalKey(ResultSet rs, DbNaturalKey keyDef) throws SQLException {

        if (Objects.isNull(keyDef)) {
            return null;
        }

        Object keyObject = getFieldValue(rs, keyDef.getAlias(), keyDef.getTypeClazz());
        return keyObject == null || StringUtils.isBlank(keyObject.toString()) ? null : keyObject.toString();
    }

    protected String importSystemKey(ResultSet rs, DbSystemKey keyDef) throws SQLException {

        if (Objects.isNull(keyDef)) {
            return null;
        }

        Object keyObject = getFieldValue(rs, keyDef.getAlias(), keyDef.getTypeClazz());
        return keyObject == null || StringUtils.isBlank(keyObject.toString()) ? null : keyObject.toString();
    }

    /**
     * Sets an attribute.
     *
     * @param record the record to modify
     * @param field  exchange field
     * @param attrs  attributes tree to traverse
     * @param path   the path to process
     * @param value  the value to set
     * @param level  current expansion level
     */
    protected void setAttribute(DataRecord record, ExchangeField field, Map<String, AttributeInfoHolder> attrs, String path, Object value, int level) {

        boolean compound = ModelUtils.isCompoundPath(path);
        if (!compound) {

            AttributeInfoHolder holder = attrs.get(path);
            if (isNull(holder)) {
                throw new SystemRuntimeException("Attribute doesn't exist" + path, ExceptionId.EX_JOB_MAPPING_INCORRECT, path);
            }

            if (holder.isSimple()) {
                processSimpleAttribute(holder, record, value);
            } else if (holder.isComplex()) {
                processComplexAttribute(holder, record, field, level);
            } else if (holder.isCode()) {
                processCodeAttribute(holder, record, value);
            } else if (holder.isArray()) {
                processArrayAttribute(holder, record, value);
            }

        } else {

            String attrPath = ModelUtils.stripAttributePath(level, path);
            AttributeInfoHolder holder = attrs.get(attrPath);
            if (isNull(holder)) {
                throw new SystemRuntimeException("Attribute doesn't exist" + attrPath, ExceptionId.EX_JOB_MAPPING_INCORRECT, attrPath);
            }

            if (holder.isComplex()) {
                int nextLevel = level + 1;
                DataRecord nested = processComplexAttribute(holder, record, field, nextLevel);
                setAttribute(nested, field, attrs, path, value, nextLevel);
            } else if (holder.isSimple()) {
                processSimpleAttribute(holder, record, value);
            } else if (holder.isArray()) {
                processArrayAttribute(holder, record, value);
            }

        }
    }

    /**
     * Sets array attribute value.
     *
     * @param attr   the attribute
     * @param record the record
     * @param value  the value to set
     */
    protected void processArrayAttribute(AttributeInfoHolder holder, DataRecord record, Object value) {

        ArrayDataType type;
        ArrayAttributeDef attr = holder.narrow();
        ArrayAttribute<?> valueAttr = record.getArrayAttribute(attr.getName());
        if (valueAttr == null) {

            if (holder.isLookupLink()) {
                type = attr.getLookupEntityCodeAttributeType() == null ? null : ArrayDataType.valueOf(attr.getLookupEntityCodeAttributeType().name());
            } else {
                type = attr.getArrayValueType() == null ? null : ArrayDataType.valueOf(attr.getArrayValueType().name());
            }

            valueAttr = AbstractArrayAttribute.of(type, attr.getName());
            record.addAttribute(valueAttr);
        }

        AttributeUtils.processArrayAttributeValue(valueAttr, value, ((ArrayAttributeDef) holder.getAttribute()).getExchangeSeparator());
    }

    /**
     * Sets simple attribute value.
     *
     * @param attr   the attribute
     * @param record the record
     * @param value  the value to set
     */
    protected void processSimpleAttribute(AttributeInfoHolder holder, DataRecord record, Object value) {

        SimpleAttributeDef attr = holder.narrow();
        SimpleAttribute<?> valueAttr = record.getSimpleAttribute(attr.getName());
        if (valueAttr == null) {
            DataType type;
            if (holder.isLookupLink()) {
                type = attr.getLookupEntityCodeAttributeType() == null ? null : DataType.valueOf(attr.getLookupEntityCodeAttributeType().name());
            } else if (holder.isEnumValue()) {
                type = DataType.STRING;
            } else {
                type = attr.getSimpleDataType() == null ? null : DataType.valueOf(attr.getSimpleDataType().name());
            }

            valueAttr = AbstractSimpleAttribute.of(type, attr.getName());

            // UN-8701
            // TODO Refactor horrific measured attribute API
            if (valueAttr.getDataType() == DataType.MEASURED && attr.getMeasureSettings() != null) {
                ((MeasuredSimpleAttributeImpl) valueAttr)
                    .withValueId(attr.getMeasureSettings().getValueId())
                    .withInitialUnitId(attr.getMeasureSettings().getDefaultUnitId());
            }

            record.addAttribute(valueAttr);
        }

        AttributeUtils.processSimpleAttributeValue(valueAttr, value);
    }

    /**
     * Sets simple attribute value.
     *
     * @param attr   the attribute
     * @param record the record
     * @param value  the value to set
     */
    protected void processCodeAttribute(AttributeInfoHolder holder, DataRecord record, Object value) {

        CodeAttributeDef attr = holder.narrow();
        CodeAttribute<?> valueAttr = record.getCodeAttribute(attr.getName());
        if (valueAttr == null) {
            CodeDataType type = attr.getSimpleDataType() == null ? CodeDataType.STRING : CodeDataType.valueOf(attr.getSimpleDataType().name());
            valueAttr = AbstractCodeAttribute.of(type, attr.getName());
            record.addAttribute(valueAttr);
        }

        AttributeUtils.processCodeAttributeValue(valueAttr, value);
    }

    /**
     * Processes complex attribute.
     *
     * @param attr
     *            the attribute to process
     * @param record
     *            record to modify
     * @param field
     *            exchange field
     * @param level
     *            the level
     * @return a nested entity, new or existing
     */
    protected DataRecord processComplexAttribute(AttributeInfoHolder holder, DataRecord record, ExchangeField field, int level) {

        ComplexAttributeDef attr = holder.narrow();

        // 1. Find attribute
        ComplexAttribute valueAttr = record.getComplexAttribute(attr.getName());

        // 2. Create nested attribute, if needed
        if (valueAttr == null) {
            valueAttr = new ComplexAttributeImpl(attr.getName());
            record.addAttribute(valueAttr);
        }

        DataRecord result;
        BigInteger minCount = attr.getMinCount() == null ? BigInteger.ZERO : attr.getMinCount();
        BigInteger maxCount = attr.getMaxCount() == null ? BigInteger.ZERO : attr.getMaxCount();
        boolean cardinalityOne = 1 == minCount.intValue() && 1 == maxCount.intValue();

        // 3. Check rules. Cardinality has precedence
        if (cardinalityOne) {
            if (1 == valueAttr.size()) {
                result = valueAttr.get(0);
            } else {
                result = new SerializableDataRecord();
                valueAttr.add(result);
            }
        } else {
            // Process expansion rules
            if (CollectionUtils.isNotEmpty(field.getExpansions())) {
                return processComplexAttributeExpansions(valueAttr, field, level);
            }

            if (valueAttr.isEmpty()) {
                result = new SerializableDataRecord();
                valueAttr.add(result);
            } else {
                result = valueAttr.get(valueAttr.size() - 1);
            }
        }

        return result;
    }

    protected DataRecord processComplexAttributeExpansions(ComplexAttribute valueAttr, ExchangeField field, int level) {

        ComplexAttributeExpansion expansion = null;
        for (ComplexAttributeExpansion exp : field.getExpansions()) {
            if (exp.getLevel() == level) {
                expansion = exp;
                break;
            }
        }

        if (expansion == null) {
            return null;
        }

        Integer index = expansion.getIndex();
        String keyAttribute = expansion.getKeyAttribute();

        if (Objects.nonNull(index) && Objects.nonNull(keyAttribute)) {
            throw new SystemRuntimeException("Mapping for attribute " + field.getName() + "contains both 'index' and 'keyAttribute' in expansions. Either one can be defined.",
                    ExceptionId.EX_JOB_MAPPING_INCORRECT);
        }

        DataRecord result = null;

        // Index given
        if (Objects.nonNull(index)) {

            if (valueAttr.size() <= index) {

                int n = (index + 1) - valueAttr.size();
                for (int i  = 0; i < n; i++) {
                    valueAttr.add(new SerializableDataRecord());
                }
            }

            result = valueAttr.get(index);
        }

        // keyAttribute given TODO
        return result;
    }
}

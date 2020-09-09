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

import static com.unidata.mdm.backend.service.data.util.AttributeUtils.processSimpleAttributeValue;
import static java.util.Objects.isNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractArrayAttribute;
import com.unidata.mdm.backend.service.data.util.AttributeUtils;
import com.unidata.mdm.meta.ArrayAttributeDef;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginClassifierInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.OriginClassifierImpl;
import com.unidata.mdm.backend.exchange.def.ClassifierMapping;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportDataSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRecordSet;
import org.hsqldb.lib.Collection;

/**
 * Map Database result set to business logic object(origin record).
 */
public class EntityRowMapper extends AbstractRowMapper<ImportDataSet> {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5450521326952642056L;
    private static final String ARRAY_VALUES_SEPARATOR = "|";
    /**
     * The exchange entity.
     */
    private final DbExchangeEntity exchangeEntity;
    /**
     * The attrs.
     */
    private final Map<String, AttributeInfoHolder> entityAttributes;
    /**
     * Classifiers attributes.
     * This is a temporary solution
     * This doesn't cover the use case,
     * where different attributes with different data types, but the same name
     * exist on different branches.
     */
    private final Map<String, Map<String, ClsfNodeAttrDTO>> classifiersAttributes;
    /**
	 * Instantiates a new entity row mapper.
	 *
	 * @param exchangeEntity
	 *            the exchange entity
     * @param entityAttributes
	 *            the attrs
     * @param classifiersAttributes classifiers attributes
	 */
    public EntityRowMapper(DbExchangeEntity exchangeEntity,
            Map<String, AttributeInfoHolder> entityAttributes,
            Map<String, Map<String, ClsfNodeAttrDTO>> classifiersAttributes) {
        this.exchangeEntity = exchangeEntity;
        this.entityAttributes = entityAttributes;
        this.classifiersAttributes = classifiersAttributes;
    }

    /**
	 * Map row.
	 *
	 * @param rs
	 *            the rs
	 * @param rowNum
	 *            the row num
	 * @return the origin record
	 * @throws SQLException
	 *             the SQL exception
	 */
    @Override
    public ImportDataSet mapRow(ResultSet rs, int rowNum) throws SQLException {

        DataRecord record = new SerializableDataRecord();
        // 1. Process record if requested
        if (exchangeEntity.isProcessRecords()) {

            List<ExchangeField> fields = exchangeEntity.getFields();
            for (ExchangeField f : fields) {

                DbExchangeField dbf = (DbExchangeField) f;
                Object value;
                if (dbf.getValue() != null) {
                    value = dbf.getValue().toString();
                } else {
                    Class<?> typeClazz = dbf.getTypeClazz() == null ? getFieldClass(entityAttributes.get(dbf.getName())) : dbf.getTypeClazz();
                    if (typeClazz == null) {
                        throw new IllegalArgumentException("Cannot determine field type for ENTITY ["
                                + exchangeEntity.getName() + "." + dbf.getName() + "] field.");
                    }

                    value = getFieldValue(rs, ImportDataJobUtils.getFieldAlias(dbf), typeClazz);
                }

                if (value == null) {
                    continue;
                }

                setAttribute(record, f, entityAttributes, f.getName(), value, 0);
            }
        }

        ImportRecordSet result = new ImportRecordSet(record);
        result.setImportRowNum(rowNum);

        addOriginKey(result, rs, exchangeEntity);
        addEtalonKey(result, rs, exchangeEntity);
        addVersionRangeAndStatus(result, rs, exchangeEntity);

        // 2. Process classifiers, if requested
        if (exchangeEntity.isProcessClassifiers()) {
            addClassifiers(result, rs, exchangeEntity);
        }

        return result;
    }

    /**
     * Imports origin key.
     *
     * @param fields
     *            the record fields
     * @throws SQLException
     *             the SQL exception
     */
    @Nullable
    private void addEtalonKey(ImportRecordSet dataSet, ResultSet fields, ExchangeEntity exchangeEntity) throws SQLException {

        EtalonKey result = EtalonKey.builder()
                .id(importSystemKey(fields, (DbSystemKey) exchangeEntity.getSystemKey()))
                .build();

        dataSet.setEtalonKey(result);
    }

    /**
	 * Imports origin key.
	 *
	 * @param fields
	 *            the record fields
	 * @throws SQLException
	 *             the SQL exception
	 */
    @Nullable
    private void addOriginKey(ImportRecordSet dataSet, ResultSet fields, ExchangeEntity exchangeEntity) throws SQLException {

        OriginKey result = OriginKey.builder()
                .externalId(importNaturalKey(fields, (DbNaturalKey) exchangeEntity.getNaturalKey()))
                .entityName(exchangeEntity.getName())
                .sourceSystem(exchangeEntity.getSourceSystem())
                .build();

        dataSet.setOriginKey(result);
    }

    /**
	 * Gets the info section.
     * @param dataSet the data set
     * @param rs
	 *            - result set which can contain information about from/to
	 *            dates.
     * @param exchangeEntity entity description
	 *
	 * @throws SQLException
	 *             the SQL exception
	 */
    private void addVersionRangeAndStatus(ImportRecordSet dataSet, @Nonnull ResultSet rs, DbExchangeEntity exchangeEntity)
            throws SQLException {

        VersionRange range = exchangeEntity.getVersionRange();
        if (isNull(range)) {
            return;
        }

        java.util.Date from = importRangeFrom(rs, range);
        java.util.Date to = importRangeTo(rs, range);
        RecordStatus status = importRangeStatus(rs, range);

        dataSet.setStatus(status);
        dataSet.setValidFrom(from);
        dataSet.setValidTo(to);
    }

    /**
     * Gets the info section.
     * @param infoSection the data set
     * @param rs
     *            - result set which can contain information about from/to
     *            dates.
     * @param classifierMapping entity description
     *
     * @throws SQLException
     *             the SQL exception
     */
    @Nonnull
    private void addClassifierVersionRangeAndStatus(OriginClassifierInfoSection infoSection, @Nonnull ResultSet rs, ClassifierMapping classifierMapping)
            throws SQLException {

        VersionRange range = classifierMapping.getVersionRange();
        if (isNull(range)) {
            return;
        }

        java.util.Date from = importRangeFrom(rs, range);
        java.util.Date to = importRangeTo(rs, range);
        RecordStatus status = importRangeStatus(rs, range);

        infoSection.setStatus(status);
        infoSection.setValidFrom(from);
        infoSection.setValidTo(to);
    }
	/**
	 * Adds the classifiers.
	 *
	 * @param importSet
	 *            the record
	 * @param rs
	 *            the rs
	 * @param exchangeEntity
	 *            the exchange entity
	 * @throws SQLException
	 *             the SQL exception
	 */
	private void addClassifiers(ImportRecordSet importSet, ResultSet rs, DbExchangeEntity exchangeEntity)
			throws SQLException {

		if (CollectionUtils.isEmpty(exchangeEntity.getClassifierMappings())) {
			return;
		}

		List<ClassifierMapping> mappings = exchangeEntity.getClassifierMappings();
		for (ClassifierMapping mapping : mappings) {

			DbExchangeField node = (DbExchangeField) mapping.getNodeId();
			String nodeId = (String) getFieldValue(rs, ImportDataJobUtils.getFieldAlias(node), node.getTypeClazz());
			if (!StringUtils.isEmpty(nodeId)) {

			    String classifierName = StringUtils.substringAfter(node.getName(), "CL_");
			    Map<String, ClsfNodeAttrDTO> attrs = classifiersAttributes.get(classifierName);

                OriginClassifierImpl originClassifier = new OriginClassifierImpl(new SerializableDataRecord());
                OriginClassifierInfoSection infoSection = new OriginClassifierInfoSection();
                infoSection.setNodeId(nodeId);
                infoSection.setClassifierName(classifierName);

                addClassifierVersionRangeAndStatus(infoSection, rs, mapping);

                originClassifier.setInfoSection(infoSection);

                importSet.getClassifiers().add(originClassifier);

                List<ExchangeField> fields = mapping.getFields();
				if (CollectionUtils.isEmpty(fields) || MapUtils.isEmpty(attrs)) {
                    continue;
                }

                for (ExchangeField field : fields) {

                    DbExchangeField dbf = (DbExchangeField) field;
                    Class<?> typeClazz = dbf.getTypeClazz() == null ? getFieldClass(attrs.get(dbf.getName())) : dbf.getTypeClazz();
                    if (typeClazz == null) {
                        throw new IllegalArgumentException("Cannot determine field type for CLASSIFIER ["
                                + classifierName + "." + dbf.getName() + "] field.");
                    }

                    Object value = getFieldValue(rs, ImportDataJobUtils.getFieldAlias(dbf), typeClazz);

                    // skip empty values
                    if (value == null) {
                        continue;
                    }

                    ClsfNodeAttrDTO attrDTO = attrs.get(dbf.getName());
                    DataType dataType = attrDTO == null ? null : attrDTO.getDataType();
                    if (dataType == null && attrDTO != null && attrDTO.getLookupEntityCodeAttributeType() != null) {
                        dataType = DataType.valueOf(attrDTO.getLookupEntityCodeAttributeType().name());
                    }
                    if (dataType == null || (dataType == DataType.STRING && StringUtils.isEmpty(value.toString()))) {
                        continue;
                    }

                    if (attrDTO instanceof ClsfNodeSimpleAttrDTO) {
                        SimpleAttribute<?> attr = createSimpleAttribute(value, dbf.getName(), dataType);
                        originClassifier.addAttribute(attr);
                    }
                    else {
                        ArrayAttribute<?> attr = createArrayAttribute(
                                value,
                                dbf.getName(),
                                ArrayAttribute.ArrayDataType.valueOf(dataType.name()),
                                ARRAY_VALUES_SEPARATOR
                        );
                        originClassifier.addAttribute(attr);
                    }
                }
            }
        }
    }

    /**
     *
     * @param value
     * @param attrName
     * @param dataType
     * @return simple attribute
     */
    private SimpleAttribute<?> createSimpleAttribute(Object value, String attrName, @Nullable DataType dataType) {
        SimpleAttribute<?> valueAttr = AbstractSimpleAttribute.of(dataType == null ? DataType.STRING : dataType, attrName);
        processSimpleAttributeValue(valueAttr, value);
        return valueAttr;
    }

    private ArrayAttribute<?> createArrayAttribute(
            final Object value,
            final String name,
            final ArrayAttribute.ArrayDataType dataType,
            final String valuesSeparator
    ) {
        final ArrayAttribute<?> arrayAttribute = AbstractArrayAttribute.of(
                dataType == null ? ArrayAttribute.ArrayDataType.STRING : dataType,
                name,
                Collections.emptyList()
        );
        AttributeUtils.processArrayAttributeValue(arrayAttribute, value, valuesSeparator);
        return arrayAttribute;
    }
}

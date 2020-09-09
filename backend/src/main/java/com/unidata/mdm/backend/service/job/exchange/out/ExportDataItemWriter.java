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

package com.unidata.mdm.backend.service.job.exchange.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.common.configuration.BeanNameConstants;
import com.unidata.mdm.backend.common.dto.GetClassifierDTO;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.exchange.def.ClassifierMapping;
import com.unidata.mdm.backend.exchange.def.DqErrorsSection;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.UpdateMarkType;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbJsonDqErrorsSection;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbSingleRowDqErrorsSection;
import com.unidata.mdm.backend.exchange.def.db.DbUpdateMark;
import com.unidata.mdm.backend.exchange.util.TransformUtils;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.AttributesWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;

/**
 * @author Denis Kostovarov
 */
@Component
@Scope("step")
public class ExportDataItemWriter extends ExportDataStepChainMember implements ItemWriter<GetRecordDTO> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportDataConstants.EXPORT_JOB_LOGGER_NAME);
    /**
     * The meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Audit writer
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;
    /**
     * Updates after.
     */
    @Value("#{jobParameters[updatesAfter]}")
    private Date updatesAfter;
    /**
     * As of.
     */
    @Value("#{jobParameters[asOf]}")
    private Date asOf;
    /**
     * The operation id to use.
     */
    @Value("#{stepExecutionContext[operationId]}")
    private String operationId;

    /**
     * Default object mapper.
     */
    @Autowired
    @Qualifier(BeanNameConstants.DEFAULT_OBJECT_MAPPER_BEAN_NAME)
    protected ObjectMapper objectMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends GetRecordDTO> items) throws Exception {

        DbExchangeEntity entity = getExchangeObject();
        if (entity == null) {
            LOGGER.warn("Exchange entity object is null. Exit item processing for chunk.");
            return;
        }

        if (CollectionUtils.isEmpty(items)) {
            LOGGER.info("Bulk set is empty. Exiting.");
            return;
        }

        AttributesWrapper aw;
        if (metaModelService.isEntity(entity.getName())) {
            aw = metaModelService.getValueById(entity.getName(), EntityWrapper.class);
        } else {
            aw = metaModelService.getValueById(entity.getName(), LookupEntityWrapper.class);
        }

        String sourceSystem = entity.getSourceSystem();
        for (GetRecordDTO item : items) {
            OriginKey key = item.getRecordKeys().getKeyBySourceSystem(sourceSystem);
            if (key == null) {
                LOGGER.info(
                        "Origin key for the source system {} not found. The source system from current active origin key will be used.",
                        sourceSystem);
                key = item.getRecordKeys().getOriginKey();
            }
            LOGGER.debug("Processing record with external id {}, source system {}.", key.getExternalId(), sourceSystem);
            exportRecord(key, item, entity, aw);
            exportDqErrors(entity, item, key);
        }

        LOGGER.info("Operation bulk upsert done.");
    }

    /**
     * @param key    - used requared key
     * @param item   - record
     * @param entity - entity
     * @param aw     - attribute wrapper
     */
    private void exportRecord(OriginKey key, GetRecordDTO item, DbExchangeEntity entity, AttributesWrapper aw) {
        //just dq export!
        if (CollectionUtils.isEmpty(entity.getFields()) ||
                (CollectionUtils.isEmpty(entity.getTables())
                        && CollectionUtils.isNotEmpty(entity.getClassifierMappings()))) {
            return;
        }
        String sourceSystem = entity.getSourceSystem();
        Pair<String, List<Object>> statements = generateStatements(item, key, entity, aw);
        try {

            String action = getJdbcTemplate().queryForObject(statements.getKey(), statements.getValue().toArray(),
                    String.class);
            if (ExportDataConstants.EXPORT_JOB_UPDATE_RESULT.equals(action)) {
                getStatisticPage().incrementUpdated(1L);
            } else {
                getStatisticPage().incrementInserted(1L);
            }

            auditEventsWriter.writeSuccessEvent(AuditActions.DATA_EXPORT, item, operationId, sourceSystem, asOf,
                    updatesAfter, action);
            LOGGER.info("Record with external id {}, source system {}: Finished processing with action {}.",
                    key.getExternalId(), key.getSourceSystem(), action);
        } catch (Exception e) {
            getStatisticPage().incrementFailed(1L);
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_EXPORT, e, item, operationId, sourceSystem, asOf,
                    updatesAfter, "FAILURE");
            LOGGER.warn("Record with external id {}, source system {}: Exception caught, while doing upsert.",
                    key.getExternalId(), key.getSourceSystem(), e);
            throw e;
        }
    }

    /**
     * Dq errors export
     *
     * @param entity
     * @param item
     */
    //todo rewrite instance of!
    private void exportDqErrors(DbExchangeEntity entity, GetRecordDTO item, OriginKey key) {
        try {
            DqErrorsSection dqErrorsSection = entity.getDqErrorsSection();
            if (dqErrorsSection == null) {
                return;
            }
            List<DataQualityError> dq = item.getDqErrors();
            if (CollectionUtils.isEmpty(dq)) {
                return;
            }
            // Insert head buffer
            StringBuilder ih = new StringBuilder();
            // Insert tail buffer
            StringBuilder it = new StringBuilder();
            List<Object> foreignKey = new ArrayList<>();
            if (isNoneBlank(dqErrorsSection.getEntityNameField())) {
                generateFieldInsert(dqErrorsSection.getEntityNameField(), ih, it);
                foreignKey.add(key.getEntityName());
            }
            if (isNoneBlank(dqErrorsSection.getExternalIdField())) {
                generateFieldInsert(dqErrorsSection.getExternalIdField(), ih, it);
                foreignKey.add(key.getExternalId());
            }
            if (isNoneBlank(dqErrorsSection.getSourceSystemField())) {
                generateFieldInsert(dqErrorsSection.getSourceSystemField(), ih, it);
                foreignKey.add(key.getSourceSystem());
            }
            if (isNoneBlank(dqErrorsSection.getEtalonIdField())) {
                generateFieldInsert(dqErrorsSection.getEtalonIdField(), ih, it);
                foreignKey.add(item.getRecordKeys().getEtalonKey().getId());
            }
            List<Object[]> args = Collections.emptyList();
            String table = "";
            if (dqErrorsSection instanceof DbJsonDqErrorsSection) {
                DbJsonDqErrorsSection jsonDqErrorsSection = (DbJsonDqErrorsSection) dqErrorsSection;
                table = jsonDqErrorsSection.getTable();
                String json = objectMapper.writeValueAsString(dq);
                generateFieldInsert(jsonDqErrorsSection.getJsonColumn(), ih, it);
                foreignKey.add(json);
                args = Collections.singletonList(foreignKey.toArray(new Object[foreignKey.size()]));
            } else if (dqErrorsSection instanceof DbSingleRowDqErrorsSection) {
                DbSingleRowDqErrorsSection singleRowDqErrorsSection = (DbSingleRowDqErrorsSection) dqErrorsSection;
                table = singleRowDqErrorsSection.getTable();
                if (isNoneBlank(singleRowDqErrorsSection.getStatusField())) {
                    generateFieldInsert(singleRowDqErrorsSection.getStatusField(), ih, it);
                }
                if (isNoneBlank(singleRowDqErrorsSection.getRuleNameField())) {
                    generateFieldInsert(singleRowDqErrorsSection.getRuleNameField(), ih, it);
                }
                if (isNoneBlank(singleRowDqErrorsSection.getMessageField())) {
                    generateFieldInsert(singleRowDqErrorsSection.getMessageField(), ih, it);
                }
                if (isNoneBlank(singleRowDqErrorsSection.getSeverityField())) {
                    generateFieldInsert(singleRowDqErrorsSection.getSeverityField(), ih, it);
                }
                if (isNoneBlank(singleRowDqErrorsSection.getCategoryField())) {
                    generateFieldInsert(singleRowDqErrorsSection.getCategoryField(), ih, it);
                }
                args = extractAggrs(dq, foreignKey, singleRowDqErrorsSection);
            }
            String statement = new StringBuilder().append("insert into ")
                    .append(table)
                    .append(" (")
                    .append(ih.toString())
                    .append(") values (")
                    .append(it.toString())
                    .append(")")
                    .toString();

            if (dqErrorsSection instanceof DbJsonDqErrorsSection) {
                getJdbcTemplate().update(statement, args.get(0));
            } else {
                getJdbcTemplate().batchUpdate(statement, args);
            }
        } catch (Exception e) {
            LOGGER.warn("Record with external id {}, source system {}: Exception caught, while doing dq upsert.",
                    key.getExternalId(), key.getSourceSystem(), e);
        }
    }

    private List<Object[]> extractAggrs(List<DataQualityError> dqs, List<Object> foreignKey,
                                        DbSingleRowDqErrorsSection dqErrorsSection) {
        return dqs.stream()
                .map(dq -> extractAggr(dq, foreignKey, dqErrorsSection))
                .map(col -> col.toArray(new Object[col.size()]))
                .collect(Collectors.toList());
    }

    private List<Object> extractAggr(DataQualityError dq, List<Object> foreignKey, DbSingleRowDqErrorsSection dqErrorsSection) {
        List<Object> aggr = new ArrayList<>(foreignKey);
        if (isNoneBlank(dqErrorsSection.getStatusField())) {
            aggr.add(dq.getStatus().toString());
        }
        if (isNoneBlank(dqErrorsSection.getRuleNameField())) {
            aggr.add(dq.getRuleName());
        }
        if (isNoneBlank(dqErrorsSection.getMessageField())) {
            aggr.add(dq.getMessage());
        }
        if (isNoneBlank(dqErrorsSection.getSeverityField())) {
            aggr.add(dq.getSeverity().toString());
        }
        if (isNoneBlank(dqErrorsSection.getCategoryField())) {
            aggr.add(dq.getCategory());
        }
        return aggr;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    /**
     * @param updatesAfter the updatesAfter to set
     */
    public void setUpdatesAfter(Date updatesAfter) {
        this.updatesAfter = updatesAfter;
    }

    /**
     * @param asOf the asOf to set
     */
    public void setAsOf(Date asOf) {
        this.asOf = asOf;
    }

    /**
     * Generates update and insert statements.
     *
     * @param item      full record
     * @param originKey the selected key
     * @param dbe       exchange entity object
     * @param aw        attributes.
     * @return pair of statements
     */
    private Pair<String, List<Object>> generateStatements(GetRecordDTO item, OriginKey originKey, DbExchangeEntity dbe, AttributesWrapper aw) {

        EtalonRecord record = item.getEtalon();

        // Update buffer
        StringBuilder u = new StringBuilder();
        // Insert head buffer
        StringBuilder ih = new StringBuilder();
        // Insert tail buffer
        StringBuilder it = new StringBuilder();

        // 1. Fields and params allocation
        // The magic number 8 is: 3 keys (2 for where condition + 1 for insert), 2 update mark fields, 3 fields for version range
        List<Object> values = new ArrayList<>(dbe.getFields().size() * 2 + 8);

        // 2. Insert new key
        generateKeyInsert(dbe, ih, it);

        // 3. Generate fields update
        generateFieldsUpdate(record, dbe, aw, u, ih, it, values);

        // 4. Generate classifiers update
        generateClassifiersUpdate(item.getClassifiers(), dbe, u, ih, it, values);

        // 5. Service fields - update mark
        generateUpdateMark(dbe, originKey, u, ih, it, values);

        // 6. Service fields - version range
        generateVersionRange(record, dbe, u, ih, it, values);

        // 7. Create statement
        DbNaturalKey naturalKey = (DbNaturalKey) dbe.getNaturalKey();
        String table = dbe.getTables().get(0); // Only one table name allowed for export
        StringBuilder us = new StringBuilder()
                .append("__u as (update ")
                .append(table)
                .append(" set ")
                .append(u)
                .append(" where ")
                .append(table)
                .append(".")
                .append(naturalKey.getColumn())
                .append(" = ? returning 'UPDATE'::text as action)");

        StringBuilder is = new StringBuilder()
                .append("__i as (insert into ")
                .append(table)
                .append(" ( ")
                .append(ih)
                .append(") select ")
                .append(it)
                .append(" where not exists (select ")
                .append(naturalKey.getColumn())
                .append(" from ")
                .append(table)
                .append(" where ")
                .append(naturalKey.getColumn())
                .append(" = ?) returning 'INSERT'::text as action)");

        StringBuilder statement = new StringBuilder()
                .append("with ")
                .append(us)
                .append(", ")
                .append(is)
                .append(" select action from __u union all select action from __i");

        // 7. Transform / duplicate arguments
        // Add key for update where condition
        values.add(originKey.getExternalId());
        // Add param for key insert
        values.add(values.size(), originKey.getExternalId());
        // Duplicate arguments for insert statements
        values.addAll(values.size(), values.subList(0, values.size() - 2));
        // Add key for insert where condition
        values.add(originKey.getExternalId());

        return new ImmutablePair<>(statement.toString(), values);
    }

    /**
     * Generates key insert.
     *
     * @param dbe exchange object
     * @param ih  insert head
     * @param it  insert tail
     */
    private void generateKeyInsert(DbExchangeEntity dbe, StringBuilder ih, StringBuilder it) {
        DbNaturalKey key = (DbNaturalKey) dbe.getNaturalKey();
        generateFieldInsert(key.getColumn(), ih, it);
    }

    /**
     * Process updte mark.
     *
     * @param dbe       the exchange object
     * @param originKey the key
     * @param u         update buffer
     * @param ih        insert head buffer
     * @param it        insert tail buffer
     * @return list of arguments
     */
    private void generateVersionRange(EtalonRecord record, DbExchangeEntity dbe,
                                      StringBuilder u, StringBuilder ih, StringBuilder it, List<Object> values) {

        VersionRange versionRange = dbe.getVersionRange();
        if (Objects.isNull(versionRange)) {
            LOGGER.info("Version range is not defined for exchange type {}. Skipping.", dbe.getName());
            return;
        }

        // Active or not
        DbExchangeField active = (DbExchangeField) versionRange.getIsActive();
        if (active != null) {

            Object value;
            Class<?> typeClazz = active.getTypeClazz();
            if (typeClazz != null) {
                value = TransformUtils.getFieldValue(
                        Boolean.valueOf(record.getInfoSection().getStatus() == RecordStatus.ACTIVE), active, null);
            } else {
                value = Boolean.valueOf(record.getInfoSection().getStatus() == RecordStatus.ACTIVE);
            }

            values.add(value);
            generateFieldUpdate(active.getColumn(), u);
            generateFieldInsert(active.getColumn(), ih, it);
        }

        // Valid from
        DbExchangeField validFrom = (DbExchangeField) versionRange.getValidFrom();
        if (validFrom != null) {

            Object value;
            Class<?> typeClazz = validFrom.getTypeClazz();
            if (typeClazz != null) {
                value = TransformUtils.getFieldValue(
                        record.getInfoSection().getValidFrom(), validFrom, null);
            } else {
                value = record.getInfoSection().getValidFrom();
            }

            values.add(value);
            generateFieldUpdate(validFrom.getColumn(), u);
            generateFieldInsert(validFrom.getColumn(), ih, it);
        }

        // Valid to
        DbExchangeField validTo = (DbExchangeField) versionRange.getValidTo();
        if (validTo != null) {

            Object value;
            Class<?> typeClazz = validTo.getTypeClazz();
            if (typeClazz != null) {
                value = TransformUtils.getFieldValue(
                        record.getInfoSection().getValidTo(), validTo, null);
            } else {
                value = record.getInfoSection().getValidTo();
            }

            values.add(value);
            generateFieldUpdate(validTo.getColumn(), u);
            generateFieldInsert(validTo.getColumn(), ih, it);
        }
    }

    /**
     * Process updte mark.
     *
     * @param dbe       the exchange object
     * @param originKey the key
     * @param u         update buffer
     * @param ih        insert head buffer
     * @param it        insert tail buffer
     * @return list of arguments
     */
    private void generateUpdateMark(DbExchangeEntity dbe, OriginKey originKey,
                                    StringBuilder u, StringBuilder ih, StringBuilder it, List<Object> values) {

        DbUpdateMark updateMark = (DbUpdateMark) dbe.getUpdateMark();

        // Ignore processing if updateMarkType doesn't exist.
        if (updateMark == null) {
            return;
        }

        values.add(originKey.getSourceSystem());
        generateFieldUpdate(updateMark.getSourceSystemColumn(), u);
        generateFieldInsert(updateMark.getSourceSystemColumn(), ih, it);

        if (updateMark.getUpdateMarkType() == UpdateMarkType.TIMESTAMP) {
            values.add(new Timestamp(System.currentTimeMillis()));
            generateFieldUpdate(updateMark.getColumn(), u);
            generateFieldInsert(updateMark.getColumn(), ih, it);
        } else {  // Boolean
            values.add(Boolean.TRUE);
            generateFieldUpdate(updateMark.getColumn(), u);
            generateFieldInsert(updateMark.getColumn(), ih, it);
        }
    }

    /**
     * Process fields.
     *
     * @param record the record
     * @param dbe    the exchange object
     * @param aw     attributes
     * @param u      update buffer
     * @param ih     insert head buffer
     * @param it     insert tail buffer
     * @return list of arguments
     */
    private void generateFieldsUpdate(
            EtalonRecord record, DbExchangeEntity dbe, AttributesWrapper aw,
            StringBuilder u, StringBuilder ih, StringBuilder it, List<Object> values) {


        for (ExchangeField ef : dbe.getFields()) {

            DbExchangeField dbf = (DbExchangeField) ef;
            AttributeInfoHolder holder = aw.getAttributes().get(dbf.getName());
            Object value = null;

            if (dbf.getValue() != null) {
                value = TransformUtils.getFieldValue(dbf.getValue().toString(), dbf, null);
            } else {
                Collection<Attribute> attrs = record.getAttributeRecursive(dbf.getName());
                try {

                    value = TransformUtils.getFieldValue(CollectionUtils.isEmpty(attrs)
                            ? null
                            : attrs.iterator().next(), dbf, holder);

                    if (value instanceof Object[] && dbf.getTypeClazz() == Array.class) {
                        value = getJdbcTemplate().getDataSource().getConnection().createArrayOf("", (Object[]) value);
                    }

                } catch (Exception e) {
                    LOGGER.warn("Failed to collect value for {}", String.join(".", dbe.getName(), dbf.getName()), e);
                }
            }

            values.add(value);
            generateFieldUpdate(dbf.getColumn(), u);
            generateFieldInsert(dbf.getColumn(), ih, it);
        }
    }

    /**
     * Process fields.
     *
     * @param classifierMap classifier map
     * @param dbe           the exchange object
     * @param u             update buffer
     * @param ih            insert head buffer
     * @param it            insert tail buffer
     * @return list of arguments
     */
    private void generateClassifiersUpdate(
            Map<String, List<GetClassifierDTO>> classifierMap, DbExchangeEntity dbe,
            StringBuilder u, StringBuilder ih, StringBuilder it, List<Object> values) {

        if (MapUtils.isEmpty(classifierMap) || CollectionUtils.isEmpty(dbe.getClassifierMappings())) {
            return;
        }

        for (ClassifierMapping cf : dbe.getClassifierMappings()) {
            DbExchangeField nodeIdField = (DbExchangeField) cf.getNodeId();
            List<GetClassifierDTO> classifiersDTO = classifierMap.get(StringUtils.substringAfter(nodeIdField.getName(), "CL_"));
            if (CollectionUtils.isNotEmpty(classifiersDTO)) {
                GetClassifierDTO classifier = classifiersDTO.get(0);
                values.add(classifier.getClassifierKeys().getNodeId());
                generateFieldUpdate(nodeIdField.getColumn(), u);
                generateFieldInsert(nodeIdField.getColumn(), ih, it);
            }
        }
    }

    private void generateFieldUpdate(String column, StringBuilder u) {
        u.append(u.length() > 0 ? ", " : "")
                .append(column)
                .append(" = ?");
    }

    private void generateFieldInsert(String column, StringBuilder ih, StringBuilder it) {
        ih.append(ih.length() > 0 ? ", " : "")
                .append(column);

        it.append(it.length() > 0 ? ", ?" : "?");
    }
}

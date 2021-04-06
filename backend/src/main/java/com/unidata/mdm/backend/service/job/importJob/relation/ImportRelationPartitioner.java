package com.unidata.mdm.backend.service.job.importJob.relation;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.importJob.AbstractRowMapper;
import com.unidata.mdm.backend.service.job.importJob.ImportPartitioner;
import com.unidata.mdm.backend.service.job.importJob.record.EntityRowMapper;
import com.unidata.mdm.backend.service.job.importJob.record.ImportRecordPartitioner;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRelationSet;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.RelationWrapper;
import com.unidata.mdm.meta.RelType;

@Component("importRelationPartitioner")
@StepScope
public class ImportRelationPartitioner extends ImportPartitioner<ExchangeRelation> {

    /**
     * Depends on type - partitioner will have other behavior
     */
    private RelType relType = null;

    /**
     * Record partitioner which will be used in contain relation case
     */
    private ImportRecordPartitioner importRecordPartitioner;

    /**
     * From source system
     */
    private String fromSourceSystem;

    /**
     * Row mapper for relations
     */
    private AbstractRowMapper<ImportRelationSet> relationRowMapper = null;

    @Autowired
    private MetaModelServiceExt modelService;

    @Override
    protected void addSpecialStepParameters(ExecutionContext context, ExchangeRelation definition) {
        String relation = definition.getRelation();
        boolean isRel = modelService.isRelation(relation);
        if (!isRel) {
            throw new IllegalArgumentException();
        }
        RelationWrapper relationWrapper = modelService.getValueById(relation, RelationWrapper.class);
        if (isReferenceRelation(definition)) {
            DbRelatesToRelation tableDefinition = (DbRelatesToRelation) definition;
            if (Objects.isNull(relationRowMapper)) {
                Map<String, AttributeInfoHolder> attrs = relationWrapper.getAttributes();
                relationRowMapper = new ReferenceRelationRowMapper(tableDefinition, attrs, relationWrapper.getRelation().getToEntity());
            }
            context.putString("toSourceSystem", tableDefinition.getToSourceSystem());
            context.putString("toEntityAttributeName", tableDefinition.getToEntityAttributeName());
            processVersionRange(context, tableDefinition.getVersionRange());
        } else {
            ContainmentRelation tableDefinition = (ContainmentRelation) definition;
            DbExchangeEntity dbExchangeEntity = (DbExchangeEntity) tableDefinition.getEntity();
            importRecordPartitioner.addSpecialStepParameters(context, dbExchangeEntity);
            if (Objects.isNull(relationRowMapper)) {
                EntityRowMapper entityRowMapper = (EntityRowMapper) context.get("rowMapper");
                relationRowMapper = new ContainsRelationRowMapper(tableDefinition, entityRowMapper);
            }
            context.putString("toSourceSystem", dbExchangeEntity.getSourceSystem());
        }
        context.put("rowMapper", relationRowMapper);
        context.putString("fromSourceSystem", fromSourceSystem);
        context.putString("entityName", relationWrapper.getRelation().getFromEntity());
        context.putString("relationName", relation);
        context.clearDirtyFlag();
    }

    @Nonnull
    @Override
    protected String getSql(@Nonnull ExchangeRelation definition, long offset, long limit) {
        if (isReferenceRelation(definition)) {

            DbRelatesToRelation tableDefinition = (DbRelatesToRelation) definition;
            StringBuilder sqlb = new StringBuilder().append("select ");

            if (tableDefinition.getVersionRange() != null) {

                DbExchangeField fromField = (DbExchangeField) tableDefinition.getVersionRange().getValidFrom();
                DbExchangeField toField = (DbExchangeField) tableDefinition.getVersionRange().getValidTo();
                DbExchangeField active = (DbExchangeField) tableDefinition.getVersionRange().getIsActive();

                sqlb
                        .append(fromField != null ? fromField.getColumn() : "null")
                        .append(" as ")
                        .append(fromField != null ? fromField.getAlias() : "FROM_FIELD ")
                        .append(", ");

                sqlb
                        .append(toField != null ? toField.getColumn() : "null")
                        .append(" as ")
                        .append(toField != null ? toField.getAlias() : "FROM_FIELD")
                        .append(", ");

                if (active != null) {
                    sqlb
                            .append(active != null ? active.getColumn() : "null")
                            .append(" as ")
                            .append(active != null ? active.getAlias() : "ACTIVE_FIELD, ")
                            .append(", ");
                }
            }

            // Natural from
            DbNaturalKey naturalFromKey = (DbNaturalKey) tableDefinition.getFromNaturalKey();
            if (naturalFromKey != null) {
                sqlb
                        .append(naturalFromKey.getSqlAdditionLeft() != null ? naturalFromKey.getSqlAdditionLeft() : "")
                        .append(" ")
                        .append(naturalFromKey.getColumn())
                        .append(" ")
                        .append(naturalFromKey.getSqlAdditionRight() != null ? naturalFromKey.getSqlAdditionRight() : "")
                        .append(" as ")
                        .append(naturalFromKey.getAlias())
                        .append(", ");
            }

            // System from
            DbSystemKey systemFromKey = (DbSystemKey) tableDefinition.getFromSystemKey();
            if (Objects.nonNull(systemFromKey)) {
                sqlb
                    .append(systemFromKey.getSqlAdditionLeft() != null ? systemFromKey.getSqlAdditionLeft() : "")
                    .append(" ")
                    .append(systemFromKey.getColumn())
                    .append(" ")
                    .append(systemFromKey.getSqlAdditionRight() != null ? systemFromKey.getSqlAdditionRight() : "")
                    .append(" as ")
                    .append(systemFromKey.getAlias())
                    .append(", ");
            }

            // Natural to
            DbNaturalKey naturalToKey = (DbNaturalKey) tableDefinition.getToNaturalKey();
            if (naturalToKey != null) {
                sqlb
                        .append(naturalToKey.getSqlAdditionLeft() != null ? naturalToKey.getSqlAdditionLeft() : "")
                        .append(" ")
                        .append(naturalToKey.getColumn())
                        .append(" ")
                        .append(naturalToKey.getSqlAdditionRight() != null ? naturalToKey.getSqlAdditionRight() : "")
                        .append(" as ")
                        .append(naturalToKey.getAlias())
                        .append(", ");
            }

            // System to
            DbSystemKey systemToKey = (DbSystemKey) tableDefinition.getToSystemKey();
            if (Objects.nonNull(systemToKey)) {
                sqlb
                    .append(systemToKey.getSqlAdditionLeft() != null ? systemToKey.getSqlAdditionLeft() : "")
                    .append(" ")
                    .append(systemToKey.getColumn())
                    .append(" ")
                    .append(systemToKey.getSqlAdditionRight() != null ? systemToKey.getSqlAdditionRight() : "")
                    .append(" as ")
                    .append(systemToKey.getAlias())
                    .append(", ");
            }

            for (int i = 0; tableDefinition.getFields() != null && i < tableDefinition.getFields().size(); i++) {

                DbExchangeField def = (DbExchangeField) tableDefinition.getFields().get(i);
                sqlb
                        .append(def.getColumn())
                        .append(" as ")
                        .append(def.getAlias())
                        .append(", ");
            }

            sqlb
                .append("'__LAST_FIELD__'")
                .append(" as ")
                .append(LAST_FIELD_INDICATOR)
                .append(" ");

            String from = getFromSql(getTables(tableDefinition), getJoins(tableDefinition));
            sqlb.append(from);

            if (tableDefinition.isMultiVersion()) {
                sqlb
                        .append(tableDefinition.getJoins() != null && !tableDefinition.getJoins().isEmpty() ? "and " : "where ")
                        .append(naturalFromKey.getColumn())
                        .append(" in (select distinct ")
                        .append(naturalFromKey.getColumn())
                        .append(" from ");

                for (int i = 0; tableDefinition.getTables() != null && i < tableDefinition.getTables().size(); i++) {

                    String tbl = tableDefinition.getTables().get(i);
                    sqlb
                            .append(tbl)
                            .append(i < tableDefinition.getTables().size() - 1 ? ", " : " ");
                }

                for (int i = 0; tableDefinition.getJoins() != null && i < tableDefinition.getJoins().size(); i++) {

                    String j = tableDefinition.getJoins().get(i);
                    sqlb
                            .append(i == 0 ? "where " : "and ")
                            .append(j)
                            .append(" ");
                }

                if (tableDefinition.getLimitPredicate() != null && offset > 0) {
                    sqlb.append(tableDefinition.getJoins() != null && tableDefinition.getJoins().size() > 0 ? "and " : "where ")
                            .append(tableDefinition.getLimitPredicate())
                            .append(" ")
                            .append(offset)
                            .append(" ");
                }

                sqlb
                        .append("order by ")
                        .append(naturalFromKey.getColumn())
                        .append(" asc ");

                if (limit > 0) {
                    sqlb.append("limit ")
                            .append(limit)
                            .append(" ");
                }

                if (offset > 0 && tableDefinition.getLimitPredicate() == null) {
                    sqlb.append(" offset ")
                            .append(offset);
                }

                sqlb.append(") ");

                if (tableDefinition.getOrderBy() != null) {
                    sqlb
                            .append("order by ")
                            .append(tableDefinition.getOrderBy());
                }

            } else {

                if (tableDefinition.getLimitPredicate() != null && offset > 0) {
                    sqlb.append(tableDefinition.getJoins() != null && tableDefinition.getJoins().size() > 0 ? "and " : "where ")
                            .append(tableDefinition.getLimitPredicate())
                            .append(" ")
                            .append(offset)
                            .append(" ");
                }

                if (tableDefinition.getOrderBy() != null) {
                    sqlb
                            .append("order by ")
                            .append(tableDefinition.getOrderBy())
                            .append(" ");
                }

                if (limit > 0) {
                    sqlb.append("limit ")
                            .append(limit);
                }

                if (offset > 0 && tableDefinition.getLimitPredicate() == null) {
                    sqlb.append(" offset ")
                            .append(offset);
                }
            }
            return sqlb.toString();
        } else {
            ContainmentRelation tableDefinition = (ContainmentRelation) definition;
            DbExchangeEntity dbExchangeEntity = (DbExchangeEntity) tableDefinition.getEntity();
            return importRecordPartitioner.getSql(dbExchangeEntity, offset, limit);
        }
    }

    @Override
    public String getEntityName(ExchangeRelation baseDefinition) {
        return baseDefinition.getRelation();
    }

    @Nonnull
    @Override
    protected List<String> getTables(@Nonnull ExchangeRelation definition) {
        if (isReferenceRelation(definition)) {
            DbRelatesToRelation tableDefinition = (DbRelatesToRelation) definition;
            return tableDefinition.getTables() == null ? Collections.emptyList() : tableDefinition.getTables();
        } else {
            ContainmentRelation tableDefinition = (ContainmentRelation) definition;
            DbExchangeEntity dbExchangeEntity = (DbExchangeEntity) tableDefinition.getEntity();
            return importRecordPartitioner.getTables(dbExchangeEntity);
        }
    }

    @Nonnull
    @Override
    protected List<String> getJoins(@Nonnull ExchangeRelation definition) {
        if (isReferenceRelation(definition)) {
            DbRelatesToRelation tableDefinition = (DbRelatesToRelation) definition;
            return tableDefinition.getJoins() == null ? Collections.emptyList() : tableDefinition.getJoins();
        } else {
            ContainmentRelation tableDefinition = (ContainmentRelation) definition;
            DbExchangeEntity dbExchangeEntity = (DbExchangeEntity) tableDefinition.getEntity();
            return importRecordPartitioner.getJoins(dbExchangeEntity);
        }
    }

    private boolean isReferenceRelation(@Nonnull ExchangeRelation definition) {
        if (relType == null) {
            if (definition instanceof DbRelatesToRelation) {
                relType = RelType.REFERENCES;
            } else if (definition instanceof ContainmentRelation) {
                relType = RelType.CONTAINS;
                ContainmentRelation tableDefinition = (ContainmentRelation) definition;
                DbExchangeEntity dbExchangeEntity = (DbExchangeEntity) tableDefinition.getEntity();
                importRecordPartitioner = new ImportRecordPartitioner();
                importRecordPartitioner.setModelService(modelService);
                importRecordPartitioner.setBaseDefinition(dbExchangeEntity);
                importRecordPartitioner.setBatchSize(this.getBatchSize());
                importRecordPartitioner.setOffset(this.getOffset());
                importRecordPartitioner.setQuantityOfProcessedRecords(this.getQuantityOfProcessedRecords());
                importRecordPartitioner.setDatabaseUrl(this.getDatabaseUrl());
                importRecordPartitioner.setDataSource(this.getDataSource());
                importRecordPartitioner.setOperationId(this.getOperationId());
            } else {
                throw new IllegalArgumentException();
            }
        }
        return relType == RelType.REFERENCES;
    }

    public void setFromSourceSystem(String fromSourceSystem) {
        this.fromSourceSystem = fromSourceSystem;
    }

    public void setModelService(MetaModelServiceExt modelService) {
        this.modelService = modelService;
    }
}

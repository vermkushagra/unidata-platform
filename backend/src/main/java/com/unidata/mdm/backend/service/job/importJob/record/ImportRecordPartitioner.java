package com.unidata.mdm.backend.service.job.importJob.record;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.types.CodeAttributeAlias;
import com.unidata.mdm.backend.exchange.def.ClassifierMapping;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.importJob.ImportPartitioner;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;


@StepScope
public class ImportRecordPartitioner extends ImportPartitioner<DbExchangeEntity> {

    @Autowired
    private MetaModelServiceExt modelService;

    private EntityRowMapper entityRowMapper = null;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        return super.partition(gridSize);
    }

    @Override
    public void addSpecialStepParameters(ExecutionContext context, DbExchangeEntity definition) {
        String entityName = definition.getName();
        context.put("rowMapper", getEntityRowMapper(definition));

        Collection<CodeAttributeAlias> aliasCodeAttributePointers = definition.getFields().stream()
                .filter(field -> field.getRefToAttribute() != null)
                .map(field -> new CodeAttributeAlias(field.getRefToAttribute(), field.getName()))
                .collect(Collectors.toList());

        context.put("aliasCodeAttributes", aliasCodeAttributePointers);
        
        context.putString("entityName", entityName);
        context.put("skipCleanse", definition.isSkipCleanse());
        context.putString("sourceSystem", definition.getSourceSystem());
        context.put("classifiers", definition.getClassifierMappings());

        //put default dates!
        VersionRange versionRange = definition.getVersionRange();
        processVersionRange(context, versionRange);
    }

    @Nonnull
    @Override
    public String getSql(@Nonnull DbExchangeEntity tableDefinition, long offset, long limit) {


        StringBuilder sqlb = new StringBuilder().append("select ");
        if (tableDefinition.getVersionRange() != null) {

            DbExchangeField fromField = (DbExchangeField) tableDefinition.getVersionRange().getValidFrom();
            DbExchangeField toField = (DbExchangeField) tableDefinition.getVersionRange().getValidTo();
            DbExchangeField active = (DbExchangeField) tableDefinition.getVersionRange().getIsActive();

            sqlb
                    .append(fromField != null ? fromField.getColumn() : "null")
                    .append(" as ")
                    .append(fromField != null ? fromField.getAlias() : "FROM_FIELD")
                    .append(", ");

            sqlb
                    .append(toField != null ? toField.getColumn() : "null")
                    .append(" as ")
                    .append(toField != null ? toField.getAlias() : "TO_FIELD")
                    .append(", ");

            if (active != null) {
                sqlb
                        .append(active != null ? active.getColumn() : "null")
                        .append(" as ")
                        .append(active != null ? active.getAlias() : "ACTIVE_FIELD, ")
                        .append(", ");
            }
        }

        DbNaturalKey naturalKey = (DbNaturalKey) tableDefinition.getNaturalKey();
        if (naturalKey != null){
        	sqlb
                .append(naturalKey.getSqlAdditionLeft() != null ? naturalKey.getSqlAdditionLeft() : "")
                .append(" ")
                .append(naturalKey.getColumn())
                .append(" ")
                .append(naturalKey.getSqlAdditionRight() != null ? naturalKey.getSqlAdditionRight() : "")
                .append(" as ")
                .append(naturalKey.getAlias())
                .append(", ");
        }

        DbSystemKey systemKey = (DbSystemKey) tableDefinition.getSystemKey();
        if (Objects.nonNull(systemKey)) {
            sqlb
                .append(systemKey.getSqlAdditionLeft() != null ? systemKey.getSqlAdditionLeft() : "")
                .append(" ")
                .append(systemKey.getColumn())
                .append(" ")
                .append(systemKey.getSqlAdditionRight() != null ? systemKey.getSqlAdditionRight() : "")
                .append(" as ")
                .append(systemKey.getAlias())
                .append(", ");
        }

        if (!CollectionUtils.isEmpty(tableDefinition.getClassifierMappings())) {

            List<ClassifierMapping> classifiers = tableDefinition.getClassifierMappings() ;
            for (ClassifierMapping classifierMapping : classifiers) {

                sqlb
                      .append(classifierMapping.getNodeId() != null ? ((DbExchangeField)classifierMapping.getNodeId()).getColumn() : "null")
                      .append(" as ")
                      .append(classifierMapping.getNodeId() != null  ? ((DbExchangeField)classifierMapping.getNodeId()).getAlias() : "null")
                      .append(", ");

                if(!org.apache.commons.collections.CollectionUtils.isEmpty(classifierMapping.getFields())){
                    List<ExchangeField> fields = classifierMapping.getFields();
                    for (ExchangeField field : fields) {
                        sqlb
                               .append(field != null ? ((DbExchangeField)field).getColumn() : "null")
                               .append(" as ")
                               .append(field != null  ? ((DbExchangeField)field).getAlias() : "null")
                               .append(", ");
                    }

                }
            }
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
                    .append(naturalKey.getColumn())
                    .append(" in (select distinct ")
                    .append(naturalKey.getColumn())
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
                sqlb.append(!CollectionUtils.isEmpty(tableDefinition.getJoins()) ? "and " : "where ")
                        .append(tableDefinition.getLimitPredicate())
                        .append(" ")
                        .append(offset)
                        .append(" ");
            }

            sqlb
                    .append("order by ")
                    .append(naturalKey.getColumn())
                    .append(" asc ");

            if (limit > 0) {
                sqlb.append("limit ")
                        .append(limit);
            }

            if (offset > 0 && tableDefinition.getLimitPredicate() == null) {
                sqlb.append(" offset ")
                        .append(offset);
            }

            sqlb.append(") ");

            if (tableDefinition.getOrderBy() != null) {
                sqlb
                        .append("order by ")
                        .append(tableDefinition.getOrderBy())
                        .append(" ");
            }
        } else {

            if (tableDefinition.getLimitPredicate() != null && offset > 0) {
                sqlb.append(!CollectionUtils.isEmpty(tableDefinition.getJoins()) ? "and " : "where ")
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
    }

    @Override
    public String getEntityName(DbExchangeEntity baseDefinition) {
        return baseDefinition.getName();
    }

    @Nonnull
    @Override
    public List<String> getTables(@Nonnull DbExchangeEntity tableDefinition) {
        return tableDefinition.getTables() == null ? Collections.emptyList() : tableDefinition.getTables();
    }

    @Nonnull
    @Override
    public List<String> getJoins(@Nonnull DbExchangeEntity tableDefinition) {
        return tableDefinition.getJoins() == null ? Collections.emptyList() : tableDefinition.getJoins();
    }

    /**
     * Create entity row mapper otherwise use created before!
     *
     * @param definition - business mapping
     * @return row mapper
     */
    public EntityRowMapper getEntityRowMapper(DbExchangeEntity definition) {
        if (Objects.isNull(entityRowMapper)) {
            String entityName = definition.getName();
            boolean isEntity = modelService.isEntity(entityName);
            Map<String, AttributeInfoHolder> attrs = isEntity ? modelService.getValueById(entityName, EntityWrapper.class).getAttributes() : modelService.getValueById(entityName, LookupEntityWrapper.class).getAttributes();
            entityRowMapper = new EntityRowMapper(definition, attrs);
        }
        return entityRowMapper;
    }

    public void setModelService(MetaModelServiceExt modelService) {
        this.modelService = modelService;
    }

}

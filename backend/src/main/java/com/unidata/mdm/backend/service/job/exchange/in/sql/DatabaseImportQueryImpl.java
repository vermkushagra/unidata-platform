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

package com.unidata.mdm.backend.service.job.exchange.in.sql;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

import com.unidata.mdm.backend.dao.util.DatabaseVendor;
import com.unidata.mdm.backend.exchange.def.ClassifierMapping;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import static com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobUtils.getFieldAlias;

/**
 * @author Mikhail Mikhailov
 * @author Alexey Tsarapkin
 *
 * __Query for example:
 *
 *  select null as VALID_FROM,
 *         null as VALID_TO,
 *         t.id  as KEY,
 *         t.id as id,
 *         '__LAST_FIELD__' as a_2b93720662f35a14b69844a008a5
 *  from UD_TEST t
 *  order by t.id asc
 *  offset 4 rows fetch next 2 rows only
 *
 * __Predicate for example:
 *
 *  select null as VALID_FROM,
 *         null as VALID_TO,
 *         t.id  as KEY,
 *         t.id as id,
 *         '__LAST_FIELD__' as a_3a5faabe7aad2cf016e5fd4b002c
 *  from UD_TEST t
 *  where t.id in (select distinct t.id from UD_TEST t order by t.id asc  offset 8 rows fetch next 2 rows only )
 */
public class DatabaseImportQueryImpl implements DatabaseImportQuery {

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getSql(@Nonnull DbExchangeEntity entity, long offset, long limit, DatabaseVendor vendor, Date previousSuccessStartDate) {

        StringBuilder sqlb = new StringBuilder().append("select ");

        applyVersionRange(sqlb, entity.getVersionRange());
        applyNaturalKey(sqlb, (DbNaturalKey) entity.getNaturalKey());
        applySystemKey(sqlb, (DbSystemKey) entity.getSystemKey());
        applyClassifiers(sqlb, entity.getClassifierMappings());
        applyFields(sqlb, entity.getFields());

        sqlb.append(getPredicateSql(entity, limit, offset, vendor, previousSuccessStartDate));

        // Historical doesn't append the order by clause due to usage in count queries. Append it.
        if (entity.isMultiVersion() && entity.getLimitPredicate() == null && entity.getOrderBy() != null) {
            sqlb
                    .append("order by ")
                    .append(entity.getOrderBy())
                    .append(" ");
        }

        return sqlb.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getSql(@Nonnull ExchangeRelation relation, long offset, long limit, DatabaseVendor databaseVendor, Date previousSuccessStartDate) {

        if (relation instanceof ContainmentRelation) {
            ContainmentRelation tableDefinition = (ContainmentRelation) relation;
            DbExchangeEntity dbExchangeEntity = (DbExchangeEntity) tableDefinition.getEntity();
            return getSql(dbExchangeEntity, offset, limit, databaseVendor, previousSuccessStartDate);
        }

        DbRelatesToRelation relTo = (DbRelatesToRelation) relation;
        StringBuilder sqlb = new StringBuilder().append("select ");

        applyRelationVersionRange(sqlb, relTo.getVersionRange());
        applyNaturalKey(sqlb, (DbNaturalKey) relTo.getFromNaturalKey());
        applySystemKey(sqlb, (DbSystemKey) relTo.getFromSystemKey());
        applyNaturalKey(sqlb, (DbNaturalKey) relTo.getToNaturalKey());
        applySystemKey(sqlb, (DbSystemKey) relTo.getToSystemKey());
        applyFields(sqlb, relTo.getFields());

        sqlb.append(getPredicateSql(relation, limit, offset, databaseVendor, previousSuccessStartDate));

        // Historical doesn't append the order by clause due to usage in count queries. Append it.
        if (relTo.isMultiVersion() && relTo.getOrderBy() != null) {
            sqlb
                    .append("order by ")
                    .append(relTo.getOrderBy());
        }

        return sqlb.toString();
    }


    protected void applyVersionRange(StringBuilder sqlb, VersionRange versionRange) {
        if (versionRange == null) {
            return;
        }
        DbExchangeField fromField = (DbExchangeField) versionRange.getValidFrom();
        DbExchangeField toField = (DbExchangeField) versionRange.getValidTo();
        DbExchangeField active = (DbExchangeField) versionRange.getIsActive();

        sqlb
                .append(fromField != null ? fromField.getColumn() : "null")
                .append(" as ")
                .append(fromField != null ? getFieldAlias(fromField) : "FROM_FIELD")
                .append(", ");

        sqlb
                .append(toField != null ? toField.getColumn() : "null")
                .append(" as ")
                .append(toField != null ? getFieldAlias(toField) : "TO_FIELD")
                .append(", ");

        if (active != null) {
            sqlb
                    .append(active != null ? active.getColumn() : "null")
                    .append(" as ")
                    .append(active != null ? getFieldAlias(active) : "ACTIVE_FIELD")
                    .append(", ");
        }
    }

    protected void applyRelationVersionRange(StringBuilder sqlb, VersionRange versionRange) {
        if (versionRange == null) {
            return;
        }

        DbExchangeField fromField = (DbExchangeField) versionRange.getValidFrom();
        DbExchangeField toField = (DbExchangeField) versionRange.getValidTo();
        DbExchangeField active = (DbExchangeField) versionRange.getIsActive();

        sqlb
                .append(fromField != null ? fromField.getColumn() : "null")
                .append(" as ")
                .append(fromField != null ? getFieldAlias(fromField) : "FROM_FIELD")
                .append(", ");

        sqlb
                .append(toField != null ? toField.getColumn() : "null")
                .append(" as ")
                .append(toField != null ? getFieldAlias(toField) : "TO_FIELD")
                .append(", ");

        if (active != null) {
            sqlb
                    .append(active != null ? active.getColumn() : "null")
                    .append(" as ")
                    .append(active != null ? getFieldAlias(active) : "ACTIVE_RELTO_FIELD")
                    .append(", ");
        }
    }

    protected void applyNaturalKey(StringBuilder sqlb, DbNaturalKey naturalKey) {
        if (naturalKey != null) {
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

    }

    protected void applySystemKey(StringBuilder sqlb, DbSystemKey systemKey) {
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

    }

    protected void applyClassifiers(StringBuilder sqlb, List<ClassifierMapping> classifiers) {
        if (CollectionUtils.isEmpty(classifiers)) {
            return;
        }

        for (ClassifierMapping classifierMapping : classifiers) {

            DbExchangeField nodeId = (DbExchangeField) classifierMapping.getNodeId();
            sqlb
                    .append(nodeId != null ? nodeId.getColumn() : "null")
                    .append(" as ")
                    .append(nodeId != null ? getFieldAlias(nodeId) : "null")
                    .append(", ");

            if (Objects.nonNull(classifierMapping.getVersionRange())
                    && Objects.nonNull(classifierMapping.getVersionRange().getIsActive())) {

                DbExchangeField active = (DbExchangeField) classifierMapping.getVersionRange().getIsActive();
                sqlb
                        .append(active != null ? active.getColumn() : "null")
                        .append(" as ")
                        .append(active != null ? getFieldAlias(active) : "ACTIVE_CLASSIFIER_FIELD")
                        .append(", ");
            }

            if (!org.apache.commons.collections.CollectionUtils.isEmpty(classifierMapping.getFields())) {

                List<ExchangeField> fields = classifierMapping.getFields();
                for (ExchangeField field : fields) {
                    DbExchangeField classifierField = (DbExchangeField) field;
                    sqlb
                            .append(classifierField != null ? classifierField.getColumn() : "null")
                            .append(" as ")
                            .append(classifierField != null ? getFieldAlias(classifierField) : "null")
                            .append(", ");
                }

            }
        }

    }

    protected void applyFields(StringBuilder sqlb, List<ExchangeField> fields) {
        for (int i = 0; fields != null && i < fields.size(); i++) {

            DbExchangeField def = (DbExchangeField) fields.get(i);
            sqlb
                    .append(def.getColumn())
                    .append(" as ")
                    .append(getFieldAlias(def))
                    .append(", ");
        }

        sqlb
                .append("'__LAST_FIELD__'")
                .append(" as ")
                .append(ImportDataJobConstants.IMPORT_JOB_LAST_FIELD_INDICATOR)
                .append(" ");
    }


    /**
     * Gets the full predicate SQL for an entity including historical section.
     *
     * @param entity the entity
     * @param limit the limit
     * @param offset the offset
     * @param vendor DB vendor
     * @return predicate SQL
     */
    protected String getPredicateSql(DbExchangeEntity entity, long limit, long offset, DatabaseVendor vendor, Date previousSuccessStartDate) {

        StringBuilder sqlb = new StringBuilder();
        String from = getFromSql(
                entity.getTables() == null ? Collections.emptyList() : entity.getTables(),
                entity.getJoins() == null ? Collections.emptyList() : entity.getJoins(),
                vendor, previousSuccessStartDate);

        sqlb.append(from);

        String keyColumn = entity.getXlsxKey();
        if (Objects.isNull(keyColumn)) {
            keyColumn = Objects.nonNull(entity.getNaturalKey())
                    ? ((DbNaturalKey) entity.getNaturalKey()).getColumn()
                    : ((DbSystemKey) entity.getSystemKey()).getColumn();
        }

        if (entity.isMultiVersion() && (entity.getLimitPredicate() == null || entity.getOrderBy() == null)) {

            sqlb
                    .append(entity.getJoins() != null && !entity.getJoins().isEmpty() ? "and " : "where ")
                    .append(keyColumn)
                    .append(" in (select distinct ")
                    .append(keyColumn)
                    .append(" ");

            String innerFrom = getFromSql(
                    entity.getTables() == null ? Collections.emptyList() : entity.getTables(),
                    entity.getJoins() == null ? Collections.emptyList() : entity.getJoins(),
                    vendor, previousSuccessStartDate);


            sqlb.append(innerFrom);


            addOrderByAndOffset(sqlb, !CollectionUtils.isEmpty(entity.getJoins()), keyColumn, entity.getLimitPredicate(),
                    offset, limit, vendor);
            sqlb.append(") ");

        } else {
            String orderBy = entity.getOrderBy() != null ? entity.getOrderBy() : keyColumn;
            addOrderByAndOffset(sqlb, !CollectionUtils.isEmpty(entity.getJoins()), orderBy, entity.getLimitPredicate(),
                    offset, limit, vendor);
        }

        return sqlb.toString();
    }

    /**
     * Gets the full predicate SQL for an entity including historical section.
     *
     * @param limit the limit
     * @param offset the offset
     * @param vendor DB vendor
     * @param relation the relation
     * @return predicate SQL
     */
    protected String getPredicateSql(ExchangeRelation relation, long limit, long offset, DatabaseVendor vendor, Date previousSuccessStartDate) {

        if (relation instanceof ContainmentRelation) {
            ContainmentRelation tableDefinition = (ContainmentRelation) relation;
            DbExchangeEntity dbExchangeEntity = (DbExchangeEntity) tableDefinition.getEntity();
            return getPredicateSql(dbExchangeEntity, offset, limit, vendor, previousSuccessStartDate);
        }

        DbRelatesToRelation relTo = (DbRelatesToRelation) relation;
        StringBuilder sqlb = new StringBuilder();

        String from = getFromSql(
                relTo.getTables() != null ? relTo.getTables() : Collections.emptyList(),
                relTo.getJoins() != null ? relTo.getJoins() : Collections.emptyList(),
                vendor, null);



        sqlb.append(from);

        String keyColumn = Objects.nonNull(relTo.getFromNaturalKey())
                ? ((DbNaturalKey) relTo.getFromNaturalKey()).getColumn()
                : ((DbSystemKey) relTo.getFromSystemKey()).getColumn();

        if (relTo.isMultiVersion()) {

            sqlb
                    .append(relTo.getJoins() != null && !relTo.getJoins().isEmpty() ? "and " : "where ")
                    .append(keyColumn)
                    .append(" in (select distinct ")
                    .append(keyColumn)
                    .append(" ");

            String innerFrom = getFromSql(
                    relTo.getTables() != null ? relTo.getTables() : Collections.emptyList(),
                    relTo.getJoins() != null ? relTo.getJoins() : Collections.emptyList(),
                    vendor, null);

            sqlb.append(innerFrom);

            addOrderByAndOffset(sqlb, !CollectionUtils.isEmpty(relTo.getJoins()), keyColumn, relTo.getLimitPredicate(),
                    offset, limit, vendor);

            sqlb.append(") ");

        } else {
            addOrderByAndOffset(sqlb, !CollectionUtils.isEmpty(relTo.getJoins()), keyColumn, relTo.getLimitPredicate(),
                    offset, limit, vendor);
        }

        return sqlb.toString();
    }

    protected void addOrderByAndOffset(StringBuilder sqlb, boolean hasJoins, String orderByColumn,
                                       String limitPredicate, long offset, long limit, DatabaseVendor vendor) {

        if (limitPredicate != null && offset > 0) {
            sqlb.append(hasJoins ? "and " : "where ")
                    .append(limitPredicate)
                    .append(" ")
                    .append(offset)
                    .append(" ");
        }
        addOrderBy(sqlb, orderByColumn);

        sqlb.append(" offset ")
                .append(limitPredicate == null ? offset : 0)
                .append(" rows ");

        if (limit > 0) {
            sqlb.append("fetch next ")
                    .append(limit)
                    .append(" rows only ");
        }
    }

    protected void addOrderBy(StringBuilder sqlb, String orderByColumn) {
        if (StringUtils.isNotBlank(orderByColumn)) {
            sqlb.append("order by ")
                    .append(orderByColumn)
                    .append(" asc ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFromSql(@Nonnull List<String> tables, @Nonnull List<String> joins, DatabaseVendor databaseVendor, Date previousSuccessStartDate) {


                StringBuilder sqlb = new StringBuilder().append("from ");
        for (int i = 0; i < tables.size(); i++) {

            String tbl = tables.get(i);
            String delimiter = ", ";
            if ((i == tables.size() - 1)
                    || (tbl.contains(" join ") || tbl.contains(" JOIN "))) {
                delimiter = " ";
            }

            sqlb.append(tbl)
                    .append(delimiter);
        }

        for (int i = 0; i < joins.size(); i++) {
            String j = joins.get(i);
            if (j.contains("$$" + JobCommonParameters.PARAM_PREVIOUS_SUCCESS_START_DATE)) {
                if (previousSuccessStartDate != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                    String formatedDateTime = LocalDateTime.ofInstant(previousSuccessStartDate.toInstant(), ZoneId.systemDefault()).format(formatter);
                    String lastTimestamp = "to_timestamp('" + formatedDateTime + "', 'YYYY-MM-DD HH24:MI:SS.MS')";
                    j = j.replace("$$" + JobCommonParameters.PARAM_PREVIOUS_SUCCESS_START_DATE, lastTimestamp);
                    sqlb.append(i == 0 ? "where " : "and ").append(j).append(" ");
                } else if (i == 0 ) {
                    sqlb.append(" where (1=1) ");
                }
            } else {
                sqlb.append(i == 0 ? "where " : "and ").append(j).append(" ");
            }
        }


        return sqlb.toString();
    }
}

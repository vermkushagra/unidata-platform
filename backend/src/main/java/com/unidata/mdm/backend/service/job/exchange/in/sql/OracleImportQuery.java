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
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import static com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobUtils.prepareTaggedQuery;

/**
 * @author Alexey Tsarapkin
 *
 * __Query for example:
 * select * from ( select null as VALID_FROM,
 *                        null as VALID_TO,
 *                        t.id as KEY_,
 *                        t.id as id,
 *                        '__LAST_FIELD__' as a_ede0268cbb1abee64eddf3f67d04,
 *                        row_number()over(order by t.id) rnum_
 *                 from UD_TEST t)
 * where rnum_>6 and rnum_<=8;
 *
 * __Predicate for example:
 *
 * select null as VALID_FROM,
 *        null as VALID_TO,
 *        t.id as KEY,
 *        t.id as id,
 *        '__LAST_FIELD__' as a_4c1057f50591c75d183d7971b307
 * from UD_TEST t
 * where t.id in
 *        (select key_ from
 *               (select key_, row_number()over(order by key_) rnum_
 *                  from (select distinct t.id as key_ from UD_TEST t )
 *               )
 *          where rnum_>8 and rnum_<=10
 *         )
 *
 */
public class OracleImportQuery extends DatabaseImportQueryImpl {

    private static final String ROW_NUMBER_COLUMN = "rnum_";
    private static final String KEY_COLUMN = "key_";

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getSql(@Nonnull DbExchangeEntity entity, long offset, long limit, DatabaseVendor vendor, Date previousSuccessStartDate) {


        StringBuilder sqlb = new StringBuilder().append("select ");
        String keyColumn = Objects.nonNull(entity.getNaturalKey())
                ? ((DbNaturalKey) entity.getNaturalKey()).getColumn()
                : ((DbSystemKey) entity.getSystemKey()).getColumn();

        boolean withLimitPredicate = StringUtils.isNotEmpty(entity.getLimitPredicate()) && StringUtils.isNotEmpty(entity.getOrderBy());
        if (!withDistinct(entity) && limit > 0) {
            keyColumn = StringUtils.isNoneBlank(entity.getOrderBy()) ? entity.getOrderBy() : keyColumn;
            if(!withLimitPredicate) {
                sqlb.append(" row_number()over(order by " + keyColumn + ") " + ROW_NUMBER_COLUMN + ", ");
            }
        }

        applyVersionRange(sqlb, entity.getVersionRange());
        applyNaturalKey(sqlb, (DbNaturalKey) entity.getNaturalKey());
        applySystemKey(sqlb, (DbSystemKey) entity.getSystemKey());
        applyClassifiers(sqlb, entity.getClassifierMappings());
        applyFields(sqlb, entity.getFields());

        sqlb.append(getPredicateSql(entity, limit, offset, vendor, previousSuccessStartDate));

        if (!withDistinct(entity) && limit > 0) {

            if (withLimitPredicate) {
                sqlb.append(CollectionUtils.isNotEmpty(entity.getJoins())?" and ":" where ")
                        .append(entity.getLimitPredicate())
                        .append(" ")
                        .append(offset)
                        .append(" and ")
                        .append(entity.getOrderBy())
                        .append(" <= ")
                        .append(limit+offset)
                        .append(" order by ")
                        .append(entity.getOrderBy())
                        .append(" asc ");
                return sqlb.toString();
            }

            return "select * from (" + sqlb.toString() + ")"
                    +" where " + (offset > 0 ? ROW_NUMBER_COLUMN + ">" + offset : "")
                    + (limit > 0 ? offset > 0 ? " and " + ROW_NUMBER_COLUMN + "<=" + (offset + limit) : " " +
                    ROW_NUMBER_COLUMN + "<=" + (limit) : "");

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

        String keyColumn = Objects.nonNull(relTo.getFromNaturalKey())
                ? ((DbNaturalKey) relTo.getFromNaturalKey()).getColumn()
                : ((DbSystemKey) relTo.getFromSystemKey()).getColumn();

        if ((!relTo.isMultiVersion() || (relTo.getLimitPredicate() != null || relTo.getOrderBy() != null)) && (limit > 0)) {
            sqlb.append(" row_number()over(order by " + keyColumn + ") " + ROW_NUMBER_COLUMN + ", ");
        }

        applyRelationVersionRange(sqlb, relTo.getVersionRange());

        applyNaturalKey(sqlb, (DbNaturalKey) relTo.getFromNaturalKey());
        applySystemKey(sqlb, (DbSystemKey) relTo.getFromSystemKey());

        applyNaturalKey(sqlb, (DbNaturalKey) relTo.getToNaturalKey());
        applySystemKey(sqlb, (DbSystemKey) relTo.getToSystemKey());

        applyFields(sqlb, relTo.getFields());

        sqlb.append(getPredicateSql(relation, limit, offset, databaseVendor, previousSuccessStartDate));

        if (!withDistinct(relTo) && (limit > 0)) {
            return "select * from (" + sqlb.toString() + ") where " + (offset > 0 ? ROW_NUMBER_COLUMN + ">" + offset : "")
                    + (limit > 0 ? offset > 0 ? " and " + ROW_NUMBER_COLUMN + "<=" + (offset + limit) : " " + ROW_NUMBER_COLUMN + "<=" + (limit) : "");
        }
        return sqlb.toString();
    }

    @Override
    protected String getPredicateSql(DbExchangeEntity entity, long limit, long offset, DatabaseVendor vendor, Date previousSuccessStartDate) {

        StringBuilder sqlb = new StringBuilder();
        String from = getFromSql(
                entity.getTables() == null ? Collections.emptyList() : entity.getTables(),
                entity.getJoins() == null ? Collections.emptyList() : entity.getJoins(),
                vendor, previousSuccessStartDate);

        sqlb.append(from);

        String keyColumn = Objects.nonNull(entity.getNaturalKey())
                ? ((DbNaturalKey) entity.getNaturalKey()).getColumn()
                : ((DbSystemKey) entity.getSystemKey()).getColumn();

        if (withDistinct(entity)) {

            String innerFrom = getFromSql(
                    entity.getTables() == null ? Collections.emptyList() : entity.getTables(),
                    entity.getJoins() == null ? Collections.emptyList() : entity.getJoins(),
                    vendor, previousSuccessStartDate);

            sqlb
                    .append(entity.getJoins() != null && !entity.getJoins().isEmpty() ? "and " : "where ")
                    .append(keyColumn)
                    .append(" in (select " + KEY_COLUMN + " from(select ")
                    .append(KEY_COLUMN)
                    .append(", row_number()over(order by " + KEY_COLUMN + ") " + ROW_NUMBER_COLUMN + " from")
                    .append("(select distinct " + keyColumn + " as " + KEY_COLUMN + " " + innerFrom + ")")
                    .append(")");
            if (offset > 0 || limit > 0) {
                sqlb.append(" where ")
                        .append(offset > 0 ? ROW_NUMBER_COLUMN + ">" + offset : "")
                        .append(limit > 0 ? offset > 0 ? " and " + ROW_NUMBER_COLUMN + "<=" + (offset + limit) : " " + ROW_NUMBER_COLUMN + "<=" + (limit) : "");
            }

            sqlb.append(") ");

        }

        return sqlb.toString();
    }

    @Override
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

        if (withDistinct(relTo)) {

            String innerFrom = getFromSql(
                    relTo.getTables() != null ? relTo.getTables() : Collections.emptyList(),
                    relTo.getJoins() != null ? relTo.getJoins() : Collections.emptyList(),
            vendor, null);

            sqlb
                    .append(relTo.getJoins() != null && !relTo.getJoins().isEmpty() ? "and " : "where ")
                    .append(keyColumn)
                    .append(" in (select " + KEY_COLUMN + " from(select ")
                    .append(KEY_COLUMN)
                    .append(", row_number()over(order by " + KEY_COLUMN + ") " + ROW_NUMBER_COLUMN + " from")
                    .append("(select distinct " + keyColumn + " as " + KEY_COLUMN + " " + innerFrom + ")")
                    .append(")");
            if (offset > 0 || limit > 0) {
                sqlb.append(" where ")
                        .append(offset > 0 ? ROW_NUMBER_COLUMN + ">" + offset : "")
                        .append(limit > 0 ? offset > 0 ? " and " + ROW_NUMBER_COLUMN + "<=" + (offset + limit) : " " + ROW_NUMBER_COLUMN + "<=" + (limit) : "");
            }
            sqlb.append(") ");
        }

        return sqlb.toString();
    }

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
                    String lastTimestamp = "to_timestamp('" + formatedDateTime + "', 'YYYY-MM-DD HH24:MI:SS.ff3')";
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

    private boolean withDistinct(ExchangeRelation relation) {
        return relation.isMultiVersion();
    }

    private boolean withDistinct(DbExchangeEntity entity) {
        return entity.isMultiVersion() && entity.getLimitPredicate() == null;
    }

}

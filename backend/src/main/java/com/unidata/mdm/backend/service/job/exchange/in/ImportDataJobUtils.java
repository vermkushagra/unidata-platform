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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.job.exchange.in.sql.DB2ImportQuery;
import com.unidata.mdm.backend.service.job.exchange.in.sql.DatabaseImportQuery;
import com.unidata.mdm.backend.service.job.exchange.in.sql.DatabaseImportQueryImpl;
import com.unidata.mdm.backend.service.job.exchange.in.sql.OracleImportQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.dao.util.DatabaseVendor;
import com.unidata.mdm.backend.exchange.def.ClassifierMapping;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.common.JobRuntimeUtils;

/**
 * @author Mikhail Mikhailov
 * Base class for import data step chain members.
 */
public class ImportDataJobUtils extends JobRuntimeUtils {
    /**
     * Import schema prefix.
     */
    private static final String IMPORT_SCHEMA_PREFIX = "unidata_data_import._";
    private static final DatabaseImportQuery DEFAULT_IMPORT_QUERY = new DatabaseImportQueryImpl();
    private static final DatabaseImportQuery ORACLE_IMPORT_QUERY = new OracleImportQuery();
    private static final DatabaseImportQuery DB2_IMPORT_QUERY = new DB2ImportQuery();

    /**
     * Constructor.
     */
    private ImportDataJobUtils() {
        super();
    }

    @Nonnull
    private static DatabaseImportQuery getImportQuery(DatabaseVendor vendor) {
        if (vendor == null) {
            return DEFAULT_IMPORT_QUERY;
        }
        switch (vendor) {
            case ORACLE:
                return ORACLE_IMPORT_QUERY;
            case DB2_JCC:
            case DB2_JDBC:
                return DB2_IMPORT_QUERY;
            default:
                return DEFAULT_IMPORT_QUERY;
        }
    }

    /**
     * Generates SQL.
     * @param entity the entity
     * @param offset the offset
     * @param limit the number of recxords to fetch
     * @param databaseVendor DB vendor
     * @return SQL string
     */
    @Nonnull
    public static String getSql(@Nonnull DbExchangeEntity entity, long offset, long limit, DatabaseVendor databaseVendor, Date previousSuccessStartDate) {
        return getImportQuery(databaseVendor).getSql(entity, offset, limit, databaseVendor, previousSuccessStartDate);
    }

    /**
     * Gets SQL for a relation.
     * @param relation the definition
     * @param offset the offset
     * @param limit the limit
     * @param databaseVendor database vendor
     * @return SQL string
     */
    @Nonnull
    public static String getSql(@Nonnull ExchangeRelation relation, long offset, long limit, DatabaseVendor databaseVendor, Date previousSuccessStartDate) {
        return getImportQuery(databaseVendor).getSql(relation, offset, limit, databaseVendor, previousSuccessStartDate);
    }
    /**
     * Selects alias.
     * @param field the field
     * @return alias
     */
    public static String getFieldAlias(@Nonnull DbExchangeField field) {
        return StringUtils.isBlank(field.getAlias()) ? StringUtils.trim(field.getColumn()) : StringUtils.trim(field.getAlias());
    }

    /**
     * @param tables - affected tables
     * @param joins  - applied joins
     * @return SQL request which contains from path of SQL query
     */
    public static String getFromSql(@Nonnull List<String> tables, @Nonnull List<String> joins, DatabaseVendor databaseVendor, Date previousSuccessStartDate) {
        return getImportQuery(databaseVendor).getFromSql(tables, joins, databaseVendor, previousSuccessStartDate);
    }

    /**
     * Replaces tag in a query string.
     * @param runId the run id
     * @param statement the statement
     * @return query string
     */
    public static String prepareTaggedQuery(String runId, String statement) {
        String tag = StringUtils.replace(runId, "-", "");
        return StringUtils.replace(statement, ImportDataJobConstants.RUN_ID_REPLACEMENT_TAG, tag);
    }

    /**
     * Creates target table name.
     * @param runId the runId
     * @param table name of the table
     * @return prepared name
     */
    public static String prepareTargetTableName(String runId, String table) {

        String tag = StringUtils.replace(runId, "-", "");
        return new StringBuilder()
                .append(IMPORT_SCHEMA_PREFIX)
                .append(tag)
                .append(table)
                .toString();
    }
}

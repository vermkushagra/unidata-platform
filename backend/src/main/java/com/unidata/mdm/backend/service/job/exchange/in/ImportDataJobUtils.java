package com.unidata.mdm.backend.service.job.exchange.in;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

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
import com.unidata.mdm.backend.service.job.JobCommonParameters;
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
    /**
     * Constructor.
     */
    private ImportDataJobUtils() {
        super();
    }
    /**
     * Generates SQL.
     * @param entity the entity
     * @param offset the offset
     * @param limit the number of recxords to fetch
     * @param vendor DB vendor
     * @return SQL string
     */
    @Nonnull
    public static String getSql(@Nonnull DbExchangeEntity entity, long offset, long limit, DatabaseVendor vendor, Date previousSuccessStartDate) {


        StringBuilder sqlb = new StringBuilder().append("select ");
        if (entity.getVersionRange() != null) {

            DbExchangeField fromField = (DbExchangeField) entity.getVersionRange().getValidFrom();
            DbExchangeField toField = (DbExchangeField) entity.getVersionRange().getValidTo();
            DbExchangeField active = (DbExchangeField) entity.getVersionRange().getIsActive();

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

        DbNaturalKey naturalKey = (DbNaturalKey) entity.getNaturalKey();
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

        DbSystemKey systemKey = (DbSystemKey) entity.getSystemKey();
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

        if (!CollectionUtils.isEmpty(entity.getClassifierMappings())) {

            List<ClassifierMapping> classifiers = entity.getClassifierMappings() ;
            for (ClassifierMapping classifierMapping : classifiers) {

                DbExchangeField nodeId = (DbExchangeField) classifierMapping.getNodeId();
                sqlb
                      .append(nodeId != null ? nodeId.getColumn() : "null")
                      .append(" as ")
                      .append(nodeId != null  ? getFieldAlias(nodeId) : "null")
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
                               .append(classifierField != null  ? getFieldAlias(classifierField) : "null")
                               .append(", ");
                    }

                }
            }
        }

        for (int i = 0; entity.getFields() != null && i < entity.getFields().size(); i++) {

            DbExchangeField def = (DbExchangeField) entity.getFields().get(i);
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

        sqlb.append(getPredicateSql(entity, limit, offset, vendor, previousSuccessStartDate));

        // Historical doesn't append the order by clause due to usage in count queries. Append it.
        if (entity.isMultiVersion() && entity.getOrderBy() != null) {
            sqlb
                    .append("order by ")
                    .append(entity.getOrderBy())
                    .append(" ");
        }

        return sqlb.toString();
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

        if (relation instanceof ContainmentRelation) {
            ContainmentRelation tableDefinition = (ContainmentRelation) relation;
            DbExchangeEntity dbExchangeEntity = (DbExchangeEntity) tableDefinition.getEntity();
            return getSql(dbExchangeEntity, offset, limit, databaseVendor, previousSuccessStartDate);
        }

        DbRelatesToRelation relTo = (DbRelatesToRelation) relation;
        StringBuilder sqlb = new StringBuilder().append("select ");

        if (relTo.getVersionRange() != null) {

            DbExchangeField fromField = (DbExchangeField) relTo.getVersionRange().getValidFrom();
            DbExchangeField toField = (DbExchangeField) relTo.getVersionRange().getValidTo();
            DbExchangeField active = (DbExchangeField) relTo.getVersionRange().getIsActive();

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

        // Natural from
        DbNaturalKey naturalFromKey = (DbNaturalKey) relTo.getFromNaturalKey();
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
        DbSystemKey systemFromKey = (DbSystemKey) relTo.getFromSystemKey();
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
        DbNaturalKey naturalToKey = (DbNaturalKey) relTo.getToNaturalKey();
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
        DbSystemKey systemToKey = (DbSystemKey) relTo.getToSystemKey();
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

        for (int i = 0; relTo.getFields() != null && i < relTo.getFields().size(); i++) {

            DbExchangeField def = (DbExchangeField) relTo.getFields().get(i);
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

        sqlb.append(getPredicateSql(relation, limit, offset, databaseVendor, previousSuccessStartDate));

        // Historical doesn't append the order by clause due to usage in count queries. Append it.
        if (relTo.isMultiVersion() && relTo.getOrderBy() != null) {
            sqlb
                .append("order by ")
                .append(relTo.getOrderBy());
        }

        return sqlb.toString();
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
     * Gets the full predicate SQL for an entity including historical section.
     * @param entity the entity
     * @param limit the limit
     * @param offset the offset
     * @param vendor DB vendor
     * @return predicate SQL
     */
    public static String getPredicateSql(DbExchangeEntity entity, long limit, long offset, DatabaseVendor vendor, Date previousSuccessStartDate) {

        StringBuilder sqlb = new StringBuilder();
        String from = getFromSql(
                entity.getTables() == null ? Collections.emptyList() : entity.getTables(),
                entity.getJoins() == null ? Collections.emptyList() : entity.getJoins(),
                		previousSuccessStartDate);

        sqlb.append(from);

        String keyColumn = Objects.nonNull(entity.getNaturalKey())
                ? ((DbNaturalKey) entity.getNaturalKey()).getColumn()
                : ((DbSystemKey) entity.getSystemKey()).getColumn();

        if (entity.isMultiVersion()) {

            sqlb
                .append(entity.getJoins() != null && !entity.getJoins().isEmpty() ? "and " : "where ")
                .append(keyColumn)
                .append(" in (select distinct ")
                .append(keyColumn)
                .append(" ");

            String innerFrom = getFromSql(
                    entity.getTables() == null ? Collections.emptyList() : entity.getTables(),
                    entity.getJoins() == null ? Collections.emptyList() : entity.getJoins(),
                    		previousSuccessStartDate);

            sqlb.append(innerFrom);

            if (entity.getLimitPredicate() != null && offset > 0) {
                sqlb.append(!CollectionUtils.isEmpty(entity.getJoins()) ? "and " : "where ")
                    .append(entity.getLimitPredicate())
                    .append(" ")
                    .append(offset)
                    .append(" ");
            }

            sqlb
                .append("order by ")
                .append(keyColumn)
                .append(" asc ");

            sqlb.append(" offset ")
                .append(entity.getLimitPredicate() == null ? offset : 0)
                .append(" rows ");

            if (limit > 0) {
                sqlb.append("fetch next ")
                    .append(limit)
                    .append(" rows only ");
            }

            sqlb.append(") ");

        } else {

            if (entity.getLimitPredicate() != null && offset > 0) {
                sqlb.append(!CollectionUtils.isEmpty(entity.getJoins()) ? "and " : "where ")
                    .append(entity.getLimitPredicate())
                    .append(" ")
                    .append(offset)
                    .append(" ");
            }

            String orderBy = entity.getOrderBy() != null ? entity.getOrderBy() : keyColumn;
            sqlb.append("order by ")
                .append(orderBy)
                .append(" ");

            sqlb.append(" offset ")
                .append(entity.getLimitPredicate() == null ? offset : 0)
                .append(" rows ");

            if (limit > 0) {
                sqlb.append("fetch next ")
                    .append(limit)
                    .append(" rows only");
            }
        }

        return sqlb.toString();
    }
    /**
     * Gets the full predicate SQL for an entity including historical section.
     * @param limit the limit
     * @param offset the offset
     * @param vendor DB vendor
     * @param entity the entity
     * @return predicate SQL
     */
    public static String getPredicateSql(ExchangeRelation relation, long limit, long offset, DatabaseVendor vendor, Date previousSuccessStartDate) {

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
                null);

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
                    null);

            sqlb.append(innerFrom);

            if (relTo.getLimitPredicate() != null && offset > 0) {
                sqlb.append(relTo.getJoins() != null && !relTo.getJoins().isEmpty() ? "and " : "where ")
                    .append(relTo.getLimitPredicate())
                    .append(" ")
                    .append(offset)
                    .append(" ");
            }

            sqlb.append("order by ")
                .append(keyColumn)
                .append(" asc ");

            sqlb.append(" offset ")
                .append(relTo.getLimitPredicate() == null ? offset : 0)
                .append(" rows ");

            if (limit > 0) {
                sqlb.append("fetch next ")
                    .append(limit)
                    .append(" rows only ");
            }

            sqlb.append(") ");

        } else {

            if (relTo.getLimitPredicate() != null && offset > 0) {
                sqlb.append(relTo.getJoins() != null && !relTo.getJoins().isEmpty() ? "and " : "where ")
                    .append(relTo.getLimitPredicate())
                    .append(" ")
                    .append(offset)
                    .append(" ");
            }

            String orderBy = relTo.getOrderBy() != null ? relTo.getOrderBy() : keyColumn;
            sqlb.append("order by ")
                .append(orderBy)
                .append(" ");

            sqlb.append(" offset ")
                .append(relTo.getLimitPredicate() == null ? offset : 0)
                .append(" rows ");

            if (limit > 0) {
                sqlb.append("fetch next ")
                    .append(limit)
                    .append(" rows only");
            }
        }

        return sqlb.toString();
    }
    /**
     * @param tables - affected tables
     * @param joins  - applied joins
     * @param previousSuccessStartDate - previous success start date
     * @return SQL request which contains from path of SQL query
     */
    public static String getFromSql(@Nonnull List<String> tables, @Nonnull List<String> joins, Date previousSuccessStartDate) {

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
				}
			} else {
				sqlb.append(i == 0 ? "where " : "and ").append(j).append(" ");
			}
		}

        return sqlb.toString();
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

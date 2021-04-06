package com.unidata.mdm.backend.service.job.importJob.relation;

import static java.util.Objects.isNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.importJob.AbstractRowMapper;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRelationSet;

public class ReferenceRelationRowMapper extends AbstractRowMapper<ImportRelationSet> {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2726588339664292053L;

    private DbRelatesToRelation rel;

    private Map<String, AttributeInfoHolder> attrs;

    private String entityName;

    public ReferenceRelationRowMapper(DbRelatesToRelation rel, Map<String, AttributeInfoHolder> attrs, String entityName) {
        this.rel = rel;
        this.attrs = attrs;
        this.entityName = entityName;
    }

    @Override
    public ImportRelationSet mapRow(ResultSet rs, int rowNum) throws SQLException {

        String originFromKey = importNaturalKey(rs, (DbNaturalKey) rel.getFromNaturalKey());
        String etalonFromKey = importSystemKey(rs, (DbSystemKey) rel.getFromSystemKey());
        if (Objects.isNull(originFromKey) && Objects.isNull(etalonFromKey)) {
            LOGGER.warn("Cannot determine relation FROM keys. Neither key is given. Skipping.");
            return null;
        }

        String originToKey = importNaturalKey(rs, (DbNaturalKey) rel.getToNaturalKey());
        String etalonToKey = importSystemKey(rs, (DbSystemKey) rel.getToSystemKey());
        if (Objects.isNull(originToKey) && Objects.isNull(etalonToKey)) {
            LOGGER.warn("Cannot determine relation TO keys. Neither key is given. Skipping.");
            return null;
        }

        DataRecord record = new SerializableDataRecord();
        for (ExchangeField f : rel.getFields()) {

            // Get value
            DbExchangeField dbf = (DbExchangeField) f;
            Object value;
            if (dbf.getValue() != null) {
                value = dbf.getValue().toString();
            } else {
                Class<?> typeClazz = dbf.getTypeClazz() == null ? getFieldClass(attrs.get(dbf.getName())) : dbf.getTypeClazz();
                if (typeClazz == null) {
                    throw new IllegalArgumentException("Cannot determine field type for ["
                            + rel.getRelation() + "." + dbf.getName() + "] field.");
                }

                value = getFieldValue(rs, dbf.getAlias(), typeClazz);
            }

            if (value == null) {
                continue;
            }

            // Resolve and set
            setAttribute(record, f, attrs, f.getName(), value, 0);
        }

        ImportRelationSet result = new ImportRelationSet();
        result.setData(record);
        result.setToOriginKey(OriginKey.builder()
                .entityName(entityName)
                .externalId(originToKey)
                .sourceSystem(rel.getToSourceSystem())
                .build());
        result.setToEtalonKey(EtalonKey.builder().id(etalonToKey).build());
        Pair<Date, Date> range = getDateRange(rs);
        result.setValidFrom(range.getLeft());
        result.setValidTo(range.getRight());
        result.setFromOriginKey(OriginKey.builder()
                .externalId(originFromKey)
                .build());
        result.setFromEtalonKey(EtalonKey.builder().id(etalonFromKey).build());
        result.setRelationName(rel.getRelation());

        return result;
    }

    /**
     * @param rs result set
     * @return date range!
     * @throws SQLException
     */
    @Nonnull
    private Pair<Date, Date> getDateRange(ResultSet rs) throws SQLException {
        VersionRange range = rel.getVersionRange();

        if (isNull(range)) {
            return Pair.of(null, null);
        }

        Date from = importRangeFrom(rs, range);
        Date to = importRangeTo(rs, range);
        return Pair.of(from, to);
    }
}

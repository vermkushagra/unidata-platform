package com.unidata.mdm.backend.service.job.exchange.in;

import static java.util.Objects.isNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeField;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportDataSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRelationSet;

public class ReferenceRelationRowMapper extends AbstractRowMapper<ImportDataSet> {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2726588339664292053L;

    private DbRelatesToRelation rel;

    private Map<String, AttributeInfoHolder> attrs;

    private String toEntityName;

    public ReferenceRelationRowMapper(DbRelatesToRelation rel, Map<String, AttributeInfoHolder> attrs, String entityName) {
        this.rel = rel;
        this.attrs = attrs;
        this.toEntityName = entityName;
    }

    @Override
    public ImportDataSet mapRow(ResultSet rs, int rowNum) throws SQLException {

        String originFromKey = importNaturalKey(rs, (DbNaturalKey) rel.getFromNaturalKey());
        String etalonFromKey = importSystemKey(rs, (DbSystemKey) rel.getFromSystemKey());
        if (Objects.isNull(originFromKey) && Objects.isNull(etalonFromKey)) {
            LOGGER.warn("Cannot determine relation FROM keys. Neither key is given. Skipping.");
            return ImportDataJobConstants.DUMMY_RECORD;
        }

        String originToKey = importNaturalKey(rs, (DbNaturalKey) rel.getToNaturalKey());
        String etalonToKey = importSystemKey(rs, (DbSystemKey) rel.getToSystemKey());
        if (Objects.isNull(originToKey) && Objects.isNull(etalonToKey)) {
            LOGGER.warn("Cannot determine relation TO keys. Neither key is given. Skipping.");
            return ImportDataJobConstants.DUMMY_RECORD;
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

                value = getFieldValue(rs, ImportDataJobUtils.getFieldAlias(dbf), typeClazz);
            }

            if (value == null) {
                continue;
            }

            // Resolve and set
            setAttribute(record, f, attrs, f.getName(), value, 0);
        }

        ImportRelationSet result = new ImportRelationSet(record);
        result.setToOriginKey(OriginKey.builder()
                .entityName(toEntityName)
                .externalId(originToKey)
                .sourceSystem(rel.getToSourceSystem())
                .build());
        result.setToEtalonKey(EtalonKey.builder().id(etalonToKey).build());
        result.setFromOriginKey(OriginKey.builder()
                .externalId(originFromKey)
                .build());
        result.setFromEtalonKey(EtalonKey.builder().id(etalonFromKey).build());
        result.setRelationName(rel.getRelation());

        addVersionRangeAndStatus(result, rs, rel);
        return result;
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
    private void addVersionRangeAndStatus(ImportRelationSet set, @Nonnull ResultSet rs, DbRelatesToRelation exchangeRelation)
            throws SQLException {

        VersionRange range = exchangeRelation.getVersionRange();
        if (isNull(range)) {
            return;
        }

        Date from = importRangeFrom(rs, range);
        Date to = importRangeTo(rs, range);
        RecordStatus status = importRangeStatus(rs, range);

        set.setStatus(status);
        set.setValidFrom(from);
        set.setValidTo(to);
    }
}

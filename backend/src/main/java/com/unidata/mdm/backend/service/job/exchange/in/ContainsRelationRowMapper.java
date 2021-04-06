package com.unidata.mdm.backend.service.job.exchange.in;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportDataSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRecordSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRelationSet;

public class ContainsRelationRowMapper extends AbstractRowMapper<ImportDataSet> {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -882114418487237088L;

    private ContainmentRelation rel;

    private EntityRowMapper entityRowMapper;


    public ContainsRelationRowMapper(ContainmentRelation rel, EntityRowMapper entityRowMapper) {
        this.rel = rel;
        this.entityRowMapper = entityRowMapper;
    }

    @Override
    public ImportDataSet mapRow(ResultSet rs, int rowNum) throws SQLException {

        ImportRecordSet ids = (ImportRecordSet) entityRowMapper.mapRow(rs, rowNum);
        if (ids == null) {
            return ImportDataJobConstants.DUMMY_RECORD;
        }

        ImportRelationSet result = new ImportRelationSet(ids.getData());
        result.setToEtalonKey(ids.getEtalonKey());
        result.setToOriginKey(ids.getOriginKey());

        String originFromKey = importNaturalKey(rs, (DbNaturalKey) rel.getFromNaturalKey());
        String etalonFromKey = importSystemKey(rs, (DbSystemKey) rel.getFromSystemKey());

        if (Objects.isNull(originFromKey) && Objects.isNull(etalonFromKey)) {
            LOGGER.warn("Cannot determine FROM keys. Neither etalon nor origin ext. ID was given. Skipping.");
            return ImportDataJobConstants.DUMMY_RECORD;
        }

        result.setFromOriginKey(OriginKey.builder().externalId(originFromKey).build());
        result.setFromEtalonKey(EtalonKey.builder().id(etalonFromKey).build());
        result.setValidFrom(ids.getValidFrom());
        result.setValidTo(ids.getValidTo());
        result.setStatus(ids.getStatus());
        result.setRelationName(rel.getRelation());

        return result;
    }
}

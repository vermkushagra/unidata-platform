package com.unidata.mdm.backend.service.job.importJob.relation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;
import com.unidata.mdm.backend.service.job.importJob.AbstractRowMapper;
import com.unidata.mdm.backend.service.job.importJob.record.EntityRowMapper;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRecordSet;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRelationSet;

public class ContainsRelationRowMapper extends AbstractRowMapper<ImportRelationSet> {

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
    public ImportRelationSet mapRow(ResultSet rs, int rowNum) throws SQLException {

        ImportRecordSet ids = entityRowMapper.mapRow(rs, rowNum);
        if (ids == null) {
            return null;
        }

        ImportRelationSet result = new ImportRelationSet();
        result.setData(ids.getData());
        result.setToEtalonKey(ids.getEtalonKey());
        result.setToOriginKey(ids.getOriginKey());

        String originFromKey = importNaturalKey(rs, (DbNaturalKey) rel.getFromNaturalKey());
        String etalonFromKey = importSystemKey(rs, (DbSystemKey) rel.getFromSystemKey());

        if (Objects.isNull(originFromKey) && Objects.isNull(etalonFromKey)) {
            LOGGER.warn("Cannot determine FROM keys. Neither etalon nor origin ext. ID was given. Skipping.");
            return null;
        }

        result.setFromOriginKey(OriginKey.builder().externalId(originFromKey).build());
        result.setFromEtalonKey(EtalonKey.builder().id(etalonFromKey).build());
        result.setValidFrom(ids.getValidFrom());
        result.setValidTo(ids.getValidTo());
        result.setRelationName(rel.getRelation());

        return result;
    }
}

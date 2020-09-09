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

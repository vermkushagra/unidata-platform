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

/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.OriginRelationPO;

/**
 * @author Mikhail Mikhailov
 * Etalon relations row mapper.
 */
public class OriginRelationRowMapper
    extends AbstractRowMapper<OriginRelationPO>
    implements RowMapper<OriginRelationPO> {

    /**
     * Default 'with data' row mapper.
     */
    public static final OriginRelationRowMapper DEFAULT_ORIGIN_RELATION_ROW_MAPPER
        = new OriginRelationRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<OriginRelationPO> DEFAULT_ORIGIN_RELATION_FIRST_RESULT_EXTRACTOR
        = rs -> rs != null && rs.next() ? DEFAULT_ORIGIN_RELATION_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    private OriginRelationRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRelationPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        OriginRelationPO po = new OriginRelationPO();

        super.mapRow(po, rs, rowNum);

        po.setId(rs.getString(OriginRelationPO.FIELD_ID));
        po.setEtalonId(rs.getString(OriginRelationPO.FIELD_ETALON_ID));
        po.setName(rs.getString(OriginRelationPO.FIELD_NAME));
        po.setOriginIdFrom(rs.getString(OriginRelationPO.FIELD_ORIGIN_ID_FROM));
        po.setOriginIdTo(rs.getString(OriginRelationPO.FIELD_ORIGIN_ID_TO));
        po.setSourceSystem(rs.getString(OriginRelationPO.FIELD_SOURCE_SYSTEM));
        po.setStatus(RecordStatus.valueOf(rs.getString(OriginRelationPO.FIELD_STATUS)));

        return po;
    }

}

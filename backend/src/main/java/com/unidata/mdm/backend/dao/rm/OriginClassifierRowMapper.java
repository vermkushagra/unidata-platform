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
import com.unidata.mdm.backend.po.OriginClassifierPO;

/**
 * @author Mikhail Mikhailov
 * Origin classifier data row mapper.
 */
public class OriginClassifierRowMapper
    extends AbstractRowMapper<OriginClassifierPO>
    implements RowMapper<OriginClassifierPO> {

    /**
     * Default 'with data' row mapper.
     */
    public static final OriginClassifierRowMapper DEFAULT_ORIGIN_CLASSIFIER_ROW_MAPPER
        = new OriginClassifierRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<OriginClassifierPO> DEFAULT_ORIGIN_CLASSIFIER_FIRST_RESULT_EXTRACTOR
        = rs -> rs != null && rs.next() ? DEFAULT_ORIGIN_CLASSIFIER_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    private OriginClassifierRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginClassifierPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        OriginClassifierPO po = new OriginClassifierPO();

        super.mapRow(po, rs, rowNum);

        po.setId(rs.getString(OriginClassifierPO.FIELD_ID));
        po.setEtalonId(rs.getString(OriginClassifierPO.FIELD_ETALON_ID));
        po.setName(rs.getString(OriginClassifierPO.FIELD_NAME));
        po.setNodeId(rs.getString(OriginClassifierPO.FIELD_NODE_ID));
        po.setOriginIdRecord(rs.getString(OriginClassifierPO.FIELD_ORIGIN_ID_RECORD));
        po.setSourceSystem(rs.getString(OriginClassifierPO.FIELD_SOURCE_SYSTEM));
        po.setStatus(RecordStatus.valueOf(rs.getString(OriginClassifierPO.FIELD_STATUS)));

        return po;
    }

}

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
import com.unidata.mdm.backend.po.EtalonDraftStatePO;
import com.unidata.mdm.backend.po.EtalonRelationDraftStatePO;


/**
 * @author Mikhail Mikhailov
 * Etalon relation draft state row mapper.
 */
public class EtalonRelationDraftStateRowMapper implements RowMapper<EtalonRelationDraftStatePO> {

        /**
     * Default row mapper.
     */
    public static final EtalonRelationDraftStateRowMapper DEFAULT_ROW_MAPPER = new EtalonRelationDraftStateRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<EtalonRelationDraftStatePO> DEFAULT_FIRST_RESULT_EXTRACTOR
        = rs -> rs.next() ? DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    private EtalonRelationDraftStateRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRelationDraftStatePO mapRow(ResultSet rs, int rowNum) throws SQLException {

        EtalonRelationDraftStatePO po = new EtalonRelationDraftStatePO();
        po.setCreateDate(rs.getTimestamp(EtalonDraftStatePO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(EtalonDraftStatePO.FIELD_CREATED_BY));
        po.setEtalonId(rs.getString(EtalonDraftStatePO.FIELD_ETALON_ID));
        po.setId(rs.getInt(EtalonDraftStatePO.FIELD_ID));
        po.setRevision(rs.getInt(EtalonDraftStatePO.FIELD_REVISION));
        po.setStatus(RecordStatus.valueOf(rs.getString(EtalonDraftStatePO.FIELD_STATUS)));

        return po;
    }

}
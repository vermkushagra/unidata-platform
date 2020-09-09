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

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.EtalonRelationPO;

/**
 * @author Mikhail Mikhailov
 * Etalon relations row mapper.
 */
public class EtalonRelationRowMapper
    extends AbstractRowMapper<EtalonRelationPO>
    implements RowMapper<EtalonRelationPO> {

    /**
     * Default 'with data' row mapper.
     */
    public static final EtalonRelationRowMapper DEFAULT_ETALON_RELATION_ROW_MAPPER
        = new EtalonRelationRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<EtalonRelationPO> DEFAULT_ETALON_RELATION_FIRST_RESULT_EXTRACTOR
        = rs -> rs != null && rs.next() ? DEFAULT_ETALON_RELATION_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    public EtalonRelationRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRelationPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        EtalonRelationPO po = new EtalonRelationPO();

        super.mapRow(po, rs, rowNum);

        po.setId(rs.getString(EtalonRelationPO.FIELD_ID));
        po.setName(rs.getString(EtalonRelationPO.FIELD_NAME));
        po.setEtalonIdFrom(rs.getString(EtalonRelationPO.FIELD_ETALON_ID_FROM));
        po.setEtalonIdTo(rs.getString(EtalonRelationPO.FIELD_ETALON_ID_TO));
        po.setStatus(RecordStatus.valueOf(rs.getString(EtalonRelationPO.FIELD_STATUS)));
        po.setApproval(ApprovalState.valueOf(rs.getString(EtalonRelationPO.FIELD_APPROVAL)));
        po.setGsn(rs.getLong(EtalonRelationPO.FIELD_GSN));
        po.setOperationId(rs.getString(EtalonRelationPO.FIELD_OPERATION_ID));

        return po;
    }

}

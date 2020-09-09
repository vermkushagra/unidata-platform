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
import com.unidata.mdm.backend.po.EtalonRecordPO;

/**
 * @author Mikhail Mikhailov
 * Row mapper for {@link EtalonRecordPO} objects.
 */
public class EtalonRecordRowMapper
    extends AbstractRowMapper<EtalonRecordPO>
    implements RowMapper<EtalonRecordPO> {

    /**
     * Default reusable row mapper.
     */
    public static final EtalonRecordRowMapper DEFAULT_ROW_MAPPER = new EtalonRecordRowMapper();

    /**
     * Extracts first result or returns null.
     */
    public static final ResultSetExtractor<EtalonRecordPO> DEFAULT_ETALON_FIRST_RESULT_EXTRACTOR
        = rs -> rs.next() ? DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Constructor.
     */
    public EtalonRecordRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRecordPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        EtalonRecordPO po = new EtalonRecordPO();

        super.mapRow(po, rs, rowNum);

        po.setId(rs.getString(EtalonRecordPO.FIELD_ID));
        po.setName(rs.getString(EtalonRecordPO.FIELD_NAME));
        po.setVersion(rs.getInt(EtalonRecordPO.FIELD_VERSION));
        po.setStatus(RecordStatus.valueOf(rs.getString(EtalonRecordPO.FIELD_STATUS)));
        po.setApproval(ApprovalState.valueOf(rs.getString(EtalonRecordPO.FIELD_APPROVAL)));
        po.setGsn(rs.getLong(EtalonRecordPO.FIELD_GSN));
        po.setOperationId(rs.getString(EtalonRecordPO.FIELD_OPERATION_ID));

        return po;
    }

}

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

import com.unidata.mdm.backend.common.types.VistoryOperationType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.po.OriginsVistoryRecordPO;
import com.unidata.mdm.backend.util.DumpUtils;

/**
 * @author Mikhail Mikhailov
 * Origins vistory row mapper.
 */
public class OriginVistoryRowMapper implements RowMapper<OriginsVistoryRecordPO> {

    /**
     * Default 'JAXB data' row mapper.
     */
    public static final OriginVistoryRowMapper DEFAULT_ORIGINS_VISTORY_JAXB_ROW_MAPPER
        = new OriginVistoryRowMapper(true, true, false);
    /**
     * Default 'binary protostuff data' row mapper.
     */
    public static final OriginVistoryRowMapper DEFAULT_ORIGINS_VISTORY_PROTOSTUFF_ROW_MAPPER
        = new OriginVistoryRowMapper(true, false, true);
    /**
     * Default 'without data' row mapper.
     */
    public static final OriginVistoryRowMapper DEFAULT_ORIGINS_VISTORY_NO_DATA_ROW_MAPPER
        = new OriginVistoryRowMapper(false, false, false);

    /**
     * Skip data or not.
     */
    private final boolean withData;

    /**
     * Read JAXB data.
     */
    private final boolean jaxbData;

    /**
     * Read binary protostuff data.
     */
    private final boolean protostuffData;

    /**
     * Constructor.
     */
    private OriginVistoryRowMapper(boolean withData, boolean jaxbData, boolean protostuffData) {
        super();
        this.withData = withData;
        this.jaxbData = jaxbData;
        this.protostuffData = protostuffData;
    }

    /**
     * @return the withData
     */
    public boolean isWithData() {
        return withData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginsVistoryRecordPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        OriginsVistoryRecordPO po = new OriginsVistoryRecordPO();

        // 1. Stat
        po.setCreateDate(rs.getTimestamp(OriginsVistoryRecordPO.FIELD_CREATE_DATE));
        po.setCreatedBy(rs.getString(OriginsVistoryRecordPO.FIELD_CREATED_BY));
        po.setUpdateDate(rs.getTimestamp(OriginsVistoryRecordPO.FIELD_UPDATE_DATE));
        po.setUpdatedBy(rs.getString(OriginsVistoryRecordPO.FIELD_UPDATED_BY));

        // 2. Data
        if (withData) {
            if (jaxbData) {
                po.setData(DumpUtils.restoreOriginRecordFromJaxb(rs.getString(OriginsVistoryRecordPO.FIELD_DATA_A)));
            } else if (protostuffData) {
                po.setData(DumpUtils.restoreFromProtostuff(rs.getBytes(OriginsVistoryRecordPO.FIELD_DATA_B)));
            }
        }

        // 3. State
        po.setId(rs.getString(OriginsVistoryRecordPO.FIELD_ID));
        po.setRevision(rs.getInt(OriginsVistoryRecordPO.FIELD_REVISION));
        po.setStatus(RecordStatus.valueOf(rs.getString(OriginsVistoryRecordPO.FIELD_STATUS)));
        po.setApproval(ApprovalState.valueOf(rs.getString(OriginsVistoryRecordPO.FIELD_APPROVAL)));
        po.setShift(DataShift.valueOf(rs.getString(OriginsVistoryRecordPO.FIELD_SHIFT)));
        po.setMajor(rs.getInt(OriginsVistoryRecordPO.FIELD_MAJOR));
        po.setMinor(rs.getInt(OriginsVistoryRecordPO.FIELD_MINOR));
        po.setValidFrom(rs.getTimestamp(OriginsVistoryRecordPO.FIELD_VALID_FROM));
        po.setValidTo(rs.getTimestamp(OriginsVistoryRecordPO.FIELD_VALID_TO));

        String operationType = rs.getString(OriginsVistoryRecordPO.FIELD_OPERATION_TYPE);
        if(StringUtils.isNotBlank(operationType)){
            po.setOperationType(VistoryOperationType.valueOf(operationType));
        }

        // 4. Origin section
        po.setOriginId(rs.getString(OriginsVistoryRecordPO.FIELD_ORIGIN_ID));
        po.setOperationId(rs.getString(OriginsVistoryRecordPO.FIELD_OPERATION_ID));
        po.setExternalId(rs.getString(OriginsVistoryRecordPO.FIELD_EXTERNAL_ID));
        po.setName(rs.getString(OriginsVistoryRecordPO.FIELD_NAME));
        po.setSourceSystem(rs.getString(OriginsVistoryRecordPO.FIELD_SOURCE_SYSTEM));
        po.setEnrichment(rs.getBoolean(OriginsVistoryRecordPO.FIELD_IS_ENRICHMENT));
        po.setGsn(rs.getLong(OriginsVistoryRecordPO.FIELD_GSN));

        return po;
    }

}

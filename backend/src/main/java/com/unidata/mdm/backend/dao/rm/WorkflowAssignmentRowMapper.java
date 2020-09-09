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

package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.integration.wf.EditWorkflowProcessTriggerType;
import com.unidata.mdm.backend.service.wf.po.WorkflowAssignmentPO;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Work flow assignment row mapper.
 */
public class WorkflowAssignmentRowMapper
    extends AbstractRowMapper<WorkflowAssignmentPO>
    implements RowMapper<WorkflowAssignmentPO> {

    /**
     * Default extractor.
     */
    public static final ResultSetExtractor<WorkflowAssignmentPO> DEFAULT_RESULT_SET_EXTRACTOR
        = rs -> rs != null && rs.next() ? WorkflowAssignmentRowMapper.DEFAULT_ROW_MAPPER.mapRow(rs, rs.getRow()) : null;

    /**
     * Default row mapper.
     */
    public static final WorkflowAssignmentRowMapper DEFAULT_ROW_MAPPER
        = new WorkflowAssignmentRowMapper();

    /**
     * Constructor.
     */
    private WorkflowAssignmentRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowAssignmentPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        WorkflowAssignmentPO po = new WorkflowAssignmentPO();

        super.mapRow(po, rs, rowNum);

        po.setId(rs.getLong(WorkflowAssignmentPO.FIELD_ID));
        po.setName(rs.getString(WorkflowAssignmentPO.FIELD_NAME));
        po.setProcessName(rs.getString(WorkflowAssignmentPO.FIELD_PROCESS_NAME));
        po.setType(WorkflowProcessType.valueOf(rs.getString(WorkflowAssignmentPO.FIELD_TYPE)));

        if (po.getType() == WorkflowProcessType.RECORD_EDIT) {
            po.setTriggerType(EditWorkflowProcessTriggerType.fromString(rs.getString(WorkflowAssignmentPO.FIELD_TRIGGER_TYPE)));
        }

        return po;
    }

}

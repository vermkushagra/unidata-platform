package com.unidata.mdm.backend.dao.impl;

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.dao.WorkflowAssignmentDao;
import com.unidata.mdm.backend.dao.rm.WorkflowAssignmentRowMapper;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.service.wf.po.WorkflowAssignmentPO;
import com.unidata.mdm.conf.WorkflowProcessType;

/**
 * @author Mikhail Mikhailov
 * Workflow assignment DAO implementation.
 */
@Repository
public class WorkflowAssignmentDaoImpl extends AbstractDaoImpl implements WorkflowAssignmentDao {
    /**
     * Load all SQL.
     */
    private final String loadAllSQL;
    /**
     * Load by entity name SQL.
     */
    private final String loadByEntityNameSQL;
    /**
     * Load by entity name and type SQL.
     */
    private final String loadByEntityNameAndTypeSQL;
    /**
     * Update SQL.
     */
    private final String updateSQL;
    /**
     * Insert SQL.
     */
    private final String insertSQL;
    /**
     * Delete SQL.
     */
    private final String deleteSQL;
    /**
     * Constructor.
     */
    @Autowired
    public WorkflowAssignmentDaoImpl(DataSource dataSource, @Qualifier("meta-sql") Properties sql) {
        super(dataSource);
        loadAllSQL = sql.getProperty("loadAllSQL");
        loadByEntityNameSQL = sql.getProperty("loadByEntityNameSQL");
        loadByEntityNameAndTypeSQL = sql.getProperty("loadByEntityNameAndTypeSQL");
        updateSQL = sql.getProperty("updateSQL");
        insertSQL = sql.getProperty("insertSQL");
        deleteSQL = sql.getProperty("deleteSQL");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorkflowAssignmentPO> loadAll() {
        return jdbcTemplate.query(loadAllSQL, WorkflowAssignmentRowMapper.DEFAULT_ROW_MAPPER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorkflowAssignmentPO> loadByEntityName(String name) {
        return jdbcTemplate.query(loadByEntityNameSQL, WorkflowAssignmentRowMapper.DEFAULT_ROW_MAPPER, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowAssignmentPO loadByEntityNameAndProcessType(String name, WorkflowProcessType type) {
        return jdbcTemplate.query(loadByEntityNameAndTypeSQL, WorkflowAssignmentRowMapper.DEFAULT_RESULT_SET_EXTRACTOR, name, type.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upsert(List<WorkflowAssignmentPO> update) {

        for (int i = 0; update != null && i < update.size(); i++) {
            WorkflowAssignmentPO po = update.get(i);
            if (po.getId() == null) {
                jdbcTemplate.update(insertSQL,
                        po.getName(),
                        po.getType().name(),
                        po.getTriggerType() != null ? po.getTriggerType().asString() : null,
                        po.getProcessName(),
                        SecurityUtils.getCurrentUserName());
            } else if (po.getProcessName() == null) {
                jdbcTemplate.update(deleteSQL, po.getId());
            } else {
                jdbcTemplate.update(updateSQL,
                        po.getProcessName(),
                        po.getTriggerType() != null ? po.getTriggerType().asString() : null,
                        SecurityUtils.getCurrentUserName(),
                        po.getId());
            }
        }
    }
}

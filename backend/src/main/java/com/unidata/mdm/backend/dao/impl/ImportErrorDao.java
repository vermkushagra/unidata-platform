package com.unidata.mdm.backend.dao.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.dao.ErrorDao;
import com.unidata.mdm.backend.po.ImportErrorPO;

/**
 * Dao to errors happens during import
 */
@Repository
public class ImportErrorDao extends AbstractDaoImpl implements ErrorDao<ImportErrorPO> {

    private static final String INSERT = "insert into import_errors (error,description,operation_id,sql,index) values (:error,:description,:operationId,:sql,:index)";

    /**
     * Constructor.
     *
     * @param dataSource
     */
    @Autowired
    public ImportErrorDao(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * persist error to DB
     *
     * @param error - error
     */
    @Override
    public void logError(ImportErrorPO error) {
        namedJdbcTemplate.update(INSERT, new BeanPropertySqlParameterSource(error));
    }
}

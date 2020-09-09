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

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

package com.unidata.mdm.backend.service.job.matching;

import java.util.Objects;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

public class MatchingItemReader extends JdbcCursorItemReader<String> {
    /**
     * Entity name.
     */
    @Value("#{stepExecutionContext['entityName']}")
    private String entityName;
    /**
     * Start of chunk to process.
     */
    @Value("#{stepExecutionContext['startGSN']}")
    private Long startGsn;
    /**
     * Start of chunk to process.
     */
    @Value("#{stepExecutionContext['endGSN']}")
    private Long endGsn;
    /**
     * Data source.
     */
    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;
    /**
     * Row mapper.
     */
    private static final RowMapper<String> NEXT_KEY_ROW_MAPPER = (rs, rowNum) -> rs.getString(1);


    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        final String sql = new StringBuilder()
                .append("select id from etalons where ")
                .append(Objects.nonNull(entityName) ? "name = '" + entityName + "' and " : "")
                .append("gsn >= ")
                .append(startGsn)
                .append(" and gsn <= ")
                .append(endGsn)
                .append("  and status in ('ACTIVE')")
                .toString();

        super.setDataSource(unidataDataSource);
        super.setSaveState(true);
        super.setDriverSupportsAbsolute(true);
        super.setRowMapper(NEXT_KEY_ROW_MAPPER);
        super.setSql(sql);
        super.afterPropertiesSet();
    }

    /**
     * @param entityName the entityName to set
     */
    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @param startGsn the startGsn to set
     */
    @Required
    public void setStartGsn(Long startGsn) {
        this.startGsn = startGsn;
    }

    /**
     * @param endGsn the endGsn to set
     */
    @Required
    public void setEndGsn(Long endGsn) {
        this.endGsn = endGsn;
    }

}

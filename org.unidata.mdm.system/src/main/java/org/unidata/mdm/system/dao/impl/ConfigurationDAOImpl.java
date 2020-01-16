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

package org.unidata.mdm.system.dao.impl;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.unidata.mdm.system.dao.ConfigurationDAO;

@Repository
public class ConfigurationDAOImpl extends NamedParameterJdbcDaoSupport implements ConfigurationDAO {

    private final String selectAllProperties;
    private final String upsertProperties;

    @Autowired
    public ConfigurationDAOImpl(
            @Qualifier("systemDataSource") final DataSource dataSource,
            @Qualifier("configuration-sql") final Properties sql
    ) {
        setDataSource(dataSource);
        selectAllProperties = sql.getProperty("SELECT_ALL_PROPERTIES");
        upsertProperties = sql.getProperty("UPSERT_PROPERTIES");
    }

    @Override
    public Map<String, byte[]> fetchAllProperties() {
        return getJdbcTemplate().query(
                selectAllProperties,
                (rs, rowNum) -> Pair.of(rs.getString("name"), rs.getBytes("value"))
        ).stream()
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public void save(final Map<String, byte[]> properties) {
        getNamedParameterJdbcTemplate().batchUpdate(
                upsertProperties,
                properties.entrySet().stream()
                        .map(o ->
                                new MapSqlParameterSource("name", o.getKey())
                                        .addValue("value", o.getValue())
                        )
                        .toArray(MapSqlParameterSource[]::new)
        );
    }
}

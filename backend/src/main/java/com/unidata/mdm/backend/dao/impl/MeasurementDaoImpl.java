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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.dao.MeasurementDao;
import com.unidata.mdm.backend.po.measurement.MeasurementUnitPO;
import com.unidata.mdm.backend.po.measurement.MeasurementValuePO;

@Repository
public class MeasurementDaoImpl extends AbstractDaoImpl implements MeasurementDao {

    private final String INSERT_VALUE;
    private final String UPDATE_VALUE;
    private final String DELETE_VALUE;
    private final String DELETE_UNITS;
    private final String INSERT_UNITS;
    private final String SELECT_VALUE_BY_ID;
    private final String SELECT_ALL_VALUES;

    private static final ResultSetExtractor<Map<String, MeasurementValuePO>> EXTRACTOR = rs -> {
        Map<String, MeasurementValuePO> result = new HashMap<>();
        while (rs.next()) {
            String valueId = rs.getString("id");
            MeasurementValuePO valuePO = result.get(valueId);
            if (valuePO == null) {
                valuePO = new MeasurementValuePO();
                String name = rs.getString("name");
                String shortName = rs.getString("short_name");
                valuePO.setId(valueId);
                valuePO.setName(name);
                valuePO.setShortName(shortName);
                result.put(valueId, valuePO);
            }
            MeasurementUnitPO measurementUnitPO = new MeasurementUnitPO();
            String unitId = rs.getString("unit_id");
            String name = rs.getString("unit_name");
            String shortName = rs.getString("unit_short_name");
            String function = rs.getString("function");
            Boolean base = rs.getBoolean("base");
            int order = rs.getInt("order");
            measurementUnitPO.setShortName(shortName);
            measurementUnitPO.setName(name);
            measurementUnitPO.setId(unitId);
            measurementUnitPO.setValueId(valueId);
            measurementUnitPO.setConvectionFunction(function);
            measurementUnitPO.setBase(base);
            measurementUnitPO.setOrder(order);
            valuePO.getMeasurementUnits().add(measurementUnitPO);
        }
        return result;
    };

    /**
     * Constructor.
     *
     * @param dataSource- data source
     */
    @Autowired
    public MeasurementDaoImpl(DataSource dataSource, @Qualifier("measurement-sql") Properties sql) {
        super(dataSource);
        INSERT_VALUE = sql.getProperty("INSERT_VALUE");
        UPDATE_VALUE = sql.getProperty("UPDATE_VALUE");
        DELETE_VALUE = sql.getProperty("DELETE_VALUE");
        DELETE_UNITS = sql.getProperty("DELETE_UNITS");
        INSERT_UNITS = sql.getProperty("INSERT_UNITS");
        SELECT_VALUE_BY_ID = sql.getProperty("SELECT_VALUE_BY_ID");
        SELECT_ALL_VALUES = sql.getProperty("SELECT_ALL_VALUES");
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void save(@Nonnull MeasurementValuePO value) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue("id", value.getId());
        valueMap.addValue("name", value.getName());
        valueMap.addValue("shortName", value.getShortName());
        namedJdbcTemplate.update(INSERT_VALUE, valueMap);
        insertUnits(value.getMeasurementUnits());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void update(@Nonnull MeasurementValuePO value) {
        MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue("id", value.getId());
        valueMap.addValue("valueId", value.getId());
        valueMap.addValue("name", value.getName());
        valueMap.addValue("shortName", value.getShortName());
        namedJdbcTemplate.update(UPDATE_VALUE, valueMap);
        namedJdbcTemplate.update(DELETE_UNITS, valueMap);
        insertUnits(value.getMeasurementUnits());
    }

    private void insertUnits(@Nonnull Collection<MeasurementUnitPO> units) {
        SqlParameterSource[] unitMap = units.stream().map(this::getUnitMap).toArray(SqlParameterSource[]::new);
        namedJdbcTemplate.batchUpdate(INSERT_UNITS, unitMap);
    }

    private SqlParameterSource getUnitMap(MeasurementUnitPO unit) {
        MapSqlParameterSource unitMap = new MapSqlParameterSource();
        unitMap.addValue("id", unit.getId());
        unitMap.addValue("shortName", unit.getShortName());
        unitMap.addValue("name", unit.getName());
        unitMap.addValue("valueId", unit.getValueId());
        unitMap.addValue("function", unit.getConvectionFunction());
        unitMap.addValue("base", unit.isBase());
        unitMap.addValue("order", unit.getOrder());
        return unitMap;
    }

    @Nullable
    @Override
    public MeasurementValuePO getById(@Nonnull String valueId) {
        Map<String, MeasurementValuePO> result = namedJdbcTemplate.query(
                SELECT_VALUE_BY_ID, Collections.singletonMap("id", valueId), EXTRACTOR);
        return result.get(valueId);
    }

    @Nonnull
    @Override
    public Map<String, MeasurementValuePO> getAllValues() {
        return namedJdbcTemplate.query(SELECT_ALL_VALUES, EXTRACTOR);
    }

    @Override
    public boolean removeValues(@Nonnull Collection<String> measureValueIds) {
        if (measureValueIds.isEmpty()) {
            return true;
        }
        return namedJdbcTemplate.update(DELETE_VALUE, Collections.singletonMap("valueIds", measureValueIds))
                == measureValueIds.size();
    }
}

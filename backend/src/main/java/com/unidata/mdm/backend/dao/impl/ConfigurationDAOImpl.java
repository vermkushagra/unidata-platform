package com.unidata.mdm.backend.dao.impl;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import com.unidata.mdm.backend.dao.ConfigurationDAO;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationDAOImpl extends AbstractDaoImpl implements ConfigurationDAO {

    private final String selectAllProperties;
    private final String upsertProperties;

    @Autowired
    public ConfigurationDAOImpl(
            final DataSource unidataDataSource,
            final @Qualifier("configuration-sql") Properties sql
    ) {
        super(unidataDataSource);
        selectAllProperties = sql.getProperty("SELECT_ALL_PROPERTIES");
        upsertProperties = sql.getProperty("UPSERT_PROPERTIES");
    }

    @Override
    public Map<String, byte[]> fetchAllProperties() {
        return jdbcTemplate.query(
                selectAllProperties,
                (rs, rowNum) -> Pair.of(rs.getString("name"), rs.getBytes("value"))
        ).stream()
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public void save(final Map<String, byte[]> properties) {
        namedJdbcTemplate.batchUpdate(
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

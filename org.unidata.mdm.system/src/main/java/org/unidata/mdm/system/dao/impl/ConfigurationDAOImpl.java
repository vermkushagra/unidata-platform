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

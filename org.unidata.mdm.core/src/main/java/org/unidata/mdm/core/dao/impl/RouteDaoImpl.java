package org.unidata.mdm.core.dao.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.unidata.mdm.core.dao.RouteDao;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.system.dao.impl.BaseDAOImpl;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RouteDaoImpl extends BaseDAOImpl implements RouteDao {

    private static final String ROUTE_ID_COLUMN = "route_id";
    private static final String ROUTE_DEFINITION_COLUMN = "route_definition";

    /**
     * Constructor.
     *
     * @param dataSource Core data source
     */
    public RouteDaoImpl(@Qualifier("coreDataSource") final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean upsertRoutes(@Nonnull Map<String, String> routes) {
        return namedJdbcTemplate.batchUpdate(
                "insert into bus_routes(route_id, route_definition) values(:route_id, :route_definition) "
                        + "on conflict (route_id) do update set route_definition = EXCLUDED.route_definition",
                routes.entrySet().stream()
                        .map(e -> Maps.of(ROUTE_ID_COLUMN, e.getKey(), ROUTE_DEFINITION_COLUMN, e.getValue()))
                        .map(MapSqlParameterSource::new)
                        .toArray(MapSqlParameterSource[]::new)
        ).length == routes.size();
    }

    @Override
    public Map<String, String> routesDefinitions() {
        return namedJdbcTemplate.query(
                "select * from bus_routes",
                (rs, num) -> Pair.of(rs.getString(ROUTE_ID_COLUMN), rs.getString("route_definition"))
        ).stream()
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }
}

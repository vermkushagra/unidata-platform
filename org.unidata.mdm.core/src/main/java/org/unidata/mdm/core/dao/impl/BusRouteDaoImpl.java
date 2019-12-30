package org.unidata.mdm.core.dao.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.unidata.mdm.core.dao.BusRouteDao;
import org.unidata.mdm.core.dto.BusRoutesDefinition;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.system.dao.impl.BaseDAOImpl;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;

@Repository
public class BusRouteDaoImpl extends BaseDAOImpl implements BusRouteDao {

    private static final String ROUTES_DEFINITION_ID_COLUMN = "routes_definition_id";
    private static final String ROUTES_DEFINITION_COLUMN = "routes_definition";

    /**
     * Constructor.
     *
     * @param dataSource Core data source
     */
    public BusRouteDaoImpl(@Qualifier("coreDataSource") final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean upsertBusRoutesDefinitions(@Nonnull Collection<BusRoutesDefinition> busRoutesDefinitions) {
        return namedJdbcTemplate.batchUpdate(
                "insert into bus_routes_definitions(routes_definition_id, routes_definition) values(:routes_definition_id, :routes_definition) "
                        + "on conflict (routes_definition_id) do update set routes_definition = EXCLUDED.routes_definition",
                busRoutesDefinitions.stream()
                        .map(brd ->
                                Maps.of(
                                        ROUTES_DEFINITION_ID_COLUMN, brd.getRoutesDefinitionId(),
                                        ROUTES_DEFINITION_COLUMN, brd.getRoutesDefinition()
                                )
                        )
                        .map(MapSqlParameterSource::new)
                        .toArray(MapSqlParameterSource[]::new)
        ).length == busRoutesDefinitions.size();
    }

    @Override
    public List<BusRoutesDefinition> fetchBusRoutesDefinitions() {
        return namedJdbcTemplate.query(
                "select * from bus_routes_definitions",
                (rs, num) ->
                        new BusRoutesDefinition(rs.getString(ROUTES_DEFINITION_ID_COLUMN), rs.getString(ROUTES_DEFINITION_COLUMN))
        );
    }

    @Override
    public void deleteBusRoutesDefinition(String busRoutesDefinitionId) {
        namedJdbcTemplate.update(
                "delete from bus_routes_definitions where routes_definition_id = :routes_definition_id",
                Maps.of("routes_definition_id", busRoutesDefinitionId)
        );
    }
}

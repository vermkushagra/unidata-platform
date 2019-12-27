package org.unidata.mdm.core.dao;

import org.unidata.mdm.core.dto.BusRoutesDefinition;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface BusRouteDao {
    boolean upsertBusRoutesDefinitions(@Nonnull Collection<BusRoutesDefinition> busRoutesDefinitions);
    List<BusRoutesDefinition> fetchBusRoutesDefinitions();
}

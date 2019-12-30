package org.unidata.mdm.core.service;

import org.unidata.mdm.core.dto.BusRoutesDefinition;

import javax.annotation.Nonnull;
import java.util.List;

public interface BusConfigurationService {
    void upsertBusRoutesDefinition(@Nonnull BusRoutesDefinition busRoutesDefinition);

    void installBusRoutesDefinition(@Nonnull BusRoutesDefinition busRoutesDefinition);

    List<BusRoutesDefinition> busRoutesDefinitions();


    void loadBusRoutesDefinitions();

    void deleteBusRoutesDefinition(String busRoutesDefinitionId);
}

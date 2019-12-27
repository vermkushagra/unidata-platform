package org.unidata.mdm.core.dto;

public class BusRoutesDefinition {
    private final String routesDefinitionId;
    private final String routesDefinition;

    public BusRoutesDefinition(String routesDefinitionId, String routesDefinition) {
        this.routesDefinitionId = routesDefinitionId;
        this.routesDefinition = routesDefinition;
    }

    public String getRoutesDefinitionId() {
        return routesDefinitionId;
    }

    public String getRoutesDefinition() {
        return routesDefinition;
    }
}

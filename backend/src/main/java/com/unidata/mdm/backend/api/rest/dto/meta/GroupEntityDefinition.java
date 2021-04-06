package com.unidata.mdm.backend.api.rest.dto.meta;

public class GroupEntityDefinition {
    private String name;
    private String displayName;
    private boolean dashboardVisible;

    public GroupEntityDefinition(String name, String displayName, boolean dashboardVisible) {
        this.name = name;
        this.displayName = displayName;
        this.dashboardVisible = dashboardVisible;
    }

    public GroupEntityDefinition() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isDashboardVisible() {
        return dashboardVisible;
    }

    public void setDashboardVisible(boolean dashboardVisible) {
        this.dashboardVisible = dashboardVisible;
    }
}

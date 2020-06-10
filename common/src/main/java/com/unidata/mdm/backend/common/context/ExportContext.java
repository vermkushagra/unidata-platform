package com.unidata.mdm.backend.common.context;

public class ExportContext {

    private final boolean exportRoles;

    private final boolean exportUsers;

    public ExportContext(final boolean exportRoles, final boolean exportUsers) {
        this.exportRoles = exportRoles;
        this.exportUsers = exportUsers;
    }

    public boolean isExportRoles() {
        return exportRoles;
    }

    public boolean isExportUsers() {
        return exportUsers;
    }
}

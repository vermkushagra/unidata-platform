package org.unidata.mdm.system.dto;

import org.unidata.mdm.system.type.module.Module;

public class ModuleInfo {
    private final Module module;

    private ModuleStatus moduleStatus = ModuleStatus.NOT_LOADED;

    private String error;

    public ModuleInfo(
            final Module module
    ) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public ModuleStatus getModuleStatus() {
        return moduleStatus;
    }

    public void setModuleStatus(ModuleStatus moduleStatus) {
        this.moduleStatus = moduleStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public enum ModuleStatus {
        NOT_LOADED,
        LOADING,
        LOADED,
        FAILED,
        INSTALLATION_FAILED,
        START_FAILED
    }
}

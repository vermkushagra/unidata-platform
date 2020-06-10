package com.unidata.mdm.backend.common.module;

/**
 * @author Mikhail Mikhailov
 * Module constants.
 */
public enum Module {
    /**
     * Import data job.
     */
    MODULE_IMPORT_DATA("importDataModule"),
    /**
     * Import data job.
     */
    MODULE_COMMON("commonModule");
    /**
     * Constructor.
     */
    private Module(String moduleName) {
        this.moduleName = moduleName;
    }
    /**
     * The name of the module.
     */
    private final String moduleName;
    /**
     * @return the moduleName
     */
    public String getModuleName() {
        return moduleName;
    }
}

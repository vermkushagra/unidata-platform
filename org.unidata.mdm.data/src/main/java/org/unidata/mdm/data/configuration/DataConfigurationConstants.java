package org.unidata.mdm.data.configuration;

import org.unidata.mdm.data.module.DataModule;

/**
 * Various constants.
 * @author Mikhail Mikhailov on Nov 12, 2019
 */
public final class DataConfigurationConstants {
	/**
     * Core data source properties prefix.
     */
    public static final String DATA_DATASOURCE_PROPERTIES_PREFIX = DataModule.MODULE_ID + ".datasource.";
    /**
     * Data storage schema name.
     */
    public static final String DATA_STORAGE_SCHEMA_NAME = "org_unidata_mdm_data";
    /**
     * Meta tables change log name.
     */
    public static final String META_LOG_NAME = "meta_change_log";
    /**
     * Data tables change log name.
     */
    public static final String DATA_LOG_NAME = "data_change_log";
    /**
     * Storage init lock name.
     */
    public static final String STORAGE_LOCK_NAME = "DATA_STORAGE_LOCK";

    public static final String DATA_TARGET = "direct:data";

    /**
     * Constructor.
     */
    private DataConfigurationConstants() {
        super();
    }
}

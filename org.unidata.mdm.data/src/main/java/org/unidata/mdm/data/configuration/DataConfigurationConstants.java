package org.unidata.mdm.data.configuration;

/**
 * Various constants.
 * @author Mikhail Mikhailov on Nov 12, 2019
 */
public final class DataConfigurationConstants {

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
    public static final String STORAGE_LOCK_NAME = "DATA_STORAGE_LOCK";

    /**
     * Constructor.
     */
    private DataConfigurationConstants() {
        super();
    }
}

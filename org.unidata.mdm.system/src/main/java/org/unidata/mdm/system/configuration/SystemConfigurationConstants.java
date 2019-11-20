package org.unidata.mdm.system.configuration;

import org.unidata.mdm.system.module.SystemModule;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * This module contsants.
 */
public final class SystemConfigurationConstants {
    /**
     * Current main platform version.
     */
    public static final String PLATFORM_VERSION_PROPERTY = "unidata.platform.version";
    /**
     * Unidata node id property.
     */
    public static final String UNIDATA_NODE_ID_PROPERTY = "unidata.node.id";
    /**
     * Unidata dump target format property.
     */
    public static final String UNIDATA_DUMP_TARGET_FORMAT_PROPERTY = "unidata.dump.target.format";
    /**
     * Unidata system schema user property name.
     */
    public static final String UNIDATA_SYSTEM_SCHEMA_USERNAME = SystemModule.MODULE_ID + ".datasource.username";
    /**
     * Unidata system schema password property name.
     */
    public static final String UNIDATA_SYSTEM_SCHEMA_PASSWORD = SystemModule.MODULE_ID + ".datasource.password";
    /**
     * Unidata system schema URL property name.
     */
    public static final String UNIDATA_SYSTEM_URL = SystemModule.MODULE_ID + ".datasource.url";
    /**
     * Unidata system schema name.
     */
    public static final String UNIDATA_SYSTEM_SCHEMA_NAME = "org_unidata_mdm_system";

    public static final String SYSTEM_MIGRATION_LOG_NAME = "system_change_log";

    /**
     * Constructor.
     */
    private SystemConfigurationConstants() {
        super();
    }

}

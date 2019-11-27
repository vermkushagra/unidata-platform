package org.unidata.mdm.core.configuration;

import org.unidata.mdm.core.module.CoreModule;

/**
 * @author Mikhail Mikhailov on Oct 30, 2019
 */
public final class CoreConfigurationConstants {
    /**
     * Core data source properties prefix.
     */
    public static final String CORE_DATASOURCE_PROPERTIES_PREFIX = CoreModule.MODULE_ID + ".datasource.";
    /**
     * Constructor.
     */
    private CoreConfigurationConstants() {
        super();
    }
}

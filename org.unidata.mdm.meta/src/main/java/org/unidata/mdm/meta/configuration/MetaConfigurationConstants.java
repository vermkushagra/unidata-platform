/**
 * 
 */
package org.unidata.mdm.meta.configuration;

import org.unidata.mdm.meta.module.MetaModule;

/**
 * @author mikhail
 * Configuration constants.
 */
public final class MetaConfigurationConstants {
	/**
	 * Disabling constructor.
	 */
	private MetaConfigurationConstants() {
		super();
	}
	/**
     * Core data source properties prefix.
     */
    public static final String META_DATASOURCE_PROPERTIES_PREFIX = MetaModule.MODULE_ID + ".datasource.";
    /**
     * Meta storage schema name.
     */
    public static final String META_SCHEMA_NAME = "org_unidata_mdm_meta";
}

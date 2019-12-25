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
     * Job initial pool size.
     */
    public static final String PROP_NAME_MIN_THREAD_POOL_SIZE = CoreModule.MODULE_ID + ".job.pool.min.size";
    /**
     * Job max pool size.
     */
    public static final String PROP_NAME_MAX_THREAD_POOL_SIZE = CoreModule.MODULE_ID + ".job.pool.max.size";
    /**
     * Job queue capacity.
     */
    public static final String PROP_NAME_QUEUE_SIZE = CoreModule.MODULE_ID + ".job.queue.size";
    /**
     * This module schema name.
     */
    public static final String CORE_SCHEMA_NAME = "org_unidata_mdm_core";
    /**
     * Parameter processor map.
     */
    public static final String BEAN_NAME_JOB_PARAMETER_PROCESSOR_MAP = "jobParameterProcessorsMap";
    /**
     * Constructor.
     */
    private CoreConfigurationConstants() {
        super();
    }
}

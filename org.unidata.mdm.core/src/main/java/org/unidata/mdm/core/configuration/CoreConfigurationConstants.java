/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

    public static final String CORE_TARGET = "direct:core";

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

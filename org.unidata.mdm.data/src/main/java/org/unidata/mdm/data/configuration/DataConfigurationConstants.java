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

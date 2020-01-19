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

package org.unidata.mdm.system.configuration;

import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.system.module.SystemModule;

/**
 * @author Mikhail Mikhailov on Oct 7, 2019
 * This module contsants.
 */
public final class SystemConfigurationConstants {

    public static final String CONFIGURATION_PATH_PROPERTY = "unidata.conf";

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
     * Default replay timeout (in millis).
     */
    public static final String SYSTEM_DEFAULT_EVENT_REPLAY_TIMEOUT = SystemModule.MODULE_ID + ".event.replay.timeout";
    /**
     * Unidata system schema URL property name.
     */
    public static final String UNIDATA_SYSTEM_URL = SystemModule.MODULE_ID + ".datasource.url";
    /**
     * Unidata system schema URL property name.
     */
    public static final String UNIDATA_DEFAULT_LOCALE = SystemModule.MODULE_ID + ".default.locale";
    /**
     * Unidata system schema name.
     */
    public static final String UNIDATA_SYSTEM_SCHEMA_NAME = "org_unidata_mdm_system";

    public static final String SYSTEM_MIGRATION_LOG_NAME = "system_change_log";
    /**
     * The non-subject mark.
     */
    public static final String NON_SUBJECT = StringUtils.EMPTY;

    /**
     * Constructor.
     */
    private SystemConfigurationConstants() {
        super();
    }

}

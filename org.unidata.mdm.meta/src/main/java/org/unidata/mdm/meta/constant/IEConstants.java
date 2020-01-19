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

package org.unidata.mdm.meta.constant;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class IEConstants {
    /** The catalina base. */
    private static final String CATALINA_BASE = "catalina.base";

    /** The temp. */
    private static final String TEMP = "temp";

    /** The to export folder. */
    private static final String TO_EXPORT_FOLDER = "to_export";

    /** The to import folder. */
    private static final String TO_IMPORT_FOLDER = "to_import";

    public static String DATA_IMPORT_METADATA_SUCCESS = "app.user.events.import.metadata.success";

    public static String DATA_IMPORT_METADATA_FAILED = "app.user.events.import.metadata.failed";

    public static String DATA_EXPORT_METADATA_SUCCESS = "app.user.events.export.metadata.success";


    /** The to import. */
    public static final Path IMPORT_PATH = Paths.get(
            System.getProperty(CATALINA_BASE) + File.separator + TEMP + File.separator + TO_IMPORT_FOLDER + File.separator
    );

    /** The to export. */
    public static final Path EXPORT_PATH = Paths.get(
            System.getProperty(CATALINA_BASE) + File.separator + TEMP + File.separator + TO_EXPORT_FOLDER + File.separator
    );

    private IEConstants() {}
}

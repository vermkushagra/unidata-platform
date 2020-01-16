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

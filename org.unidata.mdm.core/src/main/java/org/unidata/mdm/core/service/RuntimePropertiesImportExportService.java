package org.unidata.mdm.core.service;

import java.nio.file.Path;

/**
 * @author Alexander Malyshev
 */
public interface RuntimePropertiesImportExportService {
    void exportProperties();

    void importProperties(Path path);
}

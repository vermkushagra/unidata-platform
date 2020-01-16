package org.unidata.mdm.meta.service;

import java.io.InputStream;

public interface MetaModelImportService {
    void importModel(InputStream inputStream, boolean recreate);

    void importMeasureUnits(InputStream metaModelInputStream);
}

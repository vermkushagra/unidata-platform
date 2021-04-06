package com.unidata.mdm.backend.service.data.export;

import java.io.ByteArrayOutputStream;

import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
/**
 * Interface for data export.
 * @author ilya.bykov
 *
 */
public interface DataExportService {


    /**
     * Export data as xslx.
     *
     * @param ctx the ctx
     * @return byte array with all necessary information
     */
    ByteArrayOutputStream exportData(GetMultipleRequestContext ctx);
    


}

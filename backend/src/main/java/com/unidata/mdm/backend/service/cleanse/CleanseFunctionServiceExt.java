package com.unidata.mdm.backend.service.cleanse;

import java.io.IOException;
import java.nio.file.Path;

import com.unidata.mdm.backend.api.rest.dto.cleanse.CFCustomUploaderResponse;
import com.unidata.mdm.backend.common.service.CleanseFunctionService;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
/**
 * 
 * @author ilya.bykov
 *
 */
public interface CleanseFunctionServiceExt extends CleanseFunctionService, AfterContextRefresh {

    /**
     * Pre-load and validate custom function.
     * TODO Refactor this method. It uses mixture of REST and core types, creates dependencies to REST!
     *
     * @param jarFile
     *            the jar file
     * @param saveToDb
     *            the save to db
     * @return the CF custom uploader response
     */
    CFCustomUploaderResponse preloadAndValidateCustomFunction(Path jarFile, boolean saveToDb);

    /**
     * Load and init custom cf.
     *
     * @param tempId
     *            the temp id
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    void loadAndInit(String tempId) throws IOException;

}
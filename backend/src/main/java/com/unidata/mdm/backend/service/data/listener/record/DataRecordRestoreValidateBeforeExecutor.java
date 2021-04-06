/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;


/**
 * @author Mikhail Mikhailov
 * Simple context validity checker and key finder.
 */
public class DataRecordRestoreValidateBeforeExecutor
    implements DataRecordBeforeExecutor<UpsertRequestContext> {

    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonComponent;

    /**
     * Logger for this bean.
     */
    private static final Logger LOGGER
        = LoggerFactory.getLogger(DataRecordRestoreValidateBeforeExecutor.class);

    /**
     * Constructor.
     */
    public DataRecordRestoreValidateBeforeExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        // 1. Check input
        if (!ctx.isValidRecordKey()) {
            final String message = "Ivalid input [{}]";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_RESTORE_INVALID_INPUT, ctx);
        }

        // 2. Identify
        RecordKeys keys = commonComponent.identify(ctx);
        if (keys == null) {
            final String message = "Record not found by supplied keys [{}]";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_RESTORE_NOT_FOUND_BY_SUPPLIED_KEYS, ctx);
        }

        return true;
    }
}

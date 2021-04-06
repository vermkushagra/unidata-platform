package com.unidata.mdm.backend.service.data.classifiers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsValidationComponent;

/**
 * @author Mikhail Mikhailov
 * Pre-validation / key resolution code, analogously to {@link RelationsValidationComponent}.
 */
@Component
public class ClassifiersValidationComponent {
    /**
     * CRC.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    public void before(GetClassifiersDataRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // Nothing else so far.
            ensureAndGetRecordKeys(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }

    public void after(GetClassifiersDataRequestContext ctx) {
        // NOPE
    }

    public void before(DeleteClassifiersDataRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // Nothing else so far.
            ensureAndGetRecordKeys(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    public void after(DeleteClassifiersDataRequestContext ctx) {
        // NOPE
    }
    public void before(UpsertClassifiersDataRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // Nothing else so far.
            ensureAndGetRecordKeys(ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }
    public void after(UpsertClassifiersDataRequestContext ctx) {
        // NOPE
    }
    /**
     * Ensures record context.
     * @param ctx the context
     * @return keys
     */
    private void ensureAndGetRecordKeys(RecordIdentityContext ctx) {

        RecordKeys keys = ctx.keys();
        if (keys == null) {
            commonRecordsComponent.identify(ctx);
        }
    }
}

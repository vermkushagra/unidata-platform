package com.unidata.mdm.backend.service.data.listener.relation;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for deleting containments of a relation.
 */
public class RelationDeleteDropContainmentAfterExecutor implements DataRecordAfterExecutor<DeleteRelationRequestContext> {
    /**
     * Record component. Index updates are processed later.
     */
    @Autowired
    private EtalonRecordsComponent etalonRecordsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRelationRequestContext ctx) {

        MeasurementPoint.start();
        try {

            RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
            if (relation.getRelType() != RelType.CONTAINS) {
                return true;
            }

            DeleteRequestContext dCtx = ctx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT);
            etalonRecordsComponent.deleteEtalon(dCtx);
            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }
}

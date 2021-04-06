package com.unidata.mdm.backend.service.data.listener.record;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DQContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.service.cleanse.DataQualityServiceExt;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * The Class DQBeforeUpsertExecutor.
 */
public class DataRecordUpsertOriginDQBeforeExecutor implements DataRecordBeforeExecutor<UpsertRequestContext> {

    /** The dq service. */
    @Autowired
    private DataQualityServiceExt dqService;
    @Autowired
    private MetaModelServiceExt metamodelService;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor
     * #execute(com.unidata.mdm.backend.service.data.DataRequestContext)
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        if (ctx.isSkipCleanse()) {
            return true;
        }

        MeasurementPoint.start();
        try {

            RecordKeys keys = ctx.keys();

            String entityName = keys == null ? ctx.getEntityName() : keys.getEntityName();
            String etalonId = keys == null || keys.getEtalonKey() == null ? null : keys.getEtalonKey().getId();
            String originId = keys == null || keys.getOriginKey() == null ? null : keys.getOriginKey().getId();
            String sourceSystem = selectSourceSystem(keys, ctx);

            EntityDef entityDef = metamodelService.getEntityByIdNoDeps(entityName);
            LookupEntityDef lookupEntityDef = metamodelService.getLookupEntityById(entityName);
            List<DQRuleDef> rules = entityDef == null ? lookupEntityDef.getDataQualities() : entityDef.getDataQualities();
            List<DQRuleDef> forOrigin= dqService.filterForOrigin(sourceSystem, rules);

            DataRecord origin = ctx.getRecord();

            if(!CollectionUtils.isEmpty(forOrigin)) {

                for (DQRuleDef dqRuleDef : forOrigin) {
                    DQContext<DataRecord> dqContext
                        = new DQContext<DataRecord>()
                           .withEntityName(entityName)
                           .withRecordId(etalonId == null ? originId : etalonId)
                           .withRecord(origin)
                           .withRecordValidFrom(ctx.getValidFrom())
                           .withUserStorage(ctx.getUserStorage())
                           .withRecordValidTo(ctx.getValidTo())
                           .withRules(Collections.singletonList(dqRuleDef));

                    dqService.applyRules(dqContext);
                    ctx.getDqErrors().addAll(dqContext.getErrors());
                }
            }

            return ctx.getDqErrors().isEmpty();

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Selects the source system source.
     * @param keys resolved keys (potentially null)
     * @param ctx upsert context
     * @return source system
     */
    private String selectSourceSystem(RecordKeys keys, UpsertRequestContext ctx) {

        if (keys == null || keys.getOriginKey() == null) {
            return keys != null && keys.getEtalonKey() != null
                ? metamodelService.getAdminSourceSystem().getName()
                : ctx.getSourceSystem();
        }

        return keys.getOriginKey().getSourceSystem();
    }
}

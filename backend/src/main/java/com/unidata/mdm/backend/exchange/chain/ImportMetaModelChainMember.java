package com.unidata.mdm.backend.exchange.chain;

import java.util.List;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.Model;

/**
 * @author Mikhail Mikhailov
 */
public class ImportMetaModelChainMember implements ChainMember {

    /**
     * Constructor.
     */
    public ImportMetaModelChainMember() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(ExchangeContext ctx, Action currentAction) {
        try {
            MetaModelServiceExt svc = getMetaModelService(ctx);

            List<String> storageIds = svc.getStorageIdsList();
            if (!storageIds.contains(ctx.getModel().getStorageId())) {
                throw new RuntimeException("Cannot import model. "
                        + "Storage id" + ctx.getModel().getStorageId() + " was not in " + storageIds);
            }
            Model model = ctx.getModel();
            UpdateModelRequestContext updateCtx = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
                    .enumerationsUpdate(model.getEnumerations())
                    .sourceSystemsUpdate(model.getSourceSystems())
                    .nestedEntityUpdate(model.getNestedEntities())
                    .lookupEntityUpdate(model.getLookupEntities())
                    .entityUpdate(model.getEntities())
                    .relationsUpdate(model.getRelations())
                    .entitiesGroupsUpdate(model.getEntitiesGroup())
                    .cleanseFunctionsUpdate(model.getCleanseFunctions() == null ? null : model.getCleanseFunctions().getGroup())
                    .storageId(ctx.getModel().getStorageId())
                    .isForceRecreate(ctx.getImportMetaMode() == ExchangeContext.ImportMetaMode.RECREATE ? UpdateModelRequestContext.UpsertType.FULLY_NEW : UpdateModelRequestContext.UpsertType.ADDITION)
                    .build();
            ctx.putToStorage(StorageId.DEFAULT_CLASSIFIERS, model.getDefaultClassifiers());
            svc.upsertModel(updateCtx);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}

package com.unidata.mdm.backend.service.registration.handlers;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.registration.keys.ClassifierRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.LookupEntityRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * Process removing classifier from lookup entity
 */
@Component
public class ClassifierLookupEntityDeleteHandler implements DeleteHandler<ClassifierRegistryKey, LookupEntityRegistryKey> {

    @Autowired
    private MetaModelServiceExt modelService;

    @Override
    public void onDelete(ClassifierRegistryKey removedKey, LookupEntityRegistryKey linkedKey) {
        String entityName = linkedKey.getEntityName();
        LookupEntityDef lookupEntityById = modelService.getLookupEntityById(entityName);
        lookupEntityById.getClassifiers().remove(removedKey.getClassifierName());
        UpdateModelRequestContext updateModelRequestContext = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
                .lookupEntityUpdate(Collections.singletonList(lookupEntityById))
                .build();
        modelService.upsertModel(updateModelRequestContext);
    }

    @Override
    public UniqueRegistryKey.Type getRemovedEntityType() {
        return UniqueRegistryKey.Type.CLASSIFIER;
    }

    @Override
    public UniqueRegistryKey.Type getLinkedEntityType() {
        return UniqueRegistryKey.Type.LOOKUP_ENTITY;
    }
}

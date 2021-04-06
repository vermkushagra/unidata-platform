package com.unidata.mdm.backend.service.registration.handlers;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.registration.keys.ClassifierRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.EntityRegistryKey;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;
import com.unidata.mdm.meta.EntityDef;

/**
 * Process removing classifier from entity
 */
@Component
public class ClassifierEntityDeleteHandler implements DeleteHandler<ClassifierRegistryKey, EntityRegistryKey> {

    @Autowired
    private MetaModelServiceExt modelService;

    @Override
    public void onDelete(ClassifierRegistryKey removedKey, EntityRegistryKey linkedKey) {
        String entityName = linkedKey.getEntityName();
        GetEntityDTO entityById = modelService.getEntityById(entityName);
        EntityDef entity = entityById.getEntity();
        entity.getClassifiers().remove(removedKey.getClassifierName());
        UpdateModelRequestContext updateModelRequestContext = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
                .entityUpdate(Collections.singletonList(entity))
                .relationsUpdate(entityById.getRelations() == null ? Collections.emptyList() : entityById.getRelations())
                .nestedEntityUpdate(entityById.getRefs() == null ? Collections.emptyList() : entityById.getRefs())
                .build();
        modelService.upsertModel(updateModelRequestContext);
    }

    @Override
    public UniqueRegistryKey.Type getRemovedEntityType() {
        return UniqueRegistryKey.Type.CLASSIFIER;
    }

    @Override
    public UniqueRegistryKey.Type getLinkedEntityType() {
        return UniqueRegistryKey.Type.ENTITY;
    }
}

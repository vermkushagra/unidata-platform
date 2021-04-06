package com.unidata.mdm.backend.service.model.impl;

import com.unidata.mdm.backend.dao.MetaModelDao;
import com.unidata.mdm.backend.service.model.ModelType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.model.RecordsCleaner;

@Component
public class PersistedRecordsCleaner implements RecordsCleaner {

    /**
     * Ralation component
     */
    @Autowired
    private RelationsServiceComponent relationsServiceComponent;

    /**
     * Meta model DAO.
     */
    @Autowired
    private MetaModelDao metaModelDao;

    @Override

    public void cleanRelatedRecords(DeleteModelRequestContext context) {
        //UN-4757 После удалении связей из реестра невозможно удалить запись
        //perhaps we need also deactivate for entity and lookup entity
        context.getRelationIds().forEach(id -> relationsServiceComponent.deactiveteRelationsByName(id));

        if(CollectionUtils.isNotEmpty(context.getNestedEntitiesIds())){
            metaModelDao.deleteRecords(context.getStorageId(), ModelType.NESTED_ENTITY, context.getNestedEntitiesIds());
        }
    }
}

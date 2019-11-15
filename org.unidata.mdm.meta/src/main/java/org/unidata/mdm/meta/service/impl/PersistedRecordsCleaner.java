package org.unidata.mdm.meta.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.dao.MetaModelDao;
import org.unidata.mdm.meta.service.RecordsCleaner;
import org.unidata.mdm.meta.type.ModelType;

@Component
public class PersistedRecordsCleaner implements RecordsCleaner {

    // TODO: @Modules
//    /**
//     * Ralation component
//     */
//    @Autowired
//    private RelationsServiceComponent relationsServiceComponent;

    /**
     * Meta model DAO.
     */
    @Autowired
    private MetaModelDao metaModelDao;

    @Override

    public void cleanRelatedRecords(DeleteModelRequestContext context) {
        //UN-4757 После удалении связей из реестра невозможно удалить запись
        //perhaps we need also deactivate for entity and lookup entity
//        context.getRelationIds().forEach(id -> relationsServiceComponent.deactiveteRelationsByName(id));// TODO: @Modules

        if(CollectionUtils.isNotEmpty(context.getNestedEntitiesIds())){
            metaModelDao.deleteRecords(context.getStorageId(), ModelType.NESTED_ENTITY, context.getNestedEntitiesIds());
        }
    }
}

package com.unidata.mdm.backend.service.model.impl;

import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.REL_NAME;
import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.DeleteModelRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.service.model.RecordsCleaner;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.RelationDef;

@Component
public class IndexedRecordsCleaner implements RecordsCleaner {

    /**
     * Meta model
     */
    @Autowired
    private MetaModelService metaModelService;

    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchService;

    @Override
    public void cleanRelatedRecords(DeleteModelRequestContext context) {
        String storageId = context.getStorageId();
        context.getEntitiesIds().forEach(id -> searchService.dropIndex(id, storageId));
        context.getLookupEntitiesIds().forEach(id -> searchService.dropIndex(id, storageId));
        Collection<RelationDef> relationDefs = context.getRelationIds()
                                                      .stream()
                                                      .map(id -> metaModelService.getRelationById(id))
                                                      .collect(Collectors.toList());

        for (RelationDef rel : relationDefs) {
            EntityDef from = metaModelService.getEntityByIdNoDeps(rel.getFromEntity());
            if (from != null && !context.getEntitiesIds().contains(from.getName())) {
                dropAllIndexedRels(rel.getName(), from.getName(), storageId);
            }
            EntityDef to = metaModelService.getEntityByIdNoDeps(rel.getToEntity());
            if (to != null && !context.getEntitiesIds().contains(to.getName())) {
                dropAllIndexedRels(rel.getName(), to.getName(), storageId);
            }
        }
    }

    private void dropAllIndexedRels(String relName, String entityName, String storageId) {
        SearchRequestContext context = SearchRequestContext.forEtalonRelation(entityName)
                                                           .storageId(storageId)
                                                           .values(singletonList(relName))
                                                           .searchFields(singletonList(REL_NAME.getField()))
                                                           .search(SearchRequestType.TERM)
                                                           .onlyQuery(true)
                                                           .build();
        searchService.deleteFoundResult(context);
    }
}

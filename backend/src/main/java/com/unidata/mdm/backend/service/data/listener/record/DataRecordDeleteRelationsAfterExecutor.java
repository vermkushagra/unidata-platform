package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_FROM_ETALON_ID;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.REL_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.service.MetaModelService;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.service.data.RecordsServiceComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

public class DataRecordDeleteRelationsAfterExecutor implements DataRecordAfterExecutor<DeleteRequestContext> {

    @Autowired
    private SearchService searchService;

    @Autowired
    RecordsServiceComponent recordsServiceComponent;

    @Autowired
    private MetaModelService metaModelService;

    @Autowired
    private RelationsServiceComponent relationsServiceComponent;


    @Override
    public boolean execute(DeleteRequestContext deleteRequestContext) {

        RecordKeys keys = deleteRequestContext.keys();

        Map<RelationDef, EntityDef> containsDef = metaModelService.getEntityRelationsByType(
                keys.getEntityName(),
                Collections.singletonList(RelType.CONTAINS),
                false,
                true
        );

        if (MapUtils.isEmpty(containsDef)) {
            return true;
        }

        FormField fromEtalon = FormField.strictString(FIELD_FROM_ETALON_ID.getField(), keys.getEtalonKey().getId());
        FormField relType = FormField.strictString(REL_TYPE.getField(), RelationType.CONTAINS.name());

        List<String> searchFields = new ArrayList<>();
        searchFields.add(RelationHeaderField.FIELD_FROM.getField());
        searchFields.add(RelationHeaderField.FIELD_TO.getField());
        searchFields.add(RelationHeaderField.FIELD_TO_ETALON_ID.getField());
        searchFields.add(RelationHeaderField.FIELD_ETALON_ID.getField());
        searchFields.add(RelationHeaderField.REL_NAME.getField());

        SearchRequestContext relationsContext = SearchRequestContext.forEtalonRelation(keys.getEntityName())
                .form(FormFieldsGroup.createAndGroup(fromEtalon, relType))
                .count(10)
                .scrollScan(true)
                .onlyQuery(true)
                .returnFields(searchFields)
                .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                .build();

        boolean isWipe = deleteRequestContext.isWipe();

        SearchResultDTO relations = searchService.search(relationsContext);
        if (CollectionUtils.isNotEmpty(relations.getHits())) {
            Map<String, List<DeleteRelationRequestContext>> deleteRelationsMap = new HashMap<>();
            List<DeleteRequestContext> etalonToSideForRemove = new ArrayList<>();

            for (SearchResultHitDTO relationHit : relations.getHits()) {
                String relationToId = relationHit.getFieldValue(RelationHeaderField.FIELD_TO_ETALON_ID.getField()).getFirstValue().toString();
                String relationName = relationHit.getFieldValue(RelationHeaderField.REL_NAME.getField()).getFirstValue().toString();
                String relationId = relationHit.getFieldValue(RelationHeaderField.FIELD_ETALON_ID.getField()).getFirstValue().toString();


                etalonToSideForRemove.add(new DeleteRequestContext.DeleteRequestContextBuilder()
                        .etalonKey(relationToId)
                        .inactivateEtalon(deleteRequestContext.isInactivateEtalon())
                        .inactivateOrigin(deleteRequestContext.isInactivateOrigin())
                        .inactivatePeriod(deleteRequestContext.isInactivatePeriod())
                        .wipe(isWipe)
                        .build());

                if (isWipe) {
                    if (!deleteRelationsMap.containsKey(relationName)) {
                        deleteRelationsMap.put(relationName, new ArrayList<>());
                    }
                    deleteRelationsMap.get(relationName).add(new DeleteRelationRequestContext.DeleteRelationRequestContextBuilder()
                            .etalonKey(keys.getEtalonKey())
                            .entityName(keys.getEntityName())
                            .relationEtalonKey(relationId)
                            .inactivateEtalon(deleteRequestContext.isInactivateEtalon())
                            .inactivateOrigin(deleteRequestContext.isInactivateOrigin())
                            .inactivatePeriod(deleteRequestContext.isInactivatePeriod())
                            .wipe(true)
                            .build());

                }
            }

            if (isWipe && MapUtils.isNotEmpty(deleteRelationsMap)) {
                relationsServiceComponent.deleteRelations(new DeleteRelationsRequestContext.DeleteRelationsRequestContextBuilder()
                        .entityName(keys.getEntityName())
                        .etalonKey(keys.getEtalonKey())
                        .relations(deleteRelationsMap)
                        .build());
            } else if (CollectionUtils.isNotEmpty(etalonToSideForRemove)) {
                recordsServiceComponent.deleteRecords(etalonToSideForRemove);
            }
        }

        return true;
    }
}

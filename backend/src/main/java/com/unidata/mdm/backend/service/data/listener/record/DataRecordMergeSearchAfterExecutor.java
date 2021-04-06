/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonRelation;
import static com.unidata.mdm.backend.common.context.SearchRequestContext.forIndex;
import static com.unidata.mdm.backend.common.search.FormField.strictString;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createOrGroup;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_FROM_ETALON_ID;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_TO_ETALON_ID;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 */
public class DataRecordMergeSearchAfterExecutor implements DataRecordAfterExecutor<MergeRequestContext> {
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;

    /**
     * Meta model service
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * Constructor.
     */
    public DataRecordMergeSearchAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(MergeRequestContext ctx) {

        RecordKeys master = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);
        List<RecordKeys> duplicates = ctx.getFromStorage(StorageId.DATA_MERGE_DUPLICATES_KEYS);

        // Remove duplicates.
        // 1. Drop classifier links
        List<String> classifierNames = metaModelService.getClassifiersForEntity(master.getEntityName());
        if (CollectionUtils.isNotEmpty(classifierNames)) {

             List<IndexRequestContext> ircs = duplicates.stream()
                    .map(RecordKeys::getEtalonKey)
                    .map(EtalonKey::getId)
                    .flatMap(eId -> classifierNames.stream()
                            .map(classifierName -> SearchUtils.childPeriodId(eId, classifierName))
                            .map(indexedId -> IndexRequestContext.builder()
                                    .entity(master.getEntityName())
                                    .drop(true)
                                    .classifiersToDelete(Collections.singletonList(indexedId))
                                    .routing(eId)
                                    .build()))
                    .collect(Collectors.toList());

            searchService.index(ircs);
        }

        //remove old references from duplicates
        List<FormField> relFromFields = getFormFields(FIELD_FROM_ETALON_ID, duplicates);
        FormFieldsGroup relFromGroup = createOrGroup(relFromFields);
        List<SearchRequestContext> relsFromCtx = metaModelService.getRelationsByFromEntityName(master.getEntityName())
                                                                 .stream()
                                                                 .map(RelationDef::getToEntity)
                                                                 .distinct()
                                                                 .map(toEntity -> forEtalonRelation(toEntity)
                                                                     .form(relFromGroup)
                                                                     .build())
                                                                 .collect(Collectors.toList());

        List<FormField> dataFields = getFormFields(FIELD_ETALON_ID, duplicates);
        dataFields.addAll(relFromFields);

        //remove duplicates
        SearchRequestContext masterContext = forIndex(master.getEntityName())
                .form(createOrGroup(dataFields))
                .build();

        relsFromCtx.add(masterContext);
        ComplexSearchRequestContext removeContext = ComplexSearchRequestContext.multi(relsFromCtx);
        boolean removeResult = searchService.deleteFoundResult(removeContext);
        if (!removeResult) {
            return false;
        }

        //redirect old references to new master!
        Map<SearchField, Object> fields = Collections.singletonMap(FIELD_TO_ETALON_ID, master.getEtalonKey().getId());
        List<FormField> relToFields = getFormFields(FIELD_TO_ETALON_ID, duplicates);
        FormFieldsGroup relToGroup = createOrGroup(relToFields);
        List<SearchRequestContext> relsToCtx = metaModelService.getRelationsByToEntityName(master.getEntityName())
                                                               .stream()
                                                               .map(RelationDef::getFromEntity)
                                                               .distinct()
                                                               .map(toEntity -> forEtalonRelation(toEntity).form(
                                                                       relToGroup).build())
                                                               .collect(Collectors.toList());
        ComplexSearchRequestContext markContext = ComplexSearchRequestContext.multi(relsToCtx);
        return searchService.mark(markContext, fields);
    }

    private List<FormField> getFormFields(@Nonnull SearchField searchField, @Nonnull List<RecordKeys> duplicates) {
        return duplicates.stream()
                         .map(RecordKeys::getEtalonKey)
                         .map(EtalonKey::getId)
                         .map(id -> strictString(searchField.getField(), id))
                         .collect(Collectors.toList());
    }
}

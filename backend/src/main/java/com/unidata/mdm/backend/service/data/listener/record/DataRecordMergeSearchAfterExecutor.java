/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonClassifier;
import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonRelation;
import static com.unidata.mdm.backend.common.context.SearchRequestContext.forIndex;
import static com.unidata.mdm.backend.common.context.SearchRequestContext.forMatching;
import static com.unidata.mdm.backend.common.search.FormField.strictString;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createOrGroup;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.common.search.fields.RelationHeaderField.FIELD_FROM_ETALON_ID;
import static com.unidata.mdm.backend.common.search.fields.RelationHeaderField.FIELD_TO_ETALON_ID;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.fields.ClassifierDataHeaderField;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
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
        MeasurementPoint.start();
        try {
            RecordKeys master = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS);
            List<RecordKeys> duplicates = ctx.getFromStorage(StorageId.DATA_MERGE_DUPLICATES_KEYS);

            // Remove duplicates.
            // 1. Drop classifier links
            List<String> classifierNames = metaModelService.getClassifiersForEntity(master.getEntityName());
            if (CollectionUtils.isNotEmpty(classifierNames)) {

                List<FormField> dataFields = getFormFields(ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD, duplicates);
                SearchRequestContext classifierContext = forEtalonClassifier(master.getEntityName())
                        .form(createAndGroup(dataFields))
                        .shardNumber(ctx.isDirtyMode() ? ctx.getShardNumber() : null)
                        .build();

                searchService.deleteFoundResult(classifierContext, ctx.isDirtyMode() ? false : !ctx.isBatchUpsert());
            }

            //remove old references from duplicates
            List<FormField> relFromFields = getFormFields(FIELD_FROM_ETALON_ID, duplicates);

            List<FormField> dataFields = getFormFields(FIELD_ETALON_ID, duplicates);
            dataFields.addAll(relFromFields);
            Boolean removeResult = null;
            //remove duplicates
            if(ctx.isDirtyMode()) {
                if (ctx.isClearPreprocessing()) {
                    // only matching without clean data
                    SearchRequestContext matchingContext = forMatching(master.getEntityName())
                            .form(createOrGroup(dataFields))
                            .shardNumber(ctx.getShardNumber())
                            .build();
                    removeResult = searchService.deleteFoundResult(matchingContext, false);
                }
            } else {
                dataFields.addAll(relFromFields);
                SearchRequestContext masterContext = forIndex(master.getEntityName())
                        .form(createOrGroup(dataFields))
                        .build();
                removeResult = searchService.deleteFoundResult(masterContext, !ctx.isBatchUpsert());
            }

            if (removeResult != null && !removeResult) {
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
        } finally {
            MeasurementPoint.stop();
        }
    }

    private List<FormField> getFormFields(@Nonnull SearchField searchField, @Nonnull List<RecordKeys> duplicates) {
        return duplicates.stream()
                .map(RecordKeys::getEtalonKey)
                .map(EtalonKey::getId)
                .map(id -> strictString(searchField.getField(), id))
                .collect(Collectors.toList());
    }
}

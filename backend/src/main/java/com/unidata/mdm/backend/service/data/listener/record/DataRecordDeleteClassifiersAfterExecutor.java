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

package com.unidata.mdm.backend.service.data.listener.record;

import static java.lang.Boolean.TRUE;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTimelineDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.fields.ClassifierDataHeaderField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DataRecordDeleteClassifiersAfterExecutor implements DataRecordAfterExecutor<DeleteRequestContext> {
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;
    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Classifiers component.
     */
    @Autowired
    private ClassifiersServiceComponent classifiersServiceComponent;
    /**
     * Constructor.
     */
    public DataRecordDeleteClassifiersAfterExecutor() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRequestContext ctx) {

        MeasurementPoint.start();
        try {
            // - Kill classifiers if 'wipe' is specified.
            // - Mark as inactive in index, if 'delete etalon' is specified
            // - Kill classifiers if 'delete origin' is specified and the origin being deleted is the last one.
            // - Kill classifiers if 'delete period' is specified and the period being deleted is the last one.
            // - Do nothing if 'delete period' is specified and the period being deleted is not the last one.
            RecordKeys keys = ctx.keys();
            List<String> classifiersNames = metaModelService.getClassifiersForEntity(keys.getEntityName());
            if (CollectionUtils.isEmpty(classifiersNames)) {
                return true;
            }

            boolean inactivateCascadeCondition = inactivateCascadeCondition(ctx);
            if (ctx.isWipe() || inactivateCascadeCondition) {

                // Just delete indexed data, since DB data deleted by cascade
                if (ctx.isWipe()) {
                    SearchRequestContext delCtx = SearchRequestContext.builder(EntitySearchType.CLASSIFIER, keys.getEntityName())
                            .form(FormFieldsGroup.createAndGroup(FormField.strictString(
                                    ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD.getField(),
                                    keys.getEtalonKey().getId())))
                            .onlyQuery(true)
                            .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                            .build();
                    searchService.deleteFoundResult(delCtx);

                } else {
                    DeleteClassifiersDataRequestContext dCtx = DeleteClassifiersDataRequestContext.builder()
                            .classifierNames(classifiersNames)
                            .wipe(ctx.isWipe())
                            .build();

                    dCtx.putToStorage(dCtx.keysId(), keys);
                    classifiersServiceComponent.deleteClassifiers(dCtx);
                }

                return true;
            } else if (ctx.isInactivateEtalon()) {

                // Just disable search data. Don't touch persisted data, since we cannot distinguish
                // classifier records, deleted by the user from those, inactivated by the system during record inactivation
                // and thus can not do clean restore.
                SearchRequestContext delClassifiersCtx = SearchRequestContext.forEtalon(EntitySearchType.CLASSIFIER, keys.getEntityName())
                        .form(FormFieldsGroup
                                .createAndGroup()
                                .addFormField(FormField.strictString(
                                        ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD.getField(),
                                        keys.getEtalonKey().getId())))
                        .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                        .build();

                Date updateDate = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
                SearchField searchField = keys.isPending()
                        ? ClassifierDataHeaderField.FIELD_PENDING
                        : ClassifierDataHeaderField.FIELD_DELETED;

                Map<SearchField, Object> fields = new HashMap<>();
                fields.put(ClassifierDataHeaderField.FIELD_UPDATED_AT, updateDate == null ? new Date() : updateDate);
                fields.put(searchField, TRUE);

                searchService.mark(delClassifiersCtx, fields);
            }
            // TODO support delete origin cascade

        } finally {
            MeasurementPoint.stop();
        }

        return true;
    }

    private boolean inactivateCascadeCondition(DeleteRequestContext ctx) {

        RecordKeys keys = ctx.keys();
        WorkflowTimelineDTO intervals = ctx.getFromStorage(StorageId.DATA_RECORD_TIMELINE);
        return (ctx.isInactivateOrigin() && commonRecordsComponent.allOriginsAlreadyInactive(ctx.keys()))
            || (ctx.isInactivatePeriod() && !keys.isPending() && (Objects.isNull(intervals) || intervals.getIntervals().stream().noneMatch(TimeIntervalDTO::isActive)));
    }
}

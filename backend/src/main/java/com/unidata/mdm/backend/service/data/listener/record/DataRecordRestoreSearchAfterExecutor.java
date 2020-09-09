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

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_FROM;
import static com.unidata.mdm.backend.common.search.fields.RecordHeaderField.FIELD_TO;
import static com.unidata.mdm.meta.SimpleDataType.TIMESTAMP;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.search.SearchServiceExt;

/**
 * @author Mikhail Mikhailov
 *         Listener for DELETE record actions.
 */
public class DataRecordRestoreSearchAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;

    /**
     * Constructor.
     */
    public DataRecordRestoreSearchAfterExecutor() {
        super();
    }

    /**
     * Tools support constructor.
     *
     * @param svc search service
     */
    public DataRecordRestoreSearchAfterExecutor(SearchServiceExt svc) {
        this();
        this.searchService = svc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext context) {

        RecordKeys keys = context.keys();
        if (keys == null || !context.isPeriodRestore()) {
            return true;
        }

        String entityName = keys.getEntityName();
        String id = keys.getEtalonKey().getId();
        Date from = context.getValidFrom();
        Date to = context.getValidTo();
        Date updateDate = context.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        FormFieldsGroup group = createAndGroup(
                FormField.strictString(FIELD_ETALON_ID.getField(), id),
                FormField.range(TIMESTAMP, FIELD_FROM.getField(), null, to),
                FormField.range(TIMESTAMP, FIELD_TO.getField(), from, null));

        SearchRequestContext searchContext = forEtalonData(entityName)
                .form(group)
                .build();

        Map<SearchField, Object> fields = new HashMap<>();
        if(updateDate != null){
            fields.put(RecordHeaderField.FIELD_UPDATED_AT, updateDate);
        }

        fields.put(RecordHeaderField.FIELD_INACTIVE, Boolean.FALSE);
        searchService.mark(searchContext, fields);

        return true;
    }
}

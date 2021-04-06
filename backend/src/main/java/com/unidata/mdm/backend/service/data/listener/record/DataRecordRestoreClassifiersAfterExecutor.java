package com.unidata.mdm.backend.service.data.listener.record;

import static java.util.Collections.singletonList;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.ClassifierDataHeaderField;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DataRecordRestoreClassifiersAfterExecutor implements DataRecordAfterExecutor<UpsertRequestContext> {
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
     * Constructor.
     */
    public DataRecordRestoreClassifiersAfterExecutor() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {
            RecordKeys keys = ctx.keys();
            List<String> classifiersNames = metaModelService.getClassifiersForEntity(keys.getEntityName());
            if (CollectionUtils.isEmpty(classifiersNames)) {
                return true;
            }

            // Just disable search data. Don't touch persisted data, since we cannot distinguish
            // classifier records, deleted by the user from those, inactivated by the system during record inactivation
            // and thus can not do clean restore.
            SearchRequestContext delClassifiersCtx = SearchRequestContext.forEtalon(EntitySearchType.CLASSIFIER, keys.getEntityName())
                    .values(singletonList(keys.getEtalonKey().getId()))
                    .search(SearchRequestType.TERM)
                    .searchFields(singletonList(ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD.getField()))
                    .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                    .build();

            Date updateDate = ctx.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
            SearchField searchField = keys.isPending()
                    ? ClassifierDataHeaderField.FIELD_PENDING
                    : ClassifierDataHeaderField.FIELD_DELETED;

            Map<SearchField, Object> fields = new HashMap<>();
            fields.put(ClassifierDataHeaderField.FIELD_UPDATED_AT, updateDate == null ? new Date() : updateDate);
            fields.put(searchField, Boolean.FALSE);

            searchService.mark(delClassifiersCtx, fields);

        } finally {
            MeasurementPoint.stop();
        }

        return true;
    }
}

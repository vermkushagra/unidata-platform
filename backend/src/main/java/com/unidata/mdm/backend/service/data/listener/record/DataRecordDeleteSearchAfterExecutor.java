package com.unidata.mdm.backend.service.data.listener.record;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_FROM;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_TO;
import static com.unidata.mdm.meta.SimpleDataType.TIMESTAMP;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;

import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.data.batch.RecordDeleteBatchSet;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.MatchingHeaderField;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Mikhail Mikhailov
 *         Listener for DELETE record actions.
 */
public class DataRecordDeleteSearchAfterExecutor implements DataRecordAfterExecutor<DeleteRequestContext> {
    /**
     * Search service.
     */
    @Autowired
    private SearchServiceExt searchService;

    /**
     * Constructor.
     */
    public DataRecordDeleteSearchAfterExecutor() {
        super();
    }

    /**
     * Tools support constructor.
     *
     * @param svc search service
     */
    public DataRecordDeleteSearchAfterExecutor(SearchServiceExt svc) {
        this();
        this.searchService = svc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRequestContext context) {

        // Skip index context generation for batch updates.
        // This will be handled in batch* methods
        RecordKeys keys = context.keys();
        if (keys != null && !context.isInactivateOrigin()) {
            if (context.isInactivatePeriod()) {
                handlePeriodDelete(context);
            } else {
                handleRecordDelete(context);
            }
        }

        return true;
    }

    private void handlePeriodDelete(DeleteRequestContext context) {

        RecordKeys keys = context.keys();
        String entityName = keys.getEntityName();
        String id = keys.getEtalonKey().getId();
        Date from = context.getValidFrom();
        Date to = context.getValidTo();
        Date updateDate = context.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        FormField etalonId = FormField.strictString(FIELD_ETALON_ID.getField(), id);
        FormField fromField = FormField.range(TIMESTAMP, FIELD_FROM.getField(), null, to);
        FormField toField = FormField.range(TIMESTAMP, FIELD_TO.getField(), from, null);
        FormFieldsGroup group = createAndGroup(etalonId, fromField, toField);

        SearchRequestContext searchContext = forEtalonData(entityName)
                .form(group)
                .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                .build();

        if (context.isBatchUpsert()) {

            EtalonRecordInfoSection is = new EtalonRecordInfoSection()
                    .withApproval(keys.isPending() ? ApprovalState.PENDING : null)
                    .withPeriodId(Objects.isNull(to) ? SearchUtils.ES_TIMELINE_PERIOD_ID_UPPER_BOUND : to.getTime())
                    .withEntityName(keys.getEntityName())
                    .withEtalonKey(keys.getEtalonKey())
                    .withStatus(!keys.isPending() ? RecordStatus.INACTIVE : null)
                    .withUpdateDate(updateDate == null ? new Date() : updateDate)
                    .withUpdatedBy(SecurityUtils.getCurrentUserName());

            IndexRequestContext iCtx = IndexRequestContext.builder()
                    .entity(entityName)
                    .recordsToSysUpdate(Collections.singletonList(is))
                    .drop(!keys.isPending())
                    .build();

            RecordDeleteBatchSet batchSet = context.getFromStorage(StorageId.DATA_BATCH_RECORDS);
            batchSet.setIndexRequestContext(iCtx);

        } else {
            if(keys.isPending()){
                Map<RecordHeaderField, Object> fields = new EnumMap<>(RecordHeaderField.class);
                fields.put(RecordHeaderField.FIELD_UPDATED_AT, updateDate == null ? new Date() : updateDate);
                fields.put( RecordHeaderField.FIELD_PENDING , TRUE);

                searchService.mark(searchContext, fields);
            } else {
                searchService.deleteFoundResult(searchContext);
            }

            // Matching
            group = FormFieldsGroup.createAndGroup(
                    FormField.strictString(MatchingHeaderField.FIELD_ETALON_ID.getField(), id),
                    FormField.range(TIMESTAMP, MatchingHeaderField.FIELD_FROM.getField(), null, to),
                    FormField.range(TIMESTAMP, MatchingHeaderField.FIELD_TO.getField(), from, null));

            searchContext = SearchRequestContext.forEtalon(EntitySearchType.MATCHING, entityName)
                    .form(group)
                    .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                    .build();

            searchService.deleteFoundResult(searchContext);
        }
    }

    private void handleRecordDelete(DeleteRequestContext context) {

        RecordKeys keys = context.keys();
        String entityName = keys.getEntityName();
        String id = keys.getEtalonKey().getId();
        Date updateDate = context.getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);

        SearchRequestContext ctx = forEtalonData(entityName)
                .values(singletonList(id))
                .search(SearchRequestType.TERM)
                .searchFields(singletonList(FIELD_ETALON_ID.getField()))
                .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                .build();

        SearchRequestContext delMatchingCtx = SearchRequestContext.forEtalon(EntitySearchType.MATCHING, entityName)
                .values(singletonList(id))
                .search(SearchRequestType.TERM)
                .searchFields(singletonList(FIELD_ETALON_ID.getField()))
                .routings(Collections.singletonList(keys.getEtalonKey().getId()))
                .build();

        // Wipe is not supported so far
        if (context.isWipe()) {
            searchService.deleteFoundResult(ctx);
        } else {

            if (context.isBatchUpsert()) {
                // Will query for version ids the cluster
                EtalonRecordInfoSection is = new EtalonRecordInfoSection()
                        .withApproval(keys.isPending() ? ApprovalState.PENDING : null)
                        .withEntityName(keys.getEntityName())
                        .withEtalonKey(keys.getEtalonKey())
                        .withStatus(!keys.isPending() ? RecordStatus.INACTIVE : null)
                        .withUpdateDate(updateDate == null ? new Date() : updateDate)
                        .withUpdatedBy(SecurityUtils.getCurrentUserName());

                IndexRequestContext iCtx = IndexRequestContext.builder()
                        .entity(keys.getEntityName())
                        .recordsToSysUpdate(Collections.singletonList(is))
                        .build();

                RecordDeleteBatchSet batchSet = context.getFromStorage(StorageId.DATA_BATCH_RECORDS);
                batchSet.setIndexRequestContext(iCtx);
            } else {

                RecordHeaderField searchField = keys.isPending() ? RecordHeaderField.FIELD_PENDING : RecordHeaderField.FIELD_DELETED;

                Map<RecordHeaderField, Object> fields = new EnumMap<>(RecordHeaderField.class);
                fields.put(RecordHeaderField.FIELD_UPDATED_AT, updateDate == null ? new Date() : updateDate);
                fields.put(searchField, TRUE);

                searchService.mark(ctx, fields);
            }
        }

        // Delete matching data anyway.
        // It will be restored upon decline for pending records.
        searchService.deleteFoundResult(delMatchingCtx);
    }
}

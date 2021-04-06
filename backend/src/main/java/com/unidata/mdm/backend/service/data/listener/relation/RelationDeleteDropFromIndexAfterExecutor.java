package com.unidata.mdm.backend.service.data.listener.relation;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonRelation;
import static com.unidata.mdm.backend.common.search.FormFieldsGroup.createAndGroup;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_ETALON_ID;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_FROM;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_TO;
import static com.unidata.mdm.meta.SimpleDataType.TIMESTAMP;

import java.util.Collections;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;
import com.unidata.mdm.backend.service.data.batch.RelationBatchSet;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationDeleteDropFromIndexAfterExecutor implements DataRecordAfterExecutor<DeleteRelationRequestContext> {
    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt service;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRelationRequestContext dCtx) {

        // Only iactivateEtalon is handled right now
        RelationKeys keys = dCtx.relationKeys();
        if (keys != null && !dCtx.isInactivateOrigin()) {
            if (dCtx.isInactivatePeriod()) {
                handleDeletePeriod(dCtx);
            } else {
                handleDeleteRecord(dCtx);
            }
        }

        return true;
    }

    private void handleDeletePeriod(DeleteRelationRequestContext dCtx) {

        RelationKeys keys = dCtx.relationKeys();
        if (dCtx.isBatchUpsert()) {
            /*
            EtalonRelationInfoSection is = new EtalonRelationInfoSection()
                    .withApproval(keys.isPending() ? ApprovalState.PENDING : null)
                    .withFromEntityName(keys.getFrom().getEntityName())
                    .withFromEtalonKey(keys.getFrom().getEtalonKey())
                    .withPeriodId(Objects.isNull(dCtx.getValidTo()) ? SearchUtils.ES_TIMELINE_PERIOD_ID_UPPER_BOUND : dCtx.getValidTo().getTime())
                    .withRelationEtalonKey(keys.getEtalonId())
                    .withRelationName(keys.getRelationName())
                    .withStatus(!keys.isPending() ? RecordStatus.INACTIVE : null)
                    .withToEntityName(keys.getTo().getEntityName())
                    .withToEtalonKey(keys.getTo().getEtalonKey())
                    .withUpdateDate(new Date())
                    .withUpdatedBy(SecurityUtils.getCurrentUserName())
                    .withValidFrom(dCtx.getValidFrom())
                    .withValidTo(dCtx.getValidTo());
            */
            long periodId = Objects.isNull(dCtx.getValidTo()) ? SearchUtils.ES_TIMELINE_PERIOD_ID_UPPER_BOUND : dCtx.getValidTo().getTime();
            IndexRequestContext iCtx = IndexRequestContext.builder()
                    .relationToDelete(keys.getFrom().getEntityName(),
                            SearchUtils.childPeriodId(periodId,
                                    keys.getFrom().getEtalonKey().getId(),
                                    keys.getRelationName(),
                                    keys.getTo().getEtalonKey().getId()))
                    .relationToDelete(keys.getTo().getEntityName(),
                            SearchUtils.childPeriodId(periodId,
                                    keys.getTo().getEtalonKey().getId(),
                                    keys.getRelationName(),
                                    keys.getFrom().getEtalonKey().getId()))
                    .drop(true)
                    .entity(keys.getFrom().getEntityName())
                    .build();

            RelationBatchSet batchSet = dCtx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
            batchSet.setIndexRequestContext(iCtx);
        } else {

            FormField id = FormField.strictString(FIELD_ETALON_ID.getField(), keys.getEtalonId());
            FormField from = FormField.range(TIMESTAMP, FIELD_FROM.getField(), null, dCtx.getValidTo());
            FormField to = FormField.range(TIMESTAMP, FIELD_TO.getField(), dCtx.getValidFrom(), null);
            FormFieldsGroup fieldsGroup = createAndGroup(id, from, to);
            String fromEntity = keys.getFrom().getEntityName();
            String toEntity = keys.getTo().getEntityName();

            SearchRequestContext fromContext = forEtalonRelation(fromEntity)
                    .form(fieldsGroup)
                    .routings(Collections.singletonList(keys.getFrom().getEtalonKey().getId()))
                    .build();

            SearchRequestContext toContext = forEtalonRelation(toEntity)
                    .form(fieldsGroup)
                    .routings(Collections.singletonList(keys.getTo().getEtalonKey().getId()))
                    .build();

            ComplexSearchRequestContext context = ComplexSearchRequestContext.multi(fromContext, toContext);
            service.deleteFoundResult(context);
            /*
            Map<RelationHeaderField, Object> fields = new EnumMap<>(RelationHeaderField.class);
            fields.put(RelationHeaderField.FIELD_UPDATED_AT, new Date());
            fields.put(keys.isPending() ? RelationHeaderField.FIELD_PENDING : RelationHeaderField.FIELD_DELETED, TRUE);

            service.mark(context, fields);
            */
        }
    }

    private void handleDeleteRecord(DeleteRelationRequestContext dCtx) {

        RelationKeys keys = dCtx.relationKeys();

        FormField id = FormField.strictString(RelationHeaderField.FIELD_ETALON_ID.getField(), keys.getEtalonId());
        FormFieldsGroup fieldsGroup = FormFieldsGroup.createAndGroup(id);
        String fromEntity = keys.getFrom().getEntityName();
        String toEntity = keys.getTo().getEntityName();

        SearchRequestContext fromContext = forEtalonRelation(fromEntity)
                .form(fieldsGroup)
                .routings(Collections.singletonList(keys.getFrom().getEtalonKey().getId()))
                .build();

        SearchRequestContext toContext = forEtalonRelation(toEntity)
                .form(fieldsGroup)
                .routings(Collections.singletonList(keys.getTo().getEtalonKey().getId()))
                .build();

        ComplexSearchRequestContext context = ComplexSearchRequestContext.multi(fromContext, toContext);
        if (dCtx.isWipe()) {
            // Wipe is not supported via batch so far
            service.deleteFoundResult(context);
        } else {

            if (dCtx.isBatchUpsert()) {
                // Query for ids
                EtalonRelationInfoSection is = new EtalonRelationInfoSection()
                        //.withApproval(keys.isPending() ? ApprovalState.PENDING : null)
                        .withFromEntityName(keys.getFrom().getEntityName())
                        .withFromEtalonKey(keys.getFrom().getEtalonKey())
                        .withRelationEtalonKey(keys.getEtalonId())
                        .withRelationName(keys.getRelationName())
                        //.withStatus(!keys.isPending() ? RecordStatus.INACTIVE : null)
                        .withToEntityName(keys.getTo().getEntityName())
                        .withToEtalonKey(keys.getTo().getEtalonKey())
                        //.withUpdateDate(new Date())
                        //.withUpdatedBy(SecurityUtils.getCurrentUserName())
                        //.withValidFrom(dCtx.getValidFrom())
                        //.withValidTo(dCtx.getValidTo())
                        ;

                IndexRequestContext iCtx = IndexRequestContext.builder()
                        .relationsToQueryDelete(Collections.singletonList(is))
                        .drop(true)
                        .entity(keys.getFrom().getEntityName())
                        .build();

                RelationBatchSet batchSet = dCtx.getFromStorage(StorageId.DATA_BATCH_RELATIONS);
                batchSet.setIndexRequestContext(iCtx);

            } else {
                /*
                RelationHeaderField searchField = keys.isPending() ? RelationHeaderField.FIELD_PENDING : RelationHeaderField.FIELD_DELETED;

                Map<RelationHeaderField, Object> fields = new EnumMap<>(RelationHeaderField.class);
                fields.put(RelationHeaderField.FIELD_UPDATED_AT, new Date());
                fields.put(searchField, TRUE);

                service.mark(context, fields);
                */
                service.deleteFoundResult(context);
            }
        }
    }
}

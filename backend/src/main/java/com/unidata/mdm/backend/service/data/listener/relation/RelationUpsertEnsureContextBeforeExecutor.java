package com.unidata.mdm.backend.service.data.listener.relation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.OriginRelationInfoSection;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.OriginRelationImpl;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Prepares upsert context.
 */
public class RelationUpsertEnsureContextBeforeExecutor
        implements DataRecordBeforeExecutor<UpsertRelationRequestContext> {
    /**
     * Common component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRelationRequestContext ctx) {

        RelationDef relation = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
        RecordKeys fromKey = ctx.getFromStorage(StorageId.RELATIONS_FROM_KEY);
        RecordKeys toKey = ctx.getFromStorage(StorageId.RELATIONS_TO_KEY);

        RelationKeys relationKeys = commonRelationsComponent.ensureAndGetRelationKeys(relation.getName(), ctx);
        Date ts = new Date(System.currentTimeMillis());

        OriginRelation originRelation = new OriginRelationImpl()
                .withDataRecord(ctx.getRelation() == null ? new SerializableDataRecord() : ctx.getRelation())
                .withInfoSection(new OriginRelationInfoSection()
                        .withShift(DataShift.PRISTINE)
                        .withType(RelationType.fromValue(relation.getRelType().name()))
                        .withValidFrom(ctx.getValidFrom())
                        .withValidTo(ctx.getValidTo())
                        .withFromEntityName(relation.getFromEntity())
                        .withToEntityName(relation.getToEntity())
                        .withRelationName(relation.getName())
                        .withApproval(relationKeys == null ? null : relationKeys.getEtalonState())
                        .withFromOriginKey(fromKey == null ? null : fromKey.getOriginKey())
                        .withToOriginKey(toKey == null ? null : toKey.getOriginKey())
                        .withRelationOriginKey(relationKeys == null ? null : relationKeys.getOriginId())
                        .withRelationSourceSystem(relationKeys == null
                            ? fromKey == null ? null : fromKey.getOriginKey().getSourceSystem()
                            : relationKeys.getOriginSourceSystem())
                        .withStatus(relationKeys == null ? null : relationKeys.getOriginStatus())
                        .withCreateDate(ts)
                        .withUpdateDate(ts)
                        .withCreatedBy(SecurityUtils.getCurrentUserName())
                        .withUpdatedBy(SecurityUtils.getCurrentUserName()));

        ctx.putToStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION, relationKeys == null ? UpsertAction.INSERT : UpsertAction.UPDATE);
        ctx.putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, ts);
        ctx.putToStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD, originRelation);

        return true;
    }
}

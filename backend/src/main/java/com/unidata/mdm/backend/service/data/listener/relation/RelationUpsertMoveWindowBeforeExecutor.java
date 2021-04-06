package com.unidata.mdm.backend.service.data.listener.relation;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationUpsertMoveWindowBeforeExecutor implements DataRecordBeforeExecutor<UpsertRelationRequestContext> {
    /**
     * Common relations component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRelationRequestContext uCtx) {

        RelationDef relationDef = uCtx.getFromStorage(StorageId.RELATIONS_META_DEF);
        if (relationDef.getRelType() != RelType.MANY_TO_MANY) {
            return true;
        }

        RelationKeys keys = commonRelationsComponent.ensureAndGetRelationKeys(relationDef.getName(), uCtx);
        if (keys != null) {

            // There must be always only one moving window (active interval)
            TimelineDTO timeline = commonRelationsComponent.loadRelationTimeline(keys.getEtalonId(), true, false);
            for (int i = 0; timeline != null && i < timeline.getIntervals().size(); i++) {
                TimeIntervalDTO interval = timeline.getIntervals().get(i);
                if (!interval.isActive()) {
                    continue;
                }

                // Order of arguments matters,
                // since interval.getValid*() is a java.sql.Timestamp
                // and ctx.getValid*() is a java.util.Date!
                boolean fromMatches = Objects.equals(uCtx.getValidFrom(), interval.getValidFrom());
                boolean toMatches = Objects.equals(uCtx.getValidTo(), interval.getValidTo());
                if (fromMatches && toMatches) {
                    break;
                }

                ApprovalState state = DataRecordUtils.calculateVersionState(uCtx, keys.getFrom(),
                        uCtx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS));

                OriginsVistoryRelationsPO version
                    = DataRecordUtils.createInactiveRelationsVistoryRecordPO(
                        keys.getOriginId(), uCtx.getOperationId(),
                        interval.getValidFrom(),
                        interval.getValidTo(), state);

                commonRelationsComponent.putVersion(uCtx, version);

                if (state == ApprovalState.PENDING) {
                    commonRelationsComponent.possiblyResetPendingState(keys, uCtx);
                }

                break;
            }
        }

        return true;
    }
}

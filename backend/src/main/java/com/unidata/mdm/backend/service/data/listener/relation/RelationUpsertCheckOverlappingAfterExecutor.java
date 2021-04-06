package com.unidata.mdm.backend.service.data.listener.relation;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.dto.TimeIntervalDTO;
import com.unidata.mdm.backend.common.dto.TimelineDTO;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationUpsertCheckOverlappingAfterExecutor implements DataRecordAfterExecutor<UpsertRelationRequestContext> {
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
        if (relationDef.getRelType() != RelType.REFERENCES) {
            return true;
        }

        RelationKeys keys = uCtx.relationKeys();
        Date validFrom = uCtx.getValidFrom();
        Date validTo = uCtx.getValidTo();

        // Check references for overlapping. Only one reference of a type is allowed for a period
        List<TimelineDTO> timelines
            = commonRelationsComponent.loadRelationsTimeline(keys.getFrom().getEtalonKey().getId(), relationDef.getName(),
                validFrom, validTo, true, true);

        for (TimelineDTO timeline : timelines) {

            // Skip self
            if (Objects.equals(timeline.getEtalonId(), keys.getEtalonId())) {
                continue;
            }

            boolean alreadyInactive = false;
            for (TimeIntervalDTO interval : timeline.getIntervals()) {

                alreadyInactive
                    = Objects.equals(validFrom, interval.getValidFrom())
                   && Objects.equals(validTo, interval.getValidTo())
                   && !interval.isActive();

                if (alreadyInactive) {
                    break;
                }
            }

            // Create inactive period
            if (!alreadyInactive) {

                RelationKeys sysKeys = commonRelationsComponent.identify(GetRelationRequestContext.builder()
                        .relationEtalonKey(timeline.getEtalonId())
                        .build());

                ApprovalState state = DataRecordUtils.calculateVersionState(uCtx, keys.getFrom(),
                        uCtx.getFromStorage(StorageId.RELATIONS_FROM_WF_ASSIGNMENTS));

                OriginsVistoryRelationsPO version
                    = DataRecordUtils.createInactiveRelationsVistoryRecordPO(
                        sysKeys.getOriginId(), uCtx.getOperationId(),
                        validFrom,
                        validTo, state);

                commonRelationsComponent.putVersion(uCtx, version);
            }
        }

        return true;
    }
}

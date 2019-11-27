package org.unidata.mdm.data.service.segments.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.context.UpsertRelationsRequestContext;
import org.unidata.mdm.data.dto.RelationStateDTO;
import org.unidata.mdm.data.dto.UpsertRelationDTO;
import org.unidata.mdm.data.dto.UpsertRelationsDTO;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.system.context.CompositeRequestContext;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.service.ExecutionService;
import org.unidata.mdm.system.type.pipeline.Connector;
import org.unidata.mdm.system.type.pipeline.Pipeline;

/**
 * @author Mikhail Mikhailov on Nov 24, 2019
 */
@Component(RelationsUpsertConnectorExecutor.SEGMENT_ID)
public class RelationsUpsertConnectorExecutor extends Connector<PipelineExecutionContext, UpsertRelationsDTO> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATIONS_UPSERT_CONNECTOR]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relations.upsert.connector.description";
    /**
     * The ES instance.
     */
    @Autowired
    private ExecutionService executionService;
    /**
     * Constructor.
     * @param id
     * @param description
     */
    public RelationsUpsertConnectorExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRelationsDTO connect(PipelineExecutionContext ctx) {

        CompositeRequestContext target = (CompositeRequestContext) ctx;
        UpsertRelationsRequestContext payload = target.fragment(UpsertRelationsRequestContext.FRAGMENT_ID);
        if (Objects.isNull(payload)) {
            return null;
        }

        UpsertRelationsDTO result = new UpsertRelationsDTO();
        for (Entry<String, List<UpsertRelationRequestContext>> entry : payload.getRelations().entrySet()) {


        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpsertRelationsDTO connect(PipelineExecutionContext ctx, Pipeline p) {

        CompositeRequestContext target = (CompositeRequestContext) ctx;
        UpsertRelationsRequestContext payload = target.fragment(UpsertRelationsRequestContext.FRAGMENT_ID);
        if (Objects.isNull(payload)) {
            return null;
        }

        UpsertRelationsDTO result = new UpsertRelationsDTO();
        for (Entry<String, List<UpsertRelationRequestContext>> entry : payload.getRelations().entrySet()) {

            RelationStateDTO state = new RelationStateDTO();
            List<UpsertRelationDTO> collected = new ArrayList<>();
            for (UpsertRelationRequestContext uCtx : entry.getValue()) {

                UpsertRelationDTO interim = executionService.execute(p, uCtx);
                if (Objects.isNull(interim)) {
                    continue;
                }

                collected.add(interim);
            }

            result.getRelations().put(state, collected);
        }

        return result;
    }
}

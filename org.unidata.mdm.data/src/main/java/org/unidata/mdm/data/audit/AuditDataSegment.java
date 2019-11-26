package org.unidata.mdm.data.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.service.AuditService;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetRequestContext;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

import java.util.Collections;

/**
 * @author Alexander Malyshev
 */
@Component(AuditDataSegment.SEGMENT_ID)
public class AuditDataSegment extends Point {

    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[AUDIT_DATA_SEGMENT]";

    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".audit.data.segment";

    @Autowired
    private AuditService auditService;

    /**
     * Constructor.
     */
    public AuditDataSegment() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    @Override
    public void point(PipelineExecutionContext ctx) {
        auditService.writeEvent(
                AuditDataUtils.auditEventType(ctx),
                Collections.singletonMap(AuditDataConstants.CONTEXT_FILED, ctx)
        );
    }

    @Override
    public boolean supports(Start<?> start) {
        final Class<?> inputTypeClass = start.getInputTypeClass();
        return UpsertRequestContext.class.isAssignableFrom(inputTypeClass)
                || GetRequestContext.class.isAssignableFrom(inputTypeClass)
                || DeleteRequestContext.class.isAssignableFrom(inputTypeClass);
    }
}

package org.unidata.mdm.data.audit;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.audit.AuditConstants;
import org.unidata.mdm.core.service.AuditService;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.type.pipeline.Fallback;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Alexander Malyshev
 */
@Component(AuditDataFallback.SEGMENT_ID)
public class AuditDataFallback extends Fallback<PipelineExecutionContext> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[AUDIT_DATA_FALLBACK]";

    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".audit.data.fallback";

    private final AuditService auditService;

    public AuditDataFallback(AuditService auditService) {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
        this.auditService = auditService;
    }

    @Override
    public void accept(PipelineExecutionContext pipelineExecutionContext, Throwable throwable) {
        auditService.writeEvent(
                AuditDataUtils.auditEventType(pipelineExecutionContext),
                Maps.of(
                        AuditDataConstants.CONTEXT_FILED, pipelineExecutionContext,
                        AuditConstants.EXCEPTION_FIELD, throwable
                )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return true;
    }
}

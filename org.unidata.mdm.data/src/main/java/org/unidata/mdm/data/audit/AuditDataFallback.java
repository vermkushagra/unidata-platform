package org.unidata.mdm.data.audit;

import org.springframework.stereotype.Component;
import org.unidata.mdm.core.audit.AuditConstants;
import org.unidata.mdm.core.service.AuditService;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.system.context.PipelineExecutionContext;
import org.unidata.mdm.system.type.pipeline.Fallback;

/**
 * @author Alexander Malyshev
 */
@Component
public class AuditDataFallback implements Fallback {

    private final AuditService auditService;

    public AuditDataFallback(AuditService auditService) {
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


}

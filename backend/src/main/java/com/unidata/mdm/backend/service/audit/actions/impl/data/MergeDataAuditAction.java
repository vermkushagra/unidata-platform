package com.unidata.mdm.backend.service.audit.actions.impl.data;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class MergeDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "MERGE";

    @Override
    public void enrichEvent(Event event, Object... input) {
        MergeRequestContext context = (MergeRequestContext) input[0];
        putRecordInfo(context, event);
        context.getDuplicates().forEach(ctx -> putRecordInfo(ctx, event));

        if (context.isManual()) {
            event.putDetails("Ручное объединение");
        } else {
            event.putDetails("Автоматическое объединение");
        }
        event.putOperationId(context.getOperationId());
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof MergeRequestContext;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}

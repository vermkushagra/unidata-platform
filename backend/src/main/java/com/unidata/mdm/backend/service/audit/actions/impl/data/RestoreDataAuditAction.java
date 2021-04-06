package com.unidata.mdm.backend.service.audit.actions.impl.data;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.search.Event;

import static com.unidata.mdm.backend.common.types.UpsertAction.NO_ACTION;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class RestoreDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "RESTORE";

    @Override
    public void enrichEvent(Event event, Object... input) {
        UpsertRequestContext context = (UpsertRequestContext) input[0];
        putRecordInfo(context, event);
        UpsertAction action = context.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        action = action == null ? NO_ACTION : action;
        switch (action) {
            case UPDATE:
                event.putDetails("Запись была обновлена");
                break;
            case NO_ACTION:
                event.putDetails("Запись была востановленна без изменений");
                break;
        }

        enrichWithDQMessages(event, context);
        event.putOperationId(context.getOperationId());
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof UpsertRequestContext;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}

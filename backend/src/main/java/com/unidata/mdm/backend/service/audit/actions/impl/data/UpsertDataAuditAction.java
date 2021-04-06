package com.unidata.mdm.backend.service.audit.actions.impl.data;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.search.Event;

import static com.unidata.mdm.backend.common.types.UpsertAction.INSERT;
import static com.unidata.mdm.backend.common.types.UpsertAction.NO_ACTION;
import static com.unidata.mdm.backend.common.types.UpsertAction.UPDATE;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class UpsertDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "UPSERT";

    public static final String INSERT_ACTION_NAME = INSERT.name();

    public static final String UPDATE_ACTION_NAME = UPDATE.name();

    @Override
    public void enrichEvent(Event event, Object... input) {
        UpsertRequestContext context = (UpsertRequestContext) input[0];
        putRecordInfo(context, event);

        UpsertAction action = context.getFromStorage(StorageId.DATA_UPSERT_EXACT_ACTION);
        action = action == null ? NO_ACTION : action;
        String actionDetails = action == INSERT ? NEW_RECORD : action == UPDATE ? UPDATE_RECORD : "";
        String range = getValidityRange(context);
        event.putDetails(actionDetails + "|" + range);
        event.putAction(action.name());

        enrichWithDQMessages(event, context);
        enrichByRowNum(event, context.getFromStorage(StorageId.IMPORT_ROW_NUM));
        enrichByImportSource(event, context.getFromStorage(StorageId.IMPORT_RECORD_SOURCE));
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

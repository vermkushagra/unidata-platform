package com.unidata.mdm.backend.service.audit.actions.impl.data;

import static com.unidata.mdm.backend.common.types.UpsertAction.INSERT;
import static com.unidata.mdm.backend.common.types.UpsertAction.NO_ACTION;
import static com.unidata.mdm.backend.common.types.UpsertAction.UPDATE;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Mikhail Mikhailov
 * Delete relation audit action.
 */
public class UpsertRelationAuditAction extends DataAuditAction {
    /**
     * Action name.
     */
    private static final String ACTION_NAME = "DELETE_RELATION";

    /**
     * {@inheritDoc}
     */
    @Override
    public void enrichEvent(Event event, Object... input) {

        UpsertRelationRequestContext context = (UpsertRelationRequestContext) input[0];
        putRecordInfo(context, event);

        UpsertAction action = context.getFromStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION);
        action = action == null ? NO_ACTION : action;
        String actionDetails = action == INSERT ? NEW_RECORD : action == UPDATE ? UPDATE_RECORD : "";
        String range = getValidityRange(context);
        event.putDetails(actionDetails + "|" + range);

        enrichByRowNum(event, context.getFromStorage(StorageId.IMPORT_ROW_NUM));
        enrichByImportSource(event, context.getFromStorage(StorageId.IMPORT_RECORD_SOURCE));
        event.putOperationId(context.getOperationId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof UpsertRelationRequestContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return ACTION_NAME;
    }

}

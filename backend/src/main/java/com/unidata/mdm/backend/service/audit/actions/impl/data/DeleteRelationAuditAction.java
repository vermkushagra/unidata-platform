package com.unidata.mdm.backend.service.audit.actions.impl.data;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DeleteRelationAuditAction extends DataAuditAction {
    /**
     * Action name.
     */
    private static final String ACTION_NAME = "DELETE_RELATION";

    /**
     * {@inheritDoc}
     */
    @Override
    public void enrichEvent(Event event, Object... input) {

        DeleteRelationRequestContext context = (DeleteRelationRequestContext) input[0];
        putRecordInfo(context, event);

        String range = getValidityRange(context);
        event.putDetails(DELETE_RECORD + "|" + range);

        enrichByRowNum(event, context.getFromStorage(StorageId.IMPORT_ROW_NUM));
        enrichByImportSource(event, context.getFromStorage(StorageId.IMPORT_RECORD_SOURCE));
        event.putOperationId(context.getOperationId());

        event.addEtalonId(context.getRelationEtalonKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof DeleteRelationRequestContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return ACTION_NAME;
    }

}

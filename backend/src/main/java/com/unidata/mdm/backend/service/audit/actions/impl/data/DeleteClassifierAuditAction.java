package com.unidata.mdm.backend.service.audit.actions.impl.data;

import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Mikhail Mikhailov
 *
 */
public class DeleteClassifierAuditAction extends DataAuditAction {
    /**
     * Action name.
     */
    private static final String ACTION_NAME = "DELETE_CLASSIFIER";

    /**
     * {@inheritDoc}
     */
    @Override
    public void enrichEvent(Event event, Object... input) {

        DeleteClassifierDataRequestContext context = (DeleteClassifierDataRequestContext) input[0];
        putRecordInfo(context, event);

        if (context.isInactivateEtalon() || context.isWipe()) {
            String type = context.isWipe() ? "Физическое " : "Логическое ";
            event.putDetails(type + "удаление записи данных классификатора.");
        } else if (context.isInactivateOrigin()) {
            event.putDetails("Удаление оригинальной записи данных классификатора.");
        } else if (context.isInactivatePeriod()) {
            String details = "Инактивация периода записи данных классификатора.";
            String range = getValidityRange(context);
            event.putDetails(details + "|" + range);
        }

        enrichByRowNum(event, context.getFromStorage(StorageId.IMPORT_ROW_NUM));
        enrichByImportSource(event, context.getFromStorage(StorageId.IMPORT_RECORD_SOURCE));
        event.putOperationId(context.getOperationId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof UpsertClassifierDataRequestContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return ACTION_NAME;
    }

}

package com.unidata.mdm.backend.service.audit.actions.impl.data;

import com.unidata.mdm.backend.po.ImportErrorPO;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class ImportDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "UPSERT";

    @Override
    public void enrichEvent(Event event, Object... input) {
        ImportErrorPO errorPO = (ImportErrorPO) input[0];
        event.putOperationId(errorPO.getOperationId());
        enrichByRowNum(event, errorPO.getIndex());
        Object existing = event.get(Event.DETAILS);
        String row = "|Запись: " + errorPO.getDescription();
        event.reclaim(Event.DETAILS, existing.toString() + row);
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof ImportErrorPO;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}

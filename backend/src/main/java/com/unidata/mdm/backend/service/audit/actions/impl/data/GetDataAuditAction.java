package com.unidata.mdm.backend.service.audit.actions.impl.data;

import static com.unidata.mdm.backend.service.search.Event.DATE_FORMATTER;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class GetDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "GET";

    @Override
    public void enrichEvent(Event event, Object... input) {
        GetRequestContext context = (GetRequestContext) input[0];
        putRecordInfo(context, event);
        Date forDate = context.getForDate();
        Date forLastUpdate = context.getForLastUpdate();
        String details = forDate == null ? StringUtils.EMPTY : "На " + DATE_FORMATTER.get().format(forDate) + ". ";
        String lastUpdateDetails = forLastUpdate == null ?
                StringUtils.EMPTY :
                "С последней датой обновления " + DATE_FORMATTER.get().format(forLastUpdate) + ".";
        details = details + lastUpdateDetails;
        event.putDetails(details);
        event.putOperationId(context.getOperationId());
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 1 && input[0] instanceof GetRequestContext;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}

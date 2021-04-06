package com.unidata.mdm.backend.service.audit.actions.impl.data;

import java.util.Date;

import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.service.search.Event;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class ExportDataAuditAction extends DataAuditAction {

    private static final String ACTION_NAME = "EXPORT";

    @Override
    public void enrichEvent(Event event, Object... input) {
        GetRecordDTO record = (GetRecordDTO) input[0];
        String operationId = (String) input[1];
        String sourceSystem = (String) input[2];
        Date asOf = (Date) input[3];
        Date updateAfter = (Date) input[4];
        String action = (String) input[5];

        RecordKeys keys = record.getRecordKeys();
        event.putEntity(keys.getEntityName());
        event.addEtalonId(keys.getEtalonKey() == null ? null : keys.getEtalonKey().getId());
        OriginKey originKey = keys.getKeyBySourceSystem(sourceSystem);
        originKey = originKey == null ? keys.getOriginKey() : originKey;
        event.addOriginId(originKey == null ? null : originKey.getId());
        event.addExternalId(originKey == null ? null : originKey.getExternalId());
        event.putSourceSystem(originKey == null ? null : originKey.getSourceSystem());

        String details = "Экспорт записи с GSN [" + record.getRecordKeys().getGsn() + "], параметризованый датой среза [";
        details = details + (asOf == null ?
                "текущая дата" :
                Event.DATE_FORMATTER.get().format(asOf)) + "]";

        details = details + ", последнее обновление не позднее [";
        details = details + (updateAfter == null ?
                "не указана" :
                Event.DATE_FORMATTER.get().format(updateAfter)) + "]";

        details = details + ", завершившийся с индикатором [" + action + "]";

        event.putDetails(details);
        event.putOperationId(operationId);
    }

    @Override
    public boolean isValidInput(Object... input) {
        return input.length == 6 && input[0] instanceof GetRecordDTO;
    }

    @Override
    public String name() {
        return ACTION_NAME;
    }
}

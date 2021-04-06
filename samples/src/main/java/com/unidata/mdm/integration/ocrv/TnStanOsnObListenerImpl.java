package com.unidata.mdm.integration.ocrv;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.integration.exits.ExecutionContext;
import com.unidata.mdm.backend.common.integration.exits.ExitConstants;
import com.unidata.mdm.backend.common.integration.exits.ExitException;
import com.unidata.mdm.backend.common.integration.exits.ExitState;
import com.unidata.mdm.backend.common.integration.exits.UpsertListener;
import com.unidata.mdm.backend.common.service.ServiceUtils;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;


/**
 * @author Mikhail Mikhailov
 * TN_STAN_OSN_OB upsert handler.
 * Will create a new validity period in case of some changes to the data.
 */
public class TnStanOsnObListenerImpl implements UpsertListener {

    /**
     * Constructor.
     */
    public TnStanOsnObListenerImpl() {
        super();
    }

    /**
     * Attributes to map.
     * @param record the record
     * @return map
     */
    private Map<String, SimpleAttribute<?>> recordToMap(DataRecord record) {

        Map<String, SimpleAttribute<?>> result = new HashMap<>();

        // STAN_OSN has only one attributes nesting level
        for (SimpleAttribute<?> attr : record.getSimpleAttributes()) {
            result.put(attr.getName(), attr);
        }

        return result;
    }

    /**
     * Compare data attributes and return true, if some changes have been detected.
     * @param left attributes map one
     * @param right attributes map two
     * @return true, if attributes differ, false otherwise
     */
    private boolean dataChanged(Map<String, SimpleAttribute<?>> left, Map<String, SimpleAttribute<?>> right) {

        boolean hasChanges = false;
        for (Entry<String, SimpleAttribute<?>> entry : left.entrySet()) {

            SimpleAttribute<?> leftValue = entry.getValue();
            SimpleAttribute<?> rightValue = right.get(entry.getKey());

            if (rightValue == null) {
                hasChanges = true;
            } else {
                switch (leftValue.getDataType()) {
                case STRING:
                case BOOLEAN:
                case INTEGER:
                case NUMBER:
                case DATE:
                case TIME:
                case TIMESTAMP:
                    hasChanges = Objects.equals(rightValue.getValue(), leftValue.getValue());
                    break;
                case BLOB:
                case CLOB:
                default:
                    break;
                }
            }

            if (hasChanges) {
                break;
            }
        }

        return hasChanges;
    }

    /**
     * Check lower or upper boundary for being the same instant.
     * @param d1 date 1
     * @param d2 date 2
     * @return true if both  dates are null or represent the same instant in time
     */
    private boolean isSameInstant(Date d1, Date d2) {
        return (d1 == null ? 0 : d1.getTime()) == (d2 == null ? 0 : d2.getTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean beforeOriginUpdate(OriginRecord origin, ExecutionContext ctx) {

        Date validFrom = ctx.getFromUserContext(ExitConstants.IN_UPSERT_CURRENT_RECORD_VALID_FROM.name());
        Date validTo = ctx.getFromUserContext(ExitConstants.IN_UPSERT_CURRENT_RECORD_VALID_TO.name());

        Date systemValidFrom = ((UpsertRequestContext) ctx).getValidFrom();
        Date systemValidTo = ((UpsertRequestContext) ctx).getValidTo();

        // UN-841, don't touch anything, if dates defined on request
        if (!isSameInstant(systemValidFrom, validFrom) || !isSameInstant(systemValidTo, validTo)) {
            return true;
        }

        GetRecordDTO result = ServiceUtils.getDataRecordsService()
                .getRecord(GetRequestContext.builder()
                        .originKey(origin.getInfoSection().getOriginKey())
                        .build());

        EtalonRecord previous = result != null ? result.getEtalon() : null;
        // No current etalon, let create one
        if (previous == null) {
            /*
            System.err.println("DataService.findEtalonRecord(origin) returned null etalon! Upsert unsuccessful.");
            throw new ExitException(ExitState.ES_GENERAL_FAILURE,
                    "DataService.findEtalonRecord(origin) returned null etalon! Upsert unsuccessful.");
            */
            return true;
        }

        Map<String, SimpleAttribute<?>> thisValues = recordToMap(origin);
        Map<String, SimpleAttribute<?>> prevValues = recordToMap(previous);

        if ((thisValues.size() != prevValues.size())
         || dataChanged(thisValues, prevValues)) {

            // Create new period
            Calendar cal = Calendar.getInstance(); // locale-specific
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            ctx.putToUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_VALID_FROM.name(), cal.getTime());
            ctx.putToUserContext(ExitConstants.OUT_UPSERT_CURRENT_RECORD_VALID_TO.name(), systemValidTo);

            return true;
        }

        // Cancel transaction on no changes
        throw new ExitException(ExitState.ES_UPSERT_DENIED, "The data didn't changed. Upsert denied.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterOriginUpdate(OriginRecord origin, ExecutionContext ctx) {
        // Nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterUpdateEtalonComposition(EtalonRecord etalon, ExecutionContext ctx) {
        // Nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean beforeOriginInsert(OriginRecord origin, ExecutionContext ctx) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterOriginInsert(OriginRecord origin, ExecutionContext ctx) {
        // Nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterInsertEtalonComposition(EtalonRecord etalon, ExecutionContext ctx) {
        // Nothing
    }
}

package com.unidata.mdm.backend.service.data.listener.record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;

/**
 * @author Mikhail Mikhailov
 *         Merge validator.
 */
public class DataRecordMergeValidateBeforeExecutor
        implements DataRecordBeforeExecutor<MergeRequestContext> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordMergeValidateBeforeExecutor.class);
    /**
     * Etalon component.
     */
    @Autowired
    private EtalonRecordsComponent etalonRecordsComponent;
    /**
     * Common component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Constructor.
     */
    public DataRecordMergeValidateBeforeExecutor() {
        super();
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.data.listener.DataRecordExecutor#execute(com.unidata.mdm.backend.common.context.CommonRequestContext)
     */
    @Override
    public boolean execute(MergeRequestContext ctx) {

        // 1. Select first duplicates member, if master key was not given
        // otherwise take from the context
        RecordIdentityContext masterCtx = ctx.isValidRecordKey()
                ? ctx
                : ctx.getDuplicates().remove(ctx.getDuplicates().size() - 1);

        RecordKeys keys = commonRecordsComponent.identify(masterCtx);
        if (keys == null) {
            final String message = "Master record not found by supplied keys etalon id: [{}], origin id [{}], external id [{}], source system [{}], name [{}]";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_MERGE_NOT_FOUND_BY_SUPPLIED_KEYS,
                    ctx.getEtalonKey(),
                    ctx.getOriginKey(),
                    ctx.getExternalId(),
                    ctx.getSourceSystem(),
                    ctx.getEntityName());
        }

        if (!keys.isEtalonActive() || keys.isPending()) {
            final String message = "Record with keys etalon id: [{}], origin id: [{}] has incorrect state for merge status: [{}], approvalState: [{}]";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_MERGE_VALIDATE_INCORRECT_RECORD_STATE,
                    keys.getEtalonKey().getId(),
                    keys.getOriginKey().getId(),
                    keys.getEtalonStatus(),
                    keys.getEtalonState());
        }

        // 2. Collect duplicates keys
        List<RecordKeys> duplicatesKeys = new ArrayList<>();
        for (RecordIdentityContext dCtx : ctx.getDuplicates()) {

            RecordKeys dKeys = commonRecordsComponent.identify(dCtx);
            if (dKeys == null){
                final String message = "Merge failed, some or all duplicates were not found.";
                LOGGER.warn(message, duplicatesKeys);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_MERGE_DUPLICATES_NOT_FOUND);
            }

            if (!dKeys.isEtalonActive() || dKeys.isPending()) {
                final String message = "Record with keys etalon id: [{}], origin id: [{}] has incorrect state for merge status: [{}], approvalState: [{}]";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_MERGE_VALIDATE_INCORRECT_RECORD_STATE,
                        dKeys.getEtalonKey().getId(),
                        dKeys.getOriginKey().getId(),
                        dKeys.getEtalonStatus(),
                        dKeys.getEtalonState());
            }

            if(!dKeys.getEtalonKey().getId().equals(keys.getEtalonKey().getId())) {
                duplicatesKeys.add(dKeys);
            }
        }

        ctx.putToStorage(StorageId.DATA_MERGE_KEYS, keys);
        ctx.putToStorage(StorageId.DATA_MERGE_DUPLICATES_KEYS, duplicatesKeys);

        Map<String, Date> recordCalcDateMap = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS_FOR_DATES);

        // 3. Run before actions.
        EtalonRecord master
                = etalonRecordsComponent.loadEtalonData(keys.getEtalonKey().getId(),
                getDateForCalculateOrigins(recordCalcDateMap, keys.getEtalonKey().getId()),
                null, null, null, false, false);
        List<EtalonRecord> duplicates = new ArrayList<>();
        for (RecordKeys key : duplicatesKeys) {
            EtalonRecord duplicate = etalonRecordsComponent.loadEtalonData(key.getEtalonKey().getId(),
                    getDateForCalculateOrigins(recordCalcDateMap, key.getEtalonKey().getId()),
                    null, null, null, false, false);
            if (duplicate != null) {
                duplicates.add(duplicate);
            }
        }

        ctx.putToStorage(StorageId.DATA_MERGE_ETALON_RECORD, master);
        ctx.putToStorage(StorageId.DATA_MERGE_DUPLICATES, duplicates);

        return true;
    }

    private Date getDateForCalculateOrigins(Map<String, Date> recordCalcDateMap, String key){
        if(recordCalcDateMap != null && recordCalcDateMap.containsKey(key)){
            return recordCalcDateMap.get(key);
        }
        return null;
    }

}

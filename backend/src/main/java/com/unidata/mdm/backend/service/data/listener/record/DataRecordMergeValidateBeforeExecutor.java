/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.data.listener.record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.RecordIdentityContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.service.audit.AuditActions;
import com.unidata.mdm.backend.service.audit.AuditEventsWriter;
import com.unidata.mdm.backend.service.data.common.CommonRecordsComponent;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
     * Audit events writer.
     */
    @Autowired
    private AuditEventsWriter auditEventsWriter;

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
        // 1. Selection one of duplicates member, if master key was not given
        // otherwise take from the context
        List<String> notFoundEtalonKeys = new ArrayList<>();
        RecordKeys keys = findRecordKeys(ctx, notFoundEtalonKeys);

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
            if (dKeys == null) {
                notFoundEtalonKeys.add(dCtx.getEtalonKey());
                continue;
            }

            if (!dKeys.isEtalonActive() || dKeys.isPending()) {
                final String message = "Record with keys etalon id: [{}], origin id: [{}] has incorrect state for merge status: [{}], approvalState: [{}]";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, ExceptionId.EX_DATA_MERGE_VALIDATE_INCORRECT_RECORD_STATE,
                        dKeys.getEtalonKey().getId());
            }

            if (!dKeys.getEtalonKey().getId().equals(keys.getEtalonKey().getId())) {
                duplicatesKeys.add(dKeys);
            }
        }

        if (duplicatesKeys.isEmpty()) {
            final String message = "None of the duplicates were found for etalon id: [{}], origin id [{}], name [{}]";
            LOGGER.warn(message, ctx);
            throw new DataProcessingException(message, ExceptionId.EX_DATA_MERGE_DUPLICATES_NOT_FOUND_BY_SUPPLIED_KEYS,
                    keys.getEtalonKey().getId(),
                    keys.getOriginKey().getId(),
                    ctx.getEntityName());
        }

        if (!notFoundEtalonKeys.isEmpty()) {
            final String message = "Merge failed, some or all duplicates were not found.";
            LOGGER.warn(message, ctx);
            auditEventsWriter.writeUnsuccessfulEvent(AuditActions.DATA_MERGE, new DataProcessingException(message, ExceptionId.EX_DATA_MERGE_DUPLICATES_NOT_FOUND, notFoundEtalonKeys), ctx);
        }

        ctx.putToStorage(StorageId.DATA_MERGE_KEYS, keys);
        ctx.putToStorage(StorageId.DATA_MERGE_DUPLICATES_KEYS, duplicatesKeys);
        ctx.putToStorage(StorageId.DATA_MERGE_NOT_FOUND_ETALON_KEYS, notFoundEtalonKeys);

        Map<String, Date> recordCalcDateMap = ctx.getFromStorage(StorageId.DATA_MERGE_KEYS_FOR_DATES);

        // 3. Run before actions.
        if (ctx.isUpRecordsToContext()) {
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
        }
        return true;
    }

    private Date getDateForCalculateOrigins(Map<String, Date> recordCalcDateMap, String key) {
        if (recordCalcDateMap != null && recordCalcDateMap.containsKey(key)) {
            return recordCalcDateMap.get(key);
        }
        return null;
    }

    private RecordKeys findRecordKeys(MergeRequestContext ctx, List<String> notFoundEtalonKeys) {
        RecordKeys keys = null;

        if (ctx.isValidRecordKey()) {
            keys = commonRecordsComponent.identify(ctx);
        }

        //Select keys from duplicates if ctx.isValidRecordKey is true and keys not found
        if (keys == null) {
            for (ListIterator<RecordIdentityContext> it = ctx.getDuplicates().listIterator(ctx.getDuplicates().size()); it.hasPrevious(); ) {
                RecordIdentityContext c = it.previous();
                it.remove();
                keys = commonRecordsComponent.identify(c);
                if (keys != null) {
                    return keys;
                }
                notFoundEtalonKeys.add(c.getEtalonKey());
            }
        }

        return keys;
    }

}

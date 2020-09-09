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

import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_ETALON_INACTIVE;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_INVALID_KEYS;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_INVALID_ORIGIN_INPUT;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_NO_INPUT;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_NO_SOURCE_SYSTEM;
import static com.unidata.mdm.backend.common.exception.ExceptionId.EX_DATA_UPSERT_ORIGIN_INACTIVE;

import java.util.Collections;

import com.unidata.mdm.backend.common.dto.data.model.GetEntityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.data.ModificationBox;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.data.ValidationServiceExt;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;

/**
 * @author Mikhail Mikhailov
 */
public class DataRecordUpsertValidateBeforeExecutor
        implements DataRecordBeforeExecutor<UpsertRequestContext> {

    /**
     * Logger for this bean.
     */
    private static final Logger LOGGER
            = LoggerFactory.getLogger(DataRecordUpsertValidateBeforeExecutor.class);
    /**
     * Meta model service instance.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    @Autowired
    private ValidationServiceExt validationService;

    /**
     * Constructor.
     */
    public DataRecordUpsertValidateBeforeExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            // 1. Check input (presence of records themselves)
            if (!ctx.isEtalon() && !ctx.isOrigin()) {
                final String message = "Invalid upsert request context. Either etalon data or origin data or keys invalid / missing. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, EX_DATA_UPSERT_NO_INPUT, ctx);
            }

            RecordKeys keys = ctx.keys();

            // 2. Check supplied keys validity.
            if (keys == null && ((ctx.isOrigin() && ctx.isOriginRecordKey()) || ctx.isEtalonRecordKey())) {
                final String message = "Record can not be identified by supplied keys. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, EX_DATA_UPSERT_INVALID_KEYS);
            }

            // 3. Origin is inactive, discard updates
            if (keys != null && keys.getOriginStatus() == RecordStatus.INACTIVE) {
                final String message = "Origin [Ext. ID: {}, Source system: {}, Entity name: {}] is inactive. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, EX_DATA_UPSERT_ORIGIN_INACTIVE,
                        keys.getOriginKey().getExternalId(),
                        keys.getOriginKey().getSourceSystem(),
                        keys.getOriginKey().getEntityName());
            } else if (keys != null && keys.getEtalonStatus() == RecordStatus.INACTIVE) {
                final String message = "Etalon [ID: {}] is inactive. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, EX_DATA_UPSERT_ETALON_INACTIVE, keys.getEtalonKey().getId());
            }

            // 4. Check key combination validity
            if (ctx.isOrigin() && !ctx.isOriginExternalId() && !ctx.isOriginRecordKey()) {
                final String message = "Cannot upsert origin record. Neither valid external id nor origin record key has been supplied. Upsert rejected.";
                LOGGER.warn(message, ctx);
                throw new DataProcessingException(message, EX_DATA_UPSERT_INVALID_ORIGIN_INPUT);
            }

            // 5. Check source system.
            if (ctx.isOrigin() && ctx.isOriginExternalId() && metaModelService.getSourceSystemById(ctx.getSourceSystem()) == null) {
                String message = "Valid source system should be defined.";
                throw new DataProcessingException(message, EX_DATA_UPSERT_NO_SOURCE_SYSTEM);
            }

            // 6. Check entity name and  supplied data consistency
            DataRecord record = ctx.getRecord();
            String entityName = ctx.getEntityName() != null
                    ? ctx.getEntityName()
                    : keys != null
                    ? keys.getEntityName()
                    : null;

            if (metaModelService.isLookupEntity(entityName)) {
                validationService.checkLookupDataRecord(record, entityName);
            }
            else {
                validationService.checkEntityDataRecord(record, entityName);
            }

            // 7. Record is half-way valid. Put to box.
            OriginRecord origin = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
            ctx.putToStorage(StorageId.DATA_UPSERT_MODIFICATION_BOX, ModificationBox.of(Collections.emptyList(), origin));
            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }
}

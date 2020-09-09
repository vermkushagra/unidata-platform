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

package com.unidata.mdm.backend.service.integration;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.context.GetRequestContext;
import com.unidata.mdm.backend.common.context.GetRequestContext.GetRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.integration.exits.DataService;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;


/**
 * @author Mikhail Mikhailov
 * Simple data operations for integration support.
 */
@Service
public class IntegrationServiceImpl implements DataService {

    /**
     * Data service.
     */
    @Autowired
    private DataRecordsService svc;


    /**
     * Constructor.
     */
    public IntegrationServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonRecord findEtalonRecord(String etalonId, Date forDate) {

        GetRequestContext ctx = new GetRequestContextBuilder()
            .etalonKey(etalonId)
            .forDate(forDate)
            .build();

        GetRecordDTO result = svc.getRecord(ctx);
        return result == null ? null : result.getEtalon();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRecord findOriginRecord(String originId) {

        GetRequestContext ctx = new GetRequestContextBuilder()
            .originKey(originId)
            .build();

        GetRecordDTO result = svc.getRecord(ctx);
        return result == null
                ? null
                : result.getOrigins() != null && result.getOrigins().size() > 0
                    ? result.getOrigins().get(0)
                    : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OriginRecord findOriginRecord(String externalId, String entityName, String sourceSystem) {

        GetRequestContext ctx = new GetRequestContextBuilder()
            .externalId(externalId)
            .entityName(entityName)
            .sourceSystem(sourceSystem)
            .build();

        GetRecordDTO result = svc.getRecord(ctx);
        return result == null
                ? null
                : result.getOrigins() != null && result.getOrigins().size() > 0
                    ? result.getOrigins().get(0)
                    : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean upsertOriginRecord(OriginRecord record) {

        if (record == null) {
            return false;
        }
        // FIXME refactor by possibility
        /*
        OriginKey key = record.getOriginKey();
        UpsertRequestContext ctx = new UpsertRequestContextBuilder()
            .entityName(key == null ? record.getInfoSection().getEntityName() : key.getEntityName())
            .sourceSystem(key == null ? null : key.getSourceSystem())
            .originRecord(record)
            .validFrom(JaxbUtils.xmlGregorianCalendarToDate(record.getInfoSection().getRangeFrom()))
            .validTo(JaxbUtils.xmlGregorianCalendarToDate(record.getInfoSection().getRangeTo()))
            .build();

        UpsertRecordDTO result = svc.upsertRecord(ctx);
        return result == null
                ? false
                : (result.getAction() == UpsertAction.INSERT || result.getAction() == UpsertAction.UPDATE) && result.isOrigin();
                */
        return false;
    }
}

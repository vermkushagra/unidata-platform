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

package com.unidata.mdm.backend.dao.impl.audit;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import javax.sql.DataSource;

import com.unidata.mdm.backend.dao.AuditDao;
import com.unidata.mdm.backend.dao.impl.AbstractDaoImpl;
import com.unidata.mdm.backend.po.audit.AuditPO;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
public class AuditDatabaseStorageDaoImpl extends AbstractDaoImpl implements AuditDao {

    /**
     * Insert audit events
     */
    private final String insertAuditEvent;
    private final String deleteOldAuditEvents;


    public AuditDatabaseStorageDaoImpl(DataSource dataSource, Properties sql) {
        super(dataSource);
        insertAuditEvent = sql.getProperty("insertAuditEvent");
        deleteOldAuditEvents = sql.getProperty("deleteOldAuditEvents");
    }

    @Override
    public boolean insert(AuditPO auditEvent){
        return  jdbcTemplate.update(insertAuditEvent,
                auditEvent.getCreateDate(),
                auditEvent.getCreatedBy(),
                auditEvent.getOperationId(),
                auditEvent.getDetails().replaceAll("\u0000", ""),
                auditEvent.getAction()) == 1;
    }

    @Override
    public long deleteOldAuditEvents(long maxLifetime) {
        Timestamp dateForDelete = new Timestamp(Instant.now().minus(maxLifetime, ChronoUnit.MINUTES).toEpochMilli());
        return jdbcTemplate.update(deleteOldAuditEvents, dateForDelete);
    }
}

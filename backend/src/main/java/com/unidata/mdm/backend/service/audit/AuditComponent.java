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

package com.unidata.mdm.backend.service.audit;

import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.dao.AuditDao;
import com.unidata.mdm.backend.po.audit.AuditPO;
import com.unidata.mdm.backend.service.search.Event;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author Dmitry Kopin on 11.04.2017.
 */
@Component
public class AuditComponent {

    @Autowired
    private AuditDao auditDao;

    public boolean saveAuditEvent(Event event){
        AuditPO auditPO = new AuditPO();

        auditPO.setCreatedBy(ObjectUtils.toString(event.get(Event.USER), null));
        if(event.get(Event.DATE) != null){
            TemporalAccessor date = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(ObjectUtils.toString(event.get(Event.DATE), null));
            auditPO.setCreateDate(ConvertUtils.localDateTime2Date(LocalDateTime.from(date)));
        }

        auditPO.setDetails(event.toString());
        auditPO.setOperationId(ObjectUtils.toString(event.get(Event.OPERATION_ID), null));
        auditPO.setAction(ObjectUtils.toString(event.get(Event.ACTION), null));

        return auditDao.insert(auditPO);
    }
}

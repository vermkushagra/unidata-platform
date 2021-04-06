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

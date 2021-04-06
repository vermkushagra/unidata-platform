package com.unidata.mdm.backend.dao.impl.audit;

import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.dao.AuditDao;
import com.unidata.mdm.backend.dao.impl.AbstractDaoImpl;
import com.unidata.mdm.backend.po.audit.AuditPO;
import com.unidata.mdm.backend.service.search.Event;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

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

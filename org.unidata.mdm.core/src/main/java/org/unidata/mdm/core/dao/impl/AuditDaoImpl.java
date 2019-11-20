package org.unidata.mdm.core.dao.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.unidata.mdm.core.dao.AuditDao;
import org.unidata.mdm.core.dto.EnhancedAuditEvent;
import org.unidata.mdm.core.util.Maps;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Alexander Malyshev
 */
@Repository
public class AuditDaoImpl extends AbstractDaoImpl implements AuditDao {

    private final String insertEnhancedAuditEvent;

    public AuditDaoImpl(
            @Qualifier("coreDataSource") final DataSource coreDataSource,
            @Qualifier("audit-sql") final Properties sql
    ) {
        super(coreDataSource);
        insertEnhancedAuditEvent = sql.getProperty("INSERT_AUDIT_EVENT");
    }

    @Override
    public boolean insert(EnhancedAuditEvent enhancedAuditEvent) {
        return namedJdbcTemplate.update(
                insertEnhancedAuditEvent,
                Maps.of(
                        EnhancedAuditEvent.TYPE_FIELD, enhancedAuditEvent.type(),
                        EnhancedAuditEvent.PARAMETERS_FIELD, enhancedAuditEvent.parameters(),
                        EnhancedAuditEvent.SUCCESS_FIELD, enhancedAuditEvent.success(),
                        EnhancedAuditEvent.USER_FIELD, enhancedAuditEvent.getUser(),
                        EnhancedAuditEvent.CLIENT_IP_FIELD, enhancedAuditEvent.getClientIp(),
                        EnhancedAuditEvent.SERVER_IP_FIELD, enhancedAuditEvent.getServerIp(),
                        EnhancedAuditEvent.WHEN_FIELD, enhancedAuditEvent.getWhen()
                )
        ) > 0;
    }

    @Override
    public long deleteOldAuditEvents(long maxLifetime) {
        return 0;
    }
}

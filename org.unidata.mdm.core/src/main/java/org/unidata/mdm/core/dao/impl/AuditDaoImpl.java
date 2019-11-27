package org.unidata.mdm.core.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.unidata.mdm.core.dao.AuditDao;
import org.unidata.mdm.core.dto.EnrichedAuditEvent;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.util.Maps;
import org.unidata.mdm.system.dao.impl.BaseDAOImpl;
import org.unidata.mdm.system.exception.PlatformFailureException;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Alexander Malyshev
 */
@Repository
public class AuditDaoImpl extends BaseDAOImpl implements AuditDao {

    private final ObjectMapper objectMapper;

    private final String insertEnhancedAuditEvent;

    public AuditDaoImpl(
            final ObjectMapper objectMapper,
            @Qualifier("coreDataSource") final DataSource coreDataSource,
            @Qualifier("audit-sql") final Properties sql
    ) {
        super(coreDataSource);
        this.objectMapper = objectMapper;
        insertEnhancedAuditEvent = sql.getProperty("INSERT_AUDIT_EVENT");
    }

    @Override
    public boolean insert(EnrichedAuditEvent enhancedAuditEvent) {
        try {
            final String parameters = objectMapper.writeValueAsString(enhancedAuditEvent.parameters());
            return namedJdbcTemplate.update(
                    insertEnhancedAuditEvent,
                    Maps.of(
                            EnrichedAuditEvent.TYPE_FIELD, enhancedAuditEvent.type(),
                            EnrichedAuditEvent.PARAMETERS_FIELD, parameters,
                            EnrichedAuditEvent.SUCCESS_FIELD, enhancedAuditEvent.success(),
                            EnrichedAuditEvent.LOGIN_FIELD, enhancedAuditEvent.getLogin(),
                            EnrichedAuditEvent.CLIENT_IP_FIELD, enhancedAuditEvent.getClientIp(),
                            EnrichedAuditEvent.SERVER_IP_FIELD, enhancedAuditEvent.getServerIp(),
                            EnrichedAuditEvent.ENDPOINT_FIELD, enhancedAuditEvent.getEndpoint(),
                            EnrichedAuditEvent.WHEN_HAPPENED_FIELD, enhancedAuditEvent.getWhenwHappened()
                    )
            ) > 0;
        } catch (JsonProcessingException e) {
            throw new PlatformFailureException(
                    "Can't serialize audit event parameters " + enhancedAuditEvent.parameters(),
                    e,
                    CoreExceptionIds.EX_AUDIT_EVENT_JSON_SERIALIZATION_EXCEPTION,
                    enhancedAuditEvent.parameters()
            );
        }
    }

    @Override
    public long deleteOldAuditEvents(long maxLifetime) {
        return 0;
    }
}

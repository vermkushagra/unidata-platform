package org.unidata.mdm.core.service.impl;

import io.prometheus.client.Counter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.audit.AuditConstants;
import org.unidata.mdm.core.configuration.CoreConfigurationProperty;
import org.unidata.mdm.core.context.AuditEventWriteContext;
import org.unidata.mdm.core.dto.EnrichedAuditEvent;
import org.unidata.mdm.core.service.AuditEventBuildersRegistryService;
import org.unidata.mdm.core.service.AuditService;
import org.unidata.mdm.core.service.AuditServiceStorageService;
import org.unidata.mdm.core.type.audit.AuditEvent;
import org.unidata.mdm.core.type.monitoring.collector.QueueSizeCollector;
import org.unidata.mdm.core.type.security.SecurityToken;
import org.unidata.mdm.core.util.SecurityUtils;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Alexander Malyshev
 */
@Service
public class AuditServiceImpl implements AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

    private static final String AUDIT_EVENTS_TASKS_QUEUE_SIZE_METRIC_NAME = "unidata_audit_write_queue_size";
    private static final String AUDIT_EVENTS_TASKS_QUEUE_SIZE_METRIC_HELP_TEXT = "Size of audit event write queue";

    private static final String AUDIT_EVENTS_FOR_WRITE_TOTAL_METRIC_NAME = "udidata_audit_events_for_write_total";
    private static final String AUDIT_EVENTS_ARE_SEND_TO_WRITE_HELP_TEXT = "Audit events are send to write";

    private static final Counter AUDIT_EVENTS_FOR_WRITE = Counter.build()
            .name(AUDIT_EVENTS_FOR_WRITE_TOTAL_METRIC_NAME)
            .help(AUDIT_EVENTS_ARE_SEND_TO_WRITE_HELP_TEXT)
            .create()
            .register();

    private static final String HANDLED_AUDIT_EVENTS_METRIC_NAME = "unidata_handled_audit_events_total";
    private static final String HANDLED_AUDIT_EVENTS_HELP_TEXT = "Handled audit events";
    private static final Counter HANDLED_AUDIT_EVENTS = Counter.build()
            .name(HANDLED_AUDIT_EVENTS_METRIC_NAME)
            .help(HANDLED_AUDIT_EVENTS_HELP_TEXT)
            .create()
            .register();
    private static final String UNKNOWN = "unknown";

    // TODO Use dynamic configuration update
    private boolean auditEnabled;
    private int poolSize;

    /**
     * Audit Write Executor
     */
    private final ExecutorService executorService;

    private final AuditEventBuildersRegistryService auditEventBuildersRegistryService;

    private final Map<String, AuditServiceStorageService> auditServiceStorageServices = new HashMap<>();

    private final Set<String> enabledStorages = new CopyOnWriteArraySet<>();

    public AuditServiceImpl(
            final AuditEventBuildersRegistryService auditEventBuildersRegistryService,
            final List<AuditServiceStorageService> auditServiceStorageServices,
            @Value("${" + CoreConfigurationProperty.Constants.UNIDATA_AUDIT_ENABLED_STORAGES_KEY + "}") final String enabledStorages,
            @Value("${" + CoreConfigurationProperty.Constants.UNIDATA_AUDIT_ENABLED_KEY + ":false}") final boolean auditEnabled,
            @Value("${" + CoreConfigurationProperty.Constants.UNIDATA_AUDIT_WRITER_POOL_SIZE_KEY + ":5}") final int poolSize
    ) {
        this.auditEventBuildersRegistryService = auditEventBuildersRegistryService;
        if (CollectionUtils.isNotEmpty(auditServiceStorageServices)) {
            this.auditServiceStorageServices.putAll(
                    auditServiceStorageServices.stream()
                            .collect(Collectors.toMap(AuditServiceStorageService::id, Function.identity()))
            );
        }
        this.auditEnabled = auditEnabled;
        this.poolSize = poolSize;
        if (StringUtils.isNoneBlank(enabledStorages)) {
            this.enabledStorages.addAll(Arrays.asList(enabledStorages.split(",")));
        }
        executorService = initWriterThreadPool();
    }

    private ExecutorService initWriterThreadPool() {
        final LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        QueueSizeCollector.createAndRegister(
                workQueue,
                AUDIT_EVENTS_TASKS_QUEUE_SIZE_METRIC_NAME,
                AUDIT_EVENTS_TASKS_QUEUE_SIZE_METRIC_HELP_TEXT
        );
        return new ThreadPoolExecutor(
                1,
                poolSize,
                0L,
                TimeUnit.MILLISECONDS,
                workQueue,
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setDaemon(false);
                    thread.setName("AuditEventWriter");
                    return thread;
                }
        );
    }

    @Override
    public void writeEvent(String eventType) {
        writeEvent(eventType, Collections.emptyMap());
    }

    @Override
    public void writeEvent(final String eventType, Map<String, Object> parameters) {
        if (!auditEnabled) {
            return;
        }
        final EnrichedAuditEvent enrichedAuditEvent = enrich(
                auditEventBuildersRegistryService.eventBuilder(eventType).apply(eventType, parameters)
        );
        executorService.submit(() -> write(enrichedAuditEvent, SecurityUtils.getCurrentUserStorageId()));
        AUDIT_EVENTS_FOR_WRITE.inc();
    }

    private EnrichedAuditEvent enrich(AuditEvent auditEvent) {
        final SecurityToken securityTokenForCurrentUser = SecurityUtils.getSecurityTokenForCurrentUser();
        return new EnrichedAuditEvent(
                auditEvent,
                login(securityTokenForCurrentUser, auditEvent.parameters()),
                clientIp(securityTokenForCurrentUser, auditEvent.parameters()),
                serverIp(securityTokenForCurrentUser, auditEvent.parameters()),
                endpoint(securityTokenForCurrentUser, auditEvent.parameters()),
                LocalDateTime.now()
        );
    }

    private String login(SecurityToken securityTokenForCurrentUser, Map<String, String> parameters) {
        return securityTokenForCurrentUser != null ?
                securityTokenForCurrentUser.getUser().getLogin() :
                parameters.getOrDefault("login", SecurityUtils.getCurrentUserName());
    }

    private String clientIp(SecurityToken securityTokenForCurrentUser, Map<String, String> parameters) {
        if (securityTokenForCurrentUser != null && securityTokenForCurrentUser.getUserIp() != null) {
            return securityTokenForCurrentUser.getUserIp();
        }
        return parameters.getOrDefault(AuditConstants.CLIENT_IP_FIELD, UNKNOWN);
    }

    private String serverIp(SecurityToken securityTokenForCurrentUser, Map<String, String> parameters) {
        if (securityTokenForCurrentUser != null && securityTokenForCurrentUser.getServerIp() != null) {
            return securityTokenForCurrentUser.getServerIp();
        }
        return parameters.getOrDefault(AuditConstants.SERVER_IP_FIELD, UNKNOWN);
    }

    private String endpoint(SecurityToken securityTokenForCurrentUser, Map<String, String> parameters) {
        if (securityTokenForCurrentUser != null && securityTokenForCurrentUser.getEndpoint() != null) {
            return securityTokenForCurrentUser.getEndpoint().name();
        }
        return parameters.getOrDefault(AuditConstants.ENDPOINT_FIELD, UNKNOWN);
    }

    private void write(EnrichedAuditEvent enrichedAuditEvent, String currentUserStorageId) {
        final AuditEventWriteContext context = AuditEventWriteContext.builder()
                .enhancedAuditEvent(enrichedAuditEvent)
                .currentUserStorageId(currentUserStorageId)
                .build();
        for (String storageId : enabledStorages) {
            try {
                if (auditServiceStorageServices.containsKey(storageId)) {
                    auditServiceStorageServices.get(storageId).write(context);
                }
            }
            catch (Exception e) {
                logger.error("Error while write audit by " + storageId, e);
            }
        }
        HANDLED_AUDIT_EVENTS.inc();
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }
}

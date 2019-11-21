package org.unidata.mdm.core.service.impl;

import io.prometheus.client.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.configuration.CoreConfigurationProperty;
import org.unidata.mdm.core.dao.AuditDao;
import org.unidata.mdm.core.dto.EnhancedAuditEvent;
import org.unidata.mdm.core.service.AuditService;
import org.unidata.mdm.core.type.audit.AuditEvent;
import org.unidata.mdm.core.type.monitoring.collector.QueueSizeCollector;
import org.unidata.mdm.core.type.search.AuditIndexType;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.indexing.Indexing;
import org.unidata.mdm.search.type.indexing.IndexingField;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    private static final String FIELD_VALUE_DELIMITER = "=>>";

    // TODO Use dynamic configuration update
    private boolean auditEnabled;
    private int poolSize;

    /**
     * Audit Write Executor
     */
    private final ExecutorService executorService;

    private final SearchService searchService;

    private final AuditDao auditDao;

    public AuditServiceImpl(
            final SearchService searchService,
            final AuditDao auditDao,
            @Value("${" + CoreConfigurationProperty.Constants.UNIDATA_AUDIT_ENABLED_KEY + ":false}") final boolean auditEnabled,
            @Value("${" + CoreConfigurationProperty.Constants.UNIDATA_AUDIT_WRITER_POOL_SIZE_KEY + ":5}") final int poolSize
    ) {
        this.searchService = searchService;
        this.auditDao = auditDao;
        this.auditEnabled = auditEnabled;
        this.poolSize = poolSize;
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
    public void writeEvent(AuditEvent auditEvent) {
        if (!auditEnabled) {
            return;
        }
        final EnhancedAuditEvent enhancedAuditEvent = enhance(auditEvent);
        executorService.submit(() -> write(enhancedAuditEvent, SecurityUtils.getCurrentUserStorageId()));
        AUDIT_EVENTS_FOR_WRITE.inc();
    }

    private EnhancedAuditEvent enhance(AuditEvent auditEvent) {
        return new EnhancedAuditEvent(
                auditEvent,
                SecurityUtils.getCurrentUserName(),
                "",
                "",
                LocalDateTime.now()
        );
    }

    private void write(EnhancedAuditEvent enhancedAuditEvent, String currentUserStorageId) {
        try {
            auditDao.insert(enhancedAuditEvent);

            List<IndexingField> fields = new ArrayList<>();
            fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.TYPE_FIELD, enhancedAuditEvent.type()));
            fields.add(IndexingField.ofStrings(
                    AuditIndexType.AUDIT,
                    EnhancedAuditEvent.PARAMETERS_FIELD,
                    toIndexStrings(enhancedAuditEvent.parameters())
            ));
            fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.SUCCESS_FIELD, enhancedAuditEvent.success()));
            fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.LOGIN_FIELD, enhancedAuditEvent.getLogin()));
            fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.CLIENT_IP_FIELD, enhancedAuditEvent.getClientIp()));
            fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.SERVER_IP_FIELD, enhancedAuditEvent.getServerIp()));
            fields.add(IndexingField.of(AuditIndexType.AUDIT, EnhancedAuditEvent.WHEN_HAPPENED_FIELD, enhancedAuditEvent.getWhenwHappened()));
            final IndexRequestContext indexRequestContext = IndexRequestContext.builder()
                    .storageId(currentUserStorageId)
                    .entity(AuditIndexType.INDEX_NAME)
                    .index(new Indexing(AuditIndexType.AUDIT, null).withFields(fields))
                    .build();
            searchService.process(indexRequestContext);
            HANDLED_AUDIT_EVENTS.inc();
        }
        catch (Exception e) {
            logger.error("Error while write audit", e);
        }
    }

    private Collection<String> toIndexStrings(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(e -> String.format("%s%s%s", e.getKey(), FIELD_VALUE_DELIMITER, e.getValue()))
                .collect(Collectors.toList());
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }
}

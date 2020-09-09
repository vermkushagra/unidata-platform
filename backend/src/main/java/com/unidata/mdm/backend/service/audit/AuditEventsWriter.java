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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.audit.actions.AuditAction;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.search.Event;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.util.MessageUtils;

import io.prometheus.client.Collector;
import io.prometheus.client.Counter;
import io.prometheus.client.GaugeMetricFamily;

@Component
public class AuditEventsWriter implements AfterContextRefresh {

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

    private static class AuditEventsInQueueCollector extends Collector {

        private static final String METRIC_NAME = "unidata_audit_write_queue_size";
        private static final String METRIC_HELP_TEXT = "Size of audit event write queue";

        private final Queue<?> queue;

        AuditEventsInQueueCollector(final Queue<?> queue) {
            this.queue = queue;
        }

        @Override
        public List<MetricFamilySamples> collect() {
            return Collections.singletonList(new GaugeMetricFamily(METRIC_NAME, METRIC_HELP_TEXT, queue.size()));
        }
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditEventsWriter.class);


    /**
     * Audit Executor
     */
    private final ExecutorService EXECUTOR_SERVICE;

    //can be replaced to camel for working with events in other project
    //@Produce(uri = AuditRoute.Constants.AUDIT_AUTH_QUEUE)
    //private ProducerTemplate authenticationHistory;
    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchService;

    @Autowired
    private AuditComponent auditComponent;

    /**
     * Property define should read events be audited or not
     */
    @Value("${" + ConfigurationConstants.UNIDATA_AUDIT_READ_DISABLED + ":true}")
    private Boolean readEvents;

    @Value("${" + ConfigurationConstants.UNIDATA_AUDIT_DISABLED + ":false}")
    private boolean auditDisabled;
    /**
     * How many frames will be logged
     */
    @Value("${" + ConfigurationConstants.UNIDATA_AUDIT_STACK_TRACE_DEPTH + ":5}")
    private long stacktraceDepth = 5;

    @Autowired
    public AuditEventsWriter(@Value("${audit.writer.pool.size:5}") Integer poolSize) {
//        EXECUTOR_SERVICE = Executors.newFixedThreadPool(poolSize, runnable -> {
//            Thread thread = new Thread(runnable);
//            thread.setDaemon(false);
//            thread.setName("AuditEventWriter");
//            return thread;
//        });
        final LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        new AuditEventsInQueueCollector(workQueue).register();
        EXECUTOR_SERVICE = new ThreadPoolExecutor(
                poolSize,
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

    //event creation also can be async
    public void writeSuccessEvent(@Nonnull AuditAction auditAction, Object... input) {

        if (auditDisabled ||
            (!readEvents && AuditActions.DATA_GET.name().equals(auditAction.name()))) {
            return;
        }

        Authentication auth = SecurityContextHolder.getContext() != null ?
                SecurityContextHolder.getContext().getAuthentication() :
                null;
        EXECUTOR_SERVICE.execute(() -> writeEvent(auditAction, auth, null, input));
        AUDIT_EVENTS_FOR_WRITE.inc();
    }

    public void writeUnsuccessfulEvent(@Nonnull AuditAction auditAction, @Nonnull Exception exception,
            Object... input) {

        if (auditDisabled) {
            return;
        }

        Authentication auth = SecurityContextHolder.getContext() != null ?
                SecurityContextHolder.getContext().getAuthentication() :
                null;
        EXECUTOR_SERVICE.execute(() -> writeEvent(auditAction, auth, exception, input));
        AUDIT_EVENTS_FOR_WRITE.inc();
    }

    private void writeEvent(@Nonnull AuditAction auditAction, @Nullable Authentication auth,
            @Nullable Exception exception, Object... input) {
        try {
            if (!auditAction.isValidInput(input)) {
                LOGGER.error("Input for audit action [{}] is not valid", auditAction.name());
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
            String errorMessage = getErrorMessage(exception);
            final Event event = new Event(auditAction.getSubsystem().name(), auditAction.name(), errorMessage);
            auditAction.enrichEvent(event, input);
            LOGGER.info("Audit event : {}", event);

            auditComponent.saveAuditEvent(event);

            searchService.indexAuditEvent(null, event);
        } catch (Exception e) {
            LOGGER.error("Error happened during an audit event creation. Action [{}]. Exception [{}]",
                    auditAction.name(), e);
        }
        finally {
            HANDLED_AUDIT_EVENTS.inc();
        }
    }

    @Nullable
    private String getErrorMessage(@Nullable Exception exception) {
        if (exception == null) {
            return null;
        }
        if (exception instanceof SystemRuntimeException) {
            SystemRuntimeException systemRuntimeException = (SystemRuntimeException) exception;
            return MessageUtils.getMessage(systemRuntimeException.getId().getCode(), systemRuntimeException.getArgs());
        } else {
            String cause = StringUtils.isBlank(exception.getMessage()) ? EMPTY : exception.getMessage();
            cause += "[";
            String[] trace = ExceptionUtils.getRootCauseStackTrace(exception);
            return Arrays.stream(trace).limit(stacktraceDepth).collect(Collectors.joining(SPACE, cause, "]"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterContextRefresh() {
        searchService.createAuditIndex(null, false);
    }

    @PreDestroy
    public void shutdown() {
        EXECUTOR_SERVICE.shutdown();
    }


}

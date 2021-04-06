package com.unidata.mdm.backend.service.audit;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.audit.actions.AuditAction;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.search.Event;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.util.MessageUtils;

@Component
public class AuditEventsWriter implements AfterContextRefresh {
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
     * //todo replace to config file, which will be configure all event output.
     */
    @Value("${audit.read.events:true}")
    private Boolean readEvents;
    /**
     * How many frames will be logged
     */
    @Value("${audit.error.stacktrace.depth:5}")
    private long stacktraceDepth = 5;

    @Autowired
    public AuditEventsWriter(@Value("${audit.writer.pool.size:5}") Integer poolSize) {
        EXECUTOR_SERVICE = Executors.newFixedThreadPool(poolSize, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(false);
            thread.setName("AuditEventWriter");
            return thread;
        });
    }

    //event creation also can be async
    public void writeSuccessEvent(@Nonnull AuditAction auditAction, Object... input) {
        if (!readEvents && AuditActions.DATA_GET.name().equals(auditAction.name())) {
            return;
        }
        Authentication auth = SecurityContextHolder.getContext() != null ?
                SecurityContextHolder.getContext().getAuthentication() :
                null;
        EXECUTOR_SERVICE.execute(() -> writeEvent(auditAction, auth, null, input));
    }

    public void writeUnsuccessfulEvent(@Nonnull AuditAction auditAction, @Nonnull Exception exception,
            Object... input) {
        Authentication auth = SecurityContextHolder.getContext() != null ?
                SecurityContextHolder.getContext().getAuthentication() :
                null;
        EXECUTOR_SERVICE.execute(() -> writeEvent(auditAction, auth, exception, input));
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

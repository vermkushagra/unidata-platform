package org.unidata.mdm.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.dto.ImmutableAuditEvent;
import org.unidata.mdm.core.service.AuditEventBuildersRegistryService;
import org.unidata.mdm.core.type.audit.AuditEvent;
import org.unidata.mdm.core.audit.AuditConstants;
import org.unidata.mdm.core.audit.AuditUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author Alexander Malyshev
 */
@Service
public class AuditEventBuildersRegistryServiceImpl implements AuditEventBuildersRegistryService {

    private final Logger logger = LoggerFactory.getLogger(AuditEventBuildersRegistryServiceImpl.class);

    private final Map<String, BiFunction<String, Map<String, Object>, AuditEvent>> eventBuilders = new ConcurrentHashMap<>();

    @Override
    public BiFunction<String, Map<String, Object>, AuditEvent> eventBuilder(String eventType) {
        return eventBuilders.containsKey(eventType) ? eventBuilders.get(eventType) : this::defaultEventBuilder;
    }

    @Override
    public boolean eventBuilderRegistered(String eventType) {
        return eventBuilders.containsKey(eventType);
    }

    @Override
    public void registerEventBuilder(String eventType, BiFunction<String, Map<String, Object>, AuditEvent> builder) {
        logger.info("Register new event builder {} for type {}", builder, eventType);
        eventBuilders.put(eventType, builder);
    }

    @Override
    public BiFunction<String, Map<String, Object>, AuditEvent> defaultEventBuilder(String eventType) {
        return this::defaultEventBuilder;
    }

    private AuditEvent defaultEventBuilder(final String eventType, final Map<String, Object> map) {
        return new ImmutableAuditEvent(eventType, toEventParameters(map), isSuccess(map));
    }

    private Map<String, String> toEventParameters(Map<String, Object> parameters) {
        return parameters.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, this::valueToString));
    }

    private String valueToString(Map.Entry<String, Object> entry) {
        final Object value = entry.getValue();
        if (AuditConstants.EXCEPTION_FIELD.equals(entry.getKey()) && value instanceof Throwable) {
            return AuditUtils.toString((Throwable) value);
        }
        return value.toString();
    }

    private boolean isSuccess(Map<String, Object> parameters) {
        if (parameters.containsKey(AuditConstants.SUCCESS_FIELD) && parameters.get(AuditConstants.SUCCESS_FIELD) instanceof Boolean) {
            return (boolean) parameters.get(AuditConstants.SUCCESS_FIELD);
        }
        return !parameters.containsKey(AuditConstants.EXCEPTION_FIELD) && !parameters.containsKey(AuditConstants.ERROR_FIELD);
    }
}

package com.unidata.mdm.backend.common.exception;

import java.util.Map;
import java.util.Set;

public class ConsistencyException extends SystemRuntimeException {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -884715053710999194L;

    private final Map<String, Long> links;

    private final Set<String> entities;

    public ConsistencyException(String message, ExceptionId id, Map<String, Long> links) {
        super(message, id, links.values().stream().mapToLong(Long::longValue).sum());
        this.links = links;
        this.entities = null;
    }

    public ConsistencyException(String message, ExceptionId id, Set<String> entities) {
        super(message, id, entities.size());
        this.links = null;
        this.entities = entities;
    }

    public Map<String, Long> getLinks() {
        return links;
    }

    public Set<String> getEntities() {
        return entities;
    }
}

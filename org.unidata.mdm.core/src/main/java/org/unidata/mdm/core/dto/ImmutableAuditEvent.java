package org.unidata.mdm.core.dto;

import org.unidata.mdm.core.type.audit.AuditEvent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Malyshev
 */
public class ImmutableAuditEvent implements AuditEvent {

    private final String type;

    private final Map<String, String> parameters = new HashMap<>();

    private final boolean success;

    public ImmutableAuditEvent(
            final String type,
            final Map<String, String> parameters,
            final boolean success
    ) {
        this.type = Objects.requireNonNull(type);
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        this.success = success;
    }

    @Nonnull
    @Override
    public String type() {
        return type;
    }

    @Override
    public Map<String, String> parameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public boolean success() {
        return success;
    }
}

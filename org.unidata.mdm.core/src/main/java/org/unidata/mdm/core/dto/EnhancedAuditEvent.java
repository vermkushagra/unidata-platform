package org.unidata.mdm.core.dto;

import org.apache.commons.collections4.MapUtils;
import org.unidata.mdm.core.type.audit.AuditEvent;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Malyshev
 */
public class EnhancedAuditEvent implements AuditEvent {

    public static final String TYPE_FIELD = "type";
    public static final String PARAMETERS_FIELD = "parameters";
    public static final String SUCCESS_FIELD = "success";
    public static final String LOGIN_FIELD = "login";
    public static final String CLIENT_IP_FIELD = "client_ip";
    public static final String SERVER_IP_FIELD = "server_ip";
    public static final String WHEN_HAPPENED_FIELD = "when_happened";

    private final String type;

    private final Map<String, String> parameters = new HashMap<>();

    private final boolean success;

    private final String login;

    private final String clientIp;

    private final String serverIp;

    private final LocalDateTime whenwHappened;

    public EnhancedAuditEvent(
            final String type,
            final Map<String, String> parameters,
            final boolean success,
            final String login,
            final String clientIp,
            final String serverIp,
            final LocalDateTime whenHappened
    ) {
        this.type = Objects.requireNonNull(type);
        if (MapUtils.isNotEmpty(parameters)) {
            this.parameters.putAll(parameters);
        }
        this.success = success;
        this.login = login;
        this.clientIp = clientIp;
        this.serverIp = serverIp;
        this.whenwHappened = whenHappened;
    }

    public EnhancedAuditEvent(
            final AuditEvent auditEvent,
            final String login,
            final String clientIp,
            final String serverIp,
            final LocalDateTime whenwHappened
    ) {
        this(auditEvent.type(), auditEvent.parameters(), auditEvent.success(), login, clientIp, serverIp, whenwHappened);
    }

    @Nonnull
    @Override
    public String type() {
        return type;
    }

    @Nonnull
    @Override
    public Map<String, String> parameters() {
        return parameters;
    }

    @Override
    public boolean success() {
        return success;
    }

    public String getLogin() {
        return login;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public LocalDateTime getWhenwHappened() {
        return whenwHappened;
    }
}

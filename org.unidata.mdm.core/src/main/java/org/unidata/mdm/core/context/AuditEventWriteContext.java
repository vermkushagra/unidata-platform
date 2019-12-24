package org.unidata.mdm.core.context;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Malyshev
 */
public class AuditEventWriteContext {

    private final String currentUserStorageId;

    private final String type;

    private final boolean success;

    private final String userLogin;

    private final String clientIp;

    private final String serverIp;

    private final String endpoint;

    private final LocalDateTime whenHappened;

    private final Map<String, Object> parameters;


    public AuditEventWriteContext(final AuditEventWriteContextBuilder builder) {
        currentUserStorageId = builder.currentUserStorageId;
        type = builder.type;
        success = builder.success;
        userLogin = builder.userLogin;
        clientIp = builder.clientIp;
        serverIp = builder.serverIp;
        endpoint = builder.endpoint;
        whenHappened = builder.whenHappened;
        parameters = Collections.unmodifiableMap(builder.parameters);
    }

    public String getCurrentUserStorageId() {
        return currentUserStorageId;
    }

    public String getType() {
        return type;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public LocalDateTime getWhenHappened() {
        return whenHappened;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public static AuditEventWriteContextBuilder builder() {
        return new AuditEventWriteContextBuilder();
    }

    public static class AuditEventWriteContextBuilder {
        private String currentUserStorageId;

        private String type;

        private boolean success;

        private String userLogin;

        private String clientIp;

        private String serverIp;

        private String endpoint;

        private LocalDateTime whenHappened;

        private final Map<String, Object> parameters = new HashMap<>();

        public AuditEventWriteContextBuilder type(final String type) {
            this.type = type;
            return this;
        }

        public AuditEventWriteContextBuilder userLogin(final String userLogin) {
            this.userLogin = userLogin;
            return this;
        }

        public AuditEventWriteContextBuilder clientIp(final String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public AuditEventWriteContextBuilder serverIp(final String serverIp) {
            this.serverIp = serverIp;
            return this;
        }

        public AuditEventWriteContextBuilder endpoint(final String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public AuditEventWriteContextBuilder whenHappened(final LocalDateTime whenHappened) {
            this.whenHappened = whenHappened;
            return this;
        }


        public AuditEventWriteContextBuilder success(final boolean success) {
            this.success = success;
            return this;
        }

        public AuditEventWriteContextBuilder addParameters(final Map<String, Object> parameters) {
            if (parameters != null) {
                this.parameters.putAll(parameters);
            }
            return this;
        }

        public AuditEventWriteContext build() {
            return new AuditEventWriteContext(this);
        }
    }

    @Override
    public String toString() {
        return "AuditEventWriteContext { currentUserStorageId='" + currentUserStorageId
                + "', parameters='" + parameters + "' }";
    }
}

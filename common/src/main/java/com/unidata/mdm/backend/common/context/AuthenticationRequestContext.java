package com.unidata.mdm.backend.common.context;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.unidata.mdm.backend.common.integration.auth.AuthenticationSystemParameter;

/**
 * @author Mikhail Mikhailov
 * Auth context. Simple wrapper to allow proptrty string queries.
 */
public class AuthenticationRequestContext extends CommonRequestContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4766147798188522307L;
    /**
     * Authentication params.
     */
    private final Map<AuthenticationSystemParameter, Object> params;
    /**
     * Constructor.
     */
    private AuthenticationRequestContext(AuthenticationRequestContextBuilder b) {
        super();
        this.params = Objects.nonNull(b.params) ? b.params : Collections.emptyMap();
    }
    /**
     * @return the params
     */
    public Map<AuthenticationSystemParameter, Object> getParams() {
        return params;
    }
    /**
     * Builder.
     * @param params
     * @return
     */
    public static AuthenticationRequestContextBuilder builder(Map<AuthenticationSystemParameter, Object> params) {
        return new AuthenticationRequestContextBuilder()
                .params(params);
    }
    /**
     * Shortcut.
     * @param params
     * @return
     */
    public static AuthenticationRequestContext of(Map<AuthenticationSystemParameter, Object> params) {
        return new AuthenticationRequestContextBuilder()
                .params(params)
                .build();
    }
    /**
     * @author Mikhail Mikhailov
     * Builder.
     */
    public static class AuthenticationRequestContextBuilder {
        /**
         * Authentication params.
         */
        private Map<AuthenticationSystemParameter, Object> params;
        /**
         * Constructor.
         */
        private AuthenticationRequestContextBuilder() {
            super();
        }
        /**
         * Sets params.
         * @param params
         * @return self
         */
        public AuthenticationRequestContextBuilder params(Map<AuthenticationSystemParameter, Object> params) {
            this.params = params;
            return this;
        }
        /**
         * Build this.
         * @return context
         */
        public AuthenticationRequestContext build() {
            return new AuthenticationRequestContext(this);
        }
    }
}

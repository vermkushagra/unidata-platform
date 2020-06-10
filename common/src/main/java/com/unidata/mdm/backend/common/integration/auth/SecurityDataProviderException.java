package com.unidata.mdm.backend.common.integration.auth;

/**
 * Security exception occurs if something went wrong during authentication, authorization or profile fetching.
 *
 * @author Denis Kostovarov
 */
public class SecurityDataProviderException extends RuntimeException {
    static final long serialVersionUID = 1344547890545496749L;

    private SecurityState securityState;

    public SecurityDataProviderException(SecurityState securityState) {
        this.securityState = securityState;
    }

    public SecurityDataProviderException(SecurityState securityState, String message) {
        super(message);
        this.securityState = securityState;
    }

    public SecurityDataProviderException(SecurityState securityState, String message, Throwable cause) {
        super(message, cause);
        this.securityState = securityState;
    }

    public SecurityDataProviderException(SecurityState securityState, Throwable cause) {
        super(cause);
        this.securityState = securityState;
    }

    public SecurityDataProviderException(SecurityState securityState, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.securityState = securityState;
    }

    public SecurityState getSecurityState() {
        return securityState;
    }
}

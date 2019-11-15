package org.unidata.mdm.core.type.security.interceptor;

public class AuthRequestHandleResult implements RequestHandleResult {

    private static final AuthRequestHandleResult INSTANCE = new AuthRequestHandleResult();

    protected AuthRequestHandleResult() {
    }

    public static AuthRequestHandleResult get() {
        return INSTANCE;
    }
}

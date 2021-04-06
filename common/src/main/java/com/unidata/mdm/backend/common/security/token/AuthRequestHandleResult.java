package com.unidata.mdm.backend.common.security.token;

public class AuthRequestHandleResult implements RequestHandleResult {

    private static final AuthRequestHandleResult INSTANCE = new AuthRequestHandleResult();

    protected AuthRequestHandleResult() {
    }

    public static AuthRequestHandleResult get() {
        return INSTANCE;
    }
}

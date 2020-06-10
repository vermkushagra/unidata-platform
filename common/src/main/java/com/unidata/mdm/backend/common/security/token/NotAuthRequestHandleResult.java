package com.unidata.mdm.backend.common.security.token;

public final class NotAuthRequestHandleResult implements RequestHandleResult {
    private static final NotAuthRequestHandleResult INSTANCE = new NotAuthRequestHandleResult();
    private NotAuthRequestHandleResult() {}

    public static NotAuthRequestHandleResult get() {
        return INSTANCE;
    }
}

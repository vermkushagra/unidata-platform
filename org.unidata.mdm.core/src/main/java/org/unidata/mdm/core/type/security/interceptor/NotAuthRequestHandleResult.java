package org.unidata.mdm.core.type.security.interceptor;

public final class NotAuthRequestHandleResult implements RequestHandleResult {
    private static final NotAuthRequestHandleResult INSTANCE = new NotAuthRequestHandleResult();
    private NotAuthRequestHandleResult() {}

    public static NotAuthRequestHandleResult get() {
        return INSTANCE;
    }
}

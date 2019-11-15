package org.unidata.mdm.core.service;

import org.unidata.mdm.core.type.security.interceptor.NotAuthRequestHandleResult;
import org.unidata.mdm.core.type.security.interceptor.RequestHandleResult;

import javax.servlet.http.HttpServletRequest;

public interface SecurityInterceptionProvider {

    default RequestHandleResult handleRequest(final HttpServletRequest request) {
        return NotAuthRequestHandleResult.get();
    }
}

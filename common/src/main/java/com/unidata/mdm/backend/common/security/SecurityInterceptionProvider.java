package com.unidata.mdm.backend.common.security;

import javax.servlet.http.HttpServletRequest;

import com.unidata.mdm.backend.common.security.token.NotAuthRequestHandleResult;
import com.unidata.mdm.backend.common.security.token.RequestHandleResult;

public interface SecurityInterceptionProvider {

    default RequestHandleResult handleRequest(final HttpServletRequest request) {
        return NotAuthRequestHandleResult.get();
    }
}

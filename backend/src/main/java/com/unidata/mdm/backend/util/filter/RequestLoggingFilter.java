package com.unidata.mdm.backend.util.filter;

import com.unidata.mdm.backend.util.ClientIpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RequestLoggingFilter extends AbstractRequestLoggingFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    /**
     * Writes a log message before the request is processed.
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
            request.setAttribute(this.getClass() + "_BEGIN_TIME", System.currentTimeMillis());
        }
    }

    /**
     * Writes a log message after the request is processed.
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        if (log.isDebugEnabled()) {
            log.debug(message);
        }
    }

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);

        Object start = request.getAttribute(this.getClass() + "_BEGIN_TIME");
        if (start instanceof Long) {
            long timing = System.currentTimeMillis() - (Long)start;
            msg.append(timing).append(" ms ");
        }

        msg.append(request.getMethod()).append(' ');
        msg.append("uri=").append(request.getRequestURI());
        if (isIncludeQueryString()) {
            msg.append('?').append(request.getQueryString());
        }
        if (isIncludeClientInfo()) {
            String client = ClientIpUtil.clientIp(request);
            if (StringUtils.hasLength(client)) {
                msg.append(";client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append(";session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append(";user=").append(user);
            }
        }
                msg.append(suffix);
        return msg.toString();
    }
}

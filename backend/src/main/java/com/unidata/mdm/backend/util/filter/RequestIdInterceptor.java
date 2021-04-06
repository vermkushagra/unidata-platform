package com.unidata.mdm.backend.util.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.unidata.mdm.backend.MDCKeys;
import com.unidata.mdm.backend.api.rest.constants.Constants;
import com.unidata.mdm.backend.util.MDCUtils;

/**
 * @author Michael Yashin. Created on 18.02.15.
 */
@Component
public class RequestIdInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = request.getParameter(MDCKeys.REQUEST_ID);
        if (StringUtils.isEmpty(requestId)) {
            requestId = request.getHeader(Constants.HTTP_HEADER_REQUEST_UUID);
        }
        if (StringUtils.isEmpty(requestId)) {
            requestId = MDCUtils.generateRequestUuid();
        }
        response.setHeader(Constants.HTTP_HEADER_REQUEST_UUID, requestId);
        MDCUtils.setCommonMdcInfo(requestId, request.getRemoteUser());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDCUtils.removeCommonMdcInfo();
    }
}
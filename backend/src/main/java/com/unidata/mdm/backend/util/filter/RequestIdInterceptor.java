/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
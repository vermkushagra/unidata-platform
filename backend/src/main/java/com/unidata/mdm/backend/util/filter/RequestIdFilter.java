
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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.unidata.mdm.backend.MDCKeys;
import com.unidata.mdm.backend.api.rest.constants.Constants;
import com.unidata.mdm.backend.util.MDCUtils;

public class RequestIdFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

        //TODO: support Servlet 3.0 async calls

        String requestId = request.getParameter(MDCKeys.REQUEST_ID);
        if (StringUtils.isEmpty(requestId)) {
            requestId = request.getHeader(Constants.HTTP_HEADER_REQUEST_UUID);
        }
        if (StringUtils.isEmpty(requestId)) {
            requestId = MDCUtils.generateRequestUuid();
        }
        response.setHeader(Constants.HTTP_HEADER_REQUEST_UUID, requestId);
        MDCUtils.setCommonMdcInfo(requestId, request.getRemoteUser());

		filterChain.doFilter(request, response);

        MDCUtils.removeCommonMdcInfo();
	}

}


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

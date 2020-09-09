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

package com.unidata.mdm.backend.api.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.unidata.mdm.backend.util.ClientIpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidata.mdm.backend.common.configuration.BeanNameConstants;

/*
 http://cxf.apache.org/docs/jax-rs-data-bindings.html#JAX-RSDataBindings-JSONsupport
 http://cxf.apache.org/docs/jax-rs-basics.html
 */

@Produces({ "application/json" })
@Consumes({ "application/json" })
// , "application/x-www-form-urlencoded"
public abstract class AbstractRestService {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRestService.class);

    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    /**
     * Default object mapper.
     */
    @Autowired
    @Qualifier(BeanNameConstants.DEFAULT_OBJECT_MAPPER_BEAN_NAME)
    protected ObjectMapper MAPPER;
    /** HTTP servlet request. */
    @Context
    private HttpServletRequest hsr;
    /**
     * Allow options requests.
     */
    public static final String OPTIONS_REQUEST = "OPTIONS";

    protected Response ok(Object entity) {
        return Response.ok(entity).allow(OPTIONS_REQUEST).build();
    }

    protected Response okOrNotFound(Object entity) {
        if (entity == null) {
            return notFound();
        }
        return ok(entity);
    }

    protected Response accepted() {
        return Response.accepted().build();
    }

    protected Response notAuthorized(Object entity) {
        return Response.status(Response.Status.UNAUTHORIZED).entity(entity).build();
    }

    protected Response notFound() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    protected HttpServletRequest getHSR() {
        return this.hsr;
    }

    protected String getClientIp() {
        return ClientIpUtil.clientIp(getHSR());
    }

    protected String getServerIp() {
        return  getHSR() == null ?  null : getHSR().getLocalAddr();
    }

    /**
     * Local (in-place) unrest of a content string.
     * 
     * @param content
     *            the string
     * @param cls
     *            class to map the content to
     * @return new instance
     */
    public <T> T unrestInplace(String content, Class<T> cls) {
        try {
            if (!StringUtils.isBlank(content)) {
                return MAPPER.readValue(content, cls);
            }
        } catch (JsonParseException e) {
            LOGGER.warn("Caught a 'JsonParseException' while local unrest. {}", e);
        } catch (JsonMappingException e) {
            LOGGER.warn("Caught a 'JsonMappingException' while local unrest. {}", e);
        } catch (IOException e) {
            LOGGER.warn("Caught a 'IOException' while doing unrest. {}", e);
        }

        return null;
    }

}

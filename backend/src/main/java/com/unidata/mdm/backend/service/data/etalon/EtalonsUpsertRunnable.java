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

/**
 *
 */
package com.unidata.mdm.backend.service.data.etalon;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unidata.mdm.backend.common.context.UpsertRequestContext;

/**
 * @author Mikhail Mikhailov
 * Calculate etalons runnable.
 */
public class EtalonsUpsertRunnable implements Runnable {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EtalonsUpsertRunnable.class);
    /**
     * The context to process.
     */
    private final UpsertRequestContext context;
    /**
     * The processor.
     */
    private final EtalonRecordsComponent component;
    /**
     * MDC context map.
     */
    private final Map<String, String> mdc;
    /**
     * Security context.
     */
    private final Authentication authentication;

    /**
     * Constructor.
     */
    public EtalonsUpsertRunnable(final UpsertRequestContext context, EtalonRecordsComponent component) {
        super();
        this.context = context;
        this.component = component;

        mdc = MDC.getCopyOfContextMap();
        authentication = SecurityContextHolder.getContext().getAuthentication();

    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        if (mdc != null) {
            MDC.setContextMap(mdc);
        }
        SecurityContext sctx = SecurityContextHolder.createEmptyContext();
        sctx.setAuthentication(authentication);
        SecurityContextHolder.setContext(sctx);

        try {
            component.upsertEtalon(context);
        } catch (Throwable t) {
            LOGGER.warn("Caught throwable while calculation etalon timeline.", t);
        }
    }
}

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

package com.unidata.mdm.backend.dao.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.unidata.mdm.backend.jdbc.UnidataJdbcTemplate;
import com.unidata.mdm.backend.util.MDCUtils;

/**
 * @author Michael Yashin. Created on 31.03.2015.
 * NOTE: this is put offline for now, but may be used again in the future.
 */
public class TransactionContextUtils {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionContextUtils.class);
    /**
     * Constructor.
     */
    private TransactionContextUtils() {
        super();
    }
    /**
     * Sets additional information to transaction context.
     * @param jdbcTemplate the template
     */
    public static void setTransactionUserContext(JdbcOperations jdbcTemplate) {

        try {
            if (!TransactionSynchronizationManager.isActualTransactionActive()) {
                LOGGER.debug("Transaction not active => skipping transaction user context");
                return;
            }
            if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                LOGGER.debug("Transaction read-only => skipping transaction user context");
                return;
            }
            SecurityContext context = SecurityContextHolder.getContext();
            if (context == null) {
                LOGGER.debug("Security context NOT set => can't set user name in transaction context");
                return;
            }
            Authentication auth = context.getAuthentication();
            if (auth == null) {
                LOGGER.debug("Authentication NOT set in security context => can't set user name in transaction context");
                return;
            }
            LOGGER.debug("SET LOCAL: egais.userlogin = {}, egais.request_uuid = {}", auth.getName(), MDCUtils.getRequestUuid());
            if (jdbcTemplate instanceof UnidataJdbcTemplate) {
                ((UnidataJdbcTemplate)jdbcTemplate).updateNoContext("set local egais.request_uuid = '" + MDCUtils.getRequestUuid() + "'");
                ((UnidataJdbcTemplate)jdbcTemplate).updateNoContext("set local egais.userlogin = '" + auth.getName() + "'");
            } else {
                jdbcTemplate.update("set local egais.request_uuid = '" + MDCUtils.getRequestUuid() + "'");
                jdbcTemplate.update("set local egais.userlogin = '" + auth.getName() + "'");
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to set transaction context: " + ex.getMessage(), ex);
        }
    }
}

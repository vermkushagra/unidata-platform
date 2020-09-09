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

package com.unidata.mdm.backend.service.job;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.service.security.utils.SecurityConstants;

public class AuthAdjuster implements StepExecutionListener {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAdjuster.class);
    /**
     * The name of the user, who started the job.
     */
    private String userName;
    /**
     * The token of the user, who started the job.
     */
    private String userToken;
    /**
     * The security service.
     */
    @Autowired
    private SecurityService securityService;

    @Override
    public void beforeStep(StepExecution stepExecution) {

        // Set authentication for all import actions, started by this thread/partition.
        if (Objects.nonNull(userName) && !SecurityConstants.SYSTEM_USER_NAME.equals(userName) && Objects.nonNull(userToken)) {

            boolean authenticated = securityService.authenticate(userToken, true);
            LOGGER.info("Initiator user [{}] {} authenticated.", userName, authenticated ? "successfully" : "could NOT be");
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        SecurityContextHolder.getContext().setAuthentication(null);
        return stepExecution.getExitStatus();
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @param userToken the userToken to set
     */
    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}

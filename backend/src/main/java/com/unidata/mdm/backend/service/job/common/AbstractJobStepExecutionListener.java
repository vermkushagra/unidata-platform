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

package com.unidata.mdm.backend.service.job.common;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.service.SecurityService;
import com.unidata.mdm.backend.service.job.JobCommonParameters;
import com.unidata.mdm.backend.service.security.utils.SecurityConstants;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Mikhail Mikhailov
 * Various utilities actually.
 */
public abstract class AbstractJobStepExecutionListener implements StepExecutionListener {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJobStepExecutionListener.class);
    /**
     * Run id.
     */
    @Value("#{jobParameters[" + JobCommonParameters.PARAM_RUN_ID + "]}")
    protected String runId;
    /**
     * The name of the user, who started the job.
     */
    @Value("#{jobParameters[" + JobCommonParameters.PARAM_USER_NAME + "]}")
    protected String userName;
    /**
     * The token of the user, who started the job.
     */
    @Value("#{jobParameters[" + JobCommonParameters.PARAM_USER_TOKEN + "]}")
    protected String userToken;
    /**
     * The context name.
     */
    protected MeasurementContextName contextName;
    /**
     * The security service.
     */
    @Autowired
    protected SecurityService securityService;
    /**
     * Constructor.
     */
    public AbstractJobStepExecutionListener() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {

        if (Objects.nonNull(contextName)) {
            MeasurementPoint.init(contextName);
            MeasurementPoint.start();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        if (Objects.nonNull(contextName)) {
            MeasurementPoint.stop();
        }

        return stepExecution.getExitStatus();
    }
    /**
     * Authenticate, if token supplied.
     */
    protected void authenticateIfNeeded() {

        // Set authentication for all import actions, started by this thread/partition.
        if (Objects.nonNull(userName) && !SecurityConstants.SYSTEM_USER_NAME.equals(userName) && Objects.nonNull(userToken)) {
            boolean authenticated = securityService.authenticate(userToken, true);
            LOGGER.info("Initiator user [{}] {} authenticated.", userName, authenticated ? "successfully" : "could NOT be");
        }
    }

    /**
     * clear authentication
     */
    protected void clearAuthentication() {

        if (Objects.nonNull(SecurityContextHolder.getContext())) {
            SecurityContextHolder.getContext().setAuthentication(null);
        }
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
    /**
     * Sets the name of the context.
     * @param contextName the name of the measurement context
     */
    public void setContextName(MeasurementContextName contextName) {
        this.contextName = contextName;
    }
}

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

package com.unidata.mdm.backend.service.wf;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Denis Kostovarov
 */
public class EmailEventListener implements ActivitiEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailEventListener.class);

    @Override
    public void onEvent(ActivitiEvent event) {

        if (event.getType() == ActivitiEventType.JOB_EXECUTION_FAILURE) {
            LOGGER.warn("Failed to send email notification for processInstanceId " + event.getProcessInstanceId());
        }

        if (event.getType() == ActivitiEventType.JOB_EXECUTION_SUCCESS) {
            LOGGER.debug("Send notification succeeded for processInstanceId " + event.getProcessInstanceId());
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}

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

package com.unidata.mdm.backend.api.rest.maintanance;

import java.io.IOException;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.maintenance.MaintenanceService;
import com.unidata.mdm.backend.service.maintenance.dto.SystemMode;

/**
 * If system in maintenance mode
 * intercepter forbids all requests.
 * @author ilya.bykov
 */
public class MaintenanceCxfInterceptor extends AbstractPhaseInterceptor<Message> implements AfterContextRefresh {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceCxfInterceptor.class);

	/** The maintenance service. */
	@Autowired
	private MaintenanceService maintenanceService;

	/**
	 * Instantiates a new maintenance cxf intercepter.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public MaintenanceCxfInterceptor() throws IOException {
		super(Phase.RECEIVE);
		LOGGER.info("Register maintenance interceptor {}", MaintenanceCxfInterceptor.class.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.
	 * message.Message)
	 */
	@Override
	public void handleMessage(Message message) throws Fault {
		// TODO: Currently all request are forbidden, maybe its excessively and
		// we can allow some of them (security, statistic, etc)

        // Ignore maintenance check for requests to get WSDL document.
        if (message instanceof SoapMessage && "wsdl".equalsIgnoreCase((String) message.get(Message.QUERY_STRING))) {
            return;
        }

		SystemMode currentMode = maintenanceService.checkCurrent();
		if (currentMode.getModeEnum() == SystemMode.ModeEnum.MAINTENANCE) {
			LOGGER.warn("System in maintenance mode, request denied! Reason: {}", currentMode.getMessage());
			throw new BusinessException(currentMode.getMessage(), ExceptionId.EX_MAINTENANCE, currentMode.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.service.configuration.AfterContextRefresh#
	 * afterContextRefresh()
	 */
	@Override
	public void afterContextRefresh() {
		// TODO Auto-generated method stub

	}
}
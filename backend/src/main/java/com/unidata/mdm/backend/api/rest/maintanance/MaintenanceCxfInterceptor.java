package com.unidata.mdm.backend.api.rest.maintanance;

import java.io.IOException;

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
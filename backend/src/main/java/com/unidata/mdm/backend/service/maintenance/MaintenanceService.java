package com.unidata.mdm.backend.service.maintenance;

import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;
import com.unidata.mdm.backend.service.maintenance.dto.SystemMode;

/**
 * At the moment(08.02.2017) service provide functionality for:
 * <ul>
 * <li>Transfer system to provided mode</li>
 * <li>Check current mode</li>
 * </ul>
 *
 * @author ilya.bykov
 * @see MaintenanceCxfInterceptor
 */
public interface MaintenanceService extends AfterContextRefresh {

	/**
	 * transfer system to provided mode.
	 */
	void transferTo(SystemMode mode);

	/**
	 * Return current system mode.
	 *
	 * @return Current mode.
	 */
	SystemMode checkCurrent();
}

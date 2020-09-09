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

package com.unidata.mdm.backend.service.maintenance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.unidata.mdm.backend.service.maintenance.dto.SystemMode;
import com.unidata.mdm.backend.service.maintenance.dto.SystemMode.ModeEnum;

/**
 * The Class MaintenanceServiceImpl.
 * 
 * @author ilya.bykov
 */
@Component
public class MaintenanceServiceImpl implements MaintenanceService {

	/** The sys status topic. */
	private static String SYS_STATUS_TOPIC = "sysStatusTopic";
	/** The hazelcast instance. */
	@Autowired
	private HazelcastInstance hazelcastInstance;
	/** The topic. */
	private ITopic<SystemMode> topic = null;

	/** The current mode. */
	private SystemMode currentMode;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.service.maintenance.MaintenanceService#
	 * transferToMode(com.unidata.mdm.backend.service.maintenance.dto.
	 * SystemMode)
	 */
	@Override
	public void transferTo(SystemMode mode) {
		topic.publish(mode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.unidata.mdm.backend.service.maintenance.MaintenanceService#checkMode(
	 * )
	 */
	@Override
	public SystemMode checkCurrent() {
		if (this.currentMode == null) {
			this.currentMode = new SystemMode().withModeEnum(ModeEnum.NORMAL);
		}

		return this.currentMode;
	}

	/**
	 * Sets the current mode.
	 *
	 * @param currentMode
	 *            the new current mode
	 */
	private void setCurrentMode(SystemMode currentMode) {
		this.currentMode = currentMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.unidata.mdm.backend.service.configuration.AfterContextRefresh#
	 * afterContextRefresh()
	 */
	@Override
	public void afterContextRefresh() {
		topic = hazelcastInstance.getTopic(SYS_STATUS_TOPIC);
		topic.addMessageListener(message -> setCurrentMode(message.getMessageObject()));

	}
}

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

package com.unidata.mdm.backend.service.cleanse;

import java.io.IOException;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * The listener interface for receiving CF events. The class that is interested
 * in processing a CF event implements this interface, and the object created
 * with that class is registered with a component using the component's
 * <code>addCFListener<code> method. When the CF event occurs, that object's
 * appropriate method is invoked.
 *
 * @see CFEvent
 */
public class CFListener implements MessageListener<String> {

	/** The cleanse function service. */
	private CleanseFunctionServiceExt cleanseFunctionService;
	private String localMemberUUID;

	/**
	 * Instantiates a new CF listener.
	 *
	 * @param cleanseFunctionService
	 *            the cleanse function service
	 * @param localMemberUUID 
	 */
	public CFListener(CleanseFunctionServiceExt cleanseFunctionService, String localMemberUUID) {
		this.cleanseFunctionService = cleanseFunctionService;
		this.localMemberUUID = localMemberUUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
	 */
	@Override
	public void onMessage(Message<String> message) {
		try {
			if(message.getPublishingMember().getUuid().equals(localMemberUUID)) {
				return;
			}
			this.cleanseFunctionService.loadAndInit(message.getMessageObject());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

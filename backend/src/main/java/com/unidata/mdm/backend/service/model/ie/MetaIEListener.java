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

package com.unidata.mdm.backend.service.model.ie;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;


/**
 * The listener interface for receiving metaIE events. The class that is
 * interested in processing a metaIE event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addMetaIEListener<code> method. When the metaIE event
 * occurs, that object's appropriate method is invoked.
 *
 * @see MetaIEEvent
 */
public class MetaIEListener implements MessageListener<MetaGraph> {

	/** The meta IE service. */
	private MetaIEService metaIEService;

	/** The local member UUID. */
	private String localMemberUUID;

	/**
	 * Instantiates a new meta IE listener.
	 *
	 * @param metaIEService
	 *            the meta IE service
	 * @param localMemberUUID
	 *            the local member UUID
	 */
	public MetaIEListener(MetaIEService metaIEService, String localMemberUUID) {
		this.metaIEService = metaIEService;
		this.localMemberUUID = localMemberUUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
	 */
	@Override
	public void onMessage(Message<MetaGraph> message) {
		if (message.getPublishingMember().getUuid().equals(localMemberUUID)) {
			return;
		}
		this.metaIEService.importMetaZip(message.getMessageObject());

	}

}
